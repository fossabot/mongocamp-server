package dev.mongocamp.server.service

import org.reflections.Reflections
import org.reflections.util.{ ClasspathHelper, ConfigurationBuilder }

import java.net.URL
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._
import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader
import scala.reflect.runtime.universe.runtimeMirror
import scala.util.Try

object ReflectionService {
  private val reflectionConfigurationBuilder: ConfigurationBuilder = new ConfigurationBuilder().forPackages("")

  def instancesForType[T <: Any](clazz: Class[T]): List[T] = {
    val reflected = getSubClassesList(clazz).flatMap(foundClazz => {
      lazy val mirror   = runtimeMirror(foundClazz.getClassLoader)
      val instance      = Try(mirror.reflectModule(mirror.moduleSymbol(foundClazz)).instance).toOption
      val clazzInstance = Try(foundClazz.getDeclaredConstructor().newInstance()).toOption
      if (clazzInstance.isDefined) {
        clazzInstance.map(_.asInstanceOf[T])
      }
      else {
        instance.map(_.asInstanceOf[T])
      }
    })
    reflected
  }

  def getSubClassesList[T <: Any](clazz: Class[T]): List[Class[_ <: T]] = {
    val urls: ArrayBuffer[URL] = ArrayBuffer[URL]()
    urls.++=(ClasspathHelper.forJavaClassPath().asScala)
    Option(reflectionConfigurationBuilder.getClassLoaders).foreach(_.foreach(classLoader => {
      classLoader match {
        case loader: URLClassLoader => urls.++=(loader.getURLs)
        case _                      => classLoader.getDefinedPackages.foreach(p => urls.++=(ClasspathHelper.forPackage(p.toString, classLoader).asScala))
      }
      ""
    }))
    reflectionConfigurationBuilder.addUrls(urls.asJava)
    val reflected = new Reflections(reflectionConfigurationBuilder)
      .getSubTypesOf(clazz)
      .asScala

    reflected.toList
  }

  def registerClassLoaders[T <: Any](clazz: Class[T]): Unit = {
    val classLoader = clazz.getClassLoader
    registerClassLoaders(classLoader)
  }

  def registerClassLoaders[T <: Any](classLoader: ClassLoader): Unit = {
    reflectionConfigurationBuilder.addClassLoaders(classLoader)
    if (classLoader.getParent != null) {
      registerClassLoaders(classLoader.getParent)
    }
  }

  def getClassListByName(className: String): List[Class[_]] = {
    val arrayBuffer = ArrayBuffer[Class[_]]()
    reflectionConfigurationBuilder.getClassLoaders.foreach(cl =>
      Try {
        val clazz = cl.loadClass(className)
        arrayBuffer.+=(clazz)
      }
    )
    arrayBuffer.toList
  }

  def getClassByName(className: String): Class[_] = {
    val classList = getClassListByName(className)
    classList.headOption.getOrElse(throw new ClassNotFoundException())
  }

}
