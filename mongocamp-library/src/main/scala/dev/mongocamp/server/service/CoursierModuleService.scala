package dev.mongocamp.server.service

import better.files.File
import com.typesafe.scalalogging.LazyLogging
import com.vdurmont.semver4j.Semver
import coursier._
import coursier.core.Configuration
import coursier.params.ResolutionParams
import coursier.parse.DependencyParser
import dev.mongocamp.server.config.DefaultConfigurations
import dev.mongocamp.server.library.BuildInfo

object CoursierModuleService extends LazyLogging {

  private lazy val resolutionParams: ResolutionParams = {
    val serverExclusion = Set("2.12", "2.13").map(baseScala => (org"dev.mongocamp", ModuleName(s"mongocamp-server_$baseScala")))
    ResolutionParams()
      .withScalaVersion(BuildInfo.scalaVersion)
      .withExclusions(serverExclusion)
      .addExclusions((org"org.scala-lang", ModuleName("scala-library")))
  }

  private lazy val defaultRepositories: List[Repository] = {
    List(
      Repositories.central,
      Repositories.jcenter,
      Repositories.jitpack,
      Repositories.google,
      Repositories.sonatype("releases"),
      Repositories.sonatype("snapshots"),
      Repositories.centralGcs,
      Repositories.centralGcsAsia,
      Repositories.centralGcsEu,
      Repositories.clojars
    )
  }

  def loadMavenConfiguredDependencies(): List[File] = {
    val modules = ConfigurationRead.noPublishReader.getConfigValue[List[String]](DefaultConfigurations.ConfigKeyPluginsModules)
    loadMavenConfiguredDependencies(modules)
  }

  def loadMavenConfiguredDependencies(dependencyStrings: List[String]): List[File] = {
    val dependencies: List[Dependency] = dependencyStrings.map(s =>
      DependencyParser
        .dependency(s, scala.util.Properties.versionNumberString, Configuration.empty)
        .getOrElse(throw new Exception(s"$s is not a right configured maven dependency"))
    )
    fetchMavenDependencies(dependencies, Some(resolutionParams), true)
  }

  def loadServerWithAllDependencies(): List[File] = {
    val scalaVersion = new Semver(BuildInfo.scalaVersion)
    val scalaShort   = s"${scalaVersion.getMajor}.${scalaVersion.getMinor}"
    val dependency   = Dependency(Module(Organization(BuildInfo.organization), ModuleName(s"mongocamp-server_$scalaShort")), BuildInfo.version)
    CoursierModuleService.fetchMavenDependencies(List(dependency), None, false)
  }

  private def fetchMavenDependencies(dependencies: List[Dependency], resolutionParams: Option[ResolutionParams], useCustomMavenRepos: Boolean): List[File] = {
    try {
      var fetchCommand = Fetch().withDependencies(dependencies).addRepositories(defaultRepositories: _*)

      resolutionParams.foreach(params => fetchCommand = fetchCommand.withResolutionParams(params))

      if (useCustomMavenRepos) {
        val mvnRepository: List[coursier.MavenRepository] = getConfiguredMavenRepositories
        fetchCommand = fetchCommand.addRepositories(mvnRepository: _*)
      }

      val resolution = fetchCommand.run()
      resolution.toList.map(jFile => File(jFile.toURI))
    }
    catch {
      case _: Exception =>
        fetchMavenDependencies(checkMavenDependencies(dependencies), resolutionParams, useCustomMavenRepos)
    }
  }

  private def getConfiguredMavenRepositories: List[MavenRepository] = {
    val configRead = ConfigurationRead.noPublishReader
    val mvnRepository: List[MavenRepository] = configRead
      .getConfigValue[List[String]](DefaultConfigurations.ConfigKeyPluginsMavenRepositories)
      .map(string => dev.mongocamp.server.plugin.coursier.Repository.mvn(string))
    mvnRepository
  }

  private def checkMavenDependencies(dependencies: List[Dependency]): List[Dependency] = {
    val mvnRepository: List[MavenRepository] = getConfiguredMavenRepositories
    dependencies.filter(dependency => {
      try {
        val resolution = Fetch()
          .addDependencies(dependency)
          .addRepositories(mvnRepository: _*)
          .addRepositories(defaultRepositories: _*)
          .withResolutionParams(resolutionParams)
          .run()
        resolution.nonEmpty
      }
      catch {
        case e: Exception =>
          logger.error(e.getMessage, e)
          false
      }
    })

  }
}
