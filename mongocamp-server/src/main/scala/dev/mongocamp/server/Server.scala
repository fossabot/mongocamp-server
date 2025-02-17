package dev.mongocamp.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{ HttpHeader, HttpResponse, StatusCodes }
import akka.http.scaladsl.server.Directives.{ complete, extractRequestContext, options }
import akka.http.scaladsl.server.{ Route, RouteConcatenation }
import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.server.auth.AuthHolder
import dev.mongocamp.server.config.DefaultConfigurations
import dev.mongocamp.server.event.EventSystem
import dev.mongocamp.server.event.server.{ PluginLoadedEvent, ServerStartedEvent }
import dev.mongocamp.server.interceptor.cors.Cors
import dev.mongocamp.server.interceptor.cors.Cors.{ KeyCorsHeaderOrigin, KeyCorsHeaderReferer }
import dev.mongocamp.server.plugin.{ RoutesPlugin, ServerPlugin }
import dev.mongocamp.server.route._
import dev.mongocamp.server.route.docs.ApiDocsRoutes
import dev.mongocamp.server.service.{ ConfigurationService, PluginDownloadService, PluginService, ReflectionService }
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ ExecutionContext, Future }

object Server extends App with LazyLogging with RouteConcatenation with RestServer {

  implicit lazy val actorSystem: ActorSystem = ActorHandler.requestActorSystem
  implicit lazy val ex: ExecutionContext     = ActorHandler.requestExecutionContext

  lazy val interface: String = ConfigurationService.getConfigValue[String](DefaultConfigurations.ConfigKeyServerInterface)
  lazy val port: Int         = ConfigurationService.getConfigValue[Long](DefaultConfigurations.ConfigKeyServerPort).toInt

  lazy val listOfRoutePlugins: List[RoutesPlugin] = {
    ReflectionService
      .instancesForType(classOf[RoutesPlugin])
      .filterNot(plugin => ConfigurationService.getConfigValue[List[String]](DefaultConfigurations.ConfigKeyPluginsIgnored).contains(plugin.getClass.getName))
      .map(plugin => {
        EventSystem.eventStream.publish(PluginLoadedEvent(plugin.getClass.getName, "RoutesPlugin"))
        plugin
      })
  }

  lazy val serverEndpoints: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] =
    InformationRoutes.routes ++ AuthRoutes.authEndpoints ++ AdminRoutes.endpoints ++ listOfRoutePlugins.flatMap(_.endpoints) ++ IndexRoutes.endpoints

  ConfigurationService.registerMongoCampServerDefaultConfigs()

  def routes(implicit ex: ExecutionContext): Route = {
    val internalEndPoints = serverEndpoints ++ ApiDocsRoutes.addDocsRoutes(serverEndpoints)
    val allEndpoints      = internalEndPoints.map(ep => AkkaHttpServer.akkaHttpServerInterpreter.toRoute(ep))
    concat(allEndpoints: _*)
  }

  private def preflightRequestHandler: Route = {
    extractRequestContext { ctx =>
      options {
        complete({
          val requestHeaders = ctx.request.headers
          val originHeader   = requestHeaders.find(_.is(KeyCorsHeaderOrigin.toLowerCase())).map(_.value())
          val refererHeader = requestHeaders
            .find(_.is(KeyCorsHeaderReferer.toLowerCase()))
            .map(_.value())
            .map(string => if (string.endsWith("/")) string.replaceAll("/$", "") else string)

          val corsHeaders                      = Cors.corsHeadersFromOrigin((originHeader ++ refererHeader).headOption)
          val headers: ArrayBuffer[HttpHeader] = ArrayBuffer()
          corsHeaders.foreach(header => headers += HttpHeader.parse(header.name, header.value).asInstanceOf[ParsingResult.Ok].header)
          headers += akka.http.scaladsl.model.headers.`Access-Control-Allow-Methods`(Seq(OPTIONS, POST, PUT, PATCH, GET, DELETE))
          HttpResponse(StatusCodes.OK).withHeaders(headers.toList)
        })
      }
    }
  }

  def routeHandler(r: Route): Route = {
    preflightRequestHandler ~ r
  }

  private def activateServerPlugins(): Unit = {
    ReflectionService
      .instancesForType(classOf[ServerPlugin])
      .filterNot(plugin => ConfigurationService.getConfigValue[List[String]](DefaultConfigurations.ConfigKeyPluginsIgnored).contains(plugin.getClass.getName))
      .map(plugin => {
        plugin.activate()
        EventSystem.eventStream.publish(PluginLoadedEvent(plugin.getClass.getName, "ServerPlugin"))
        plugin
      })
  }

  def startServer()(implicit ex: ExecutionContext): Future[Unit] = {
    val pluginDownloadService = new PluginDownloadService()
    pluginDownloadService.downloadPlugins()
    val pluginService = new PluginService()
    pluginService.loadPlugins()
    ReflectionService.registerClassLoaders(getClass)
    doBeforeServerStartUp()
    Http()
      .newServerAt(interface, port)
      .bindFlow(routeHandler(routes))
      .map(serverBinding => {
        AuthHolder.handler

        logger.warn("init server with interface: %s at port: %s".format(interface, port))

        if (ApiDocsRoutes.isSwaggerEnabled) {
          println("For Swagger go to: http://%s:%s/docs".format(interface, port))
        }

        EventSystem.eventStream.publish(ServerStartedEvent())
        doAfterServerStartUp()
        serverBinding
      })
  }

  def doBeforeServerStartUp(): Unit = {
    activateServerPlugins()
  }

  private lazy val afterServerStartCallBacks: ArrayBuffer[() => Unit] = ArrayBuffer()

  def registerAfterStartCallBack(f: () => Unit): Unit = {
    afterServerStartCallBacks.addOne(f)
  }

  def doAfterServerStartUp(): Unit = {
    afterServerStartCallBacks.foreach(f => f())
  }

  startServer()

  override def registerMongoCampServerDefaultConfigs: Unit = ConfigurationService.registerMongoCampServerDefaultConfigs()
}
