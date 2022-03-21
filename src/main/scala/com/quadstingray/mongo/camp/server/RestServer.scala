package com.quadstingray.mongo.camp.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.`Access-Control-Allow-Methods`
import akka.http.scaladsl.model.{ HttpHeader, HttpResponse, StatusCodes }
import akka.http.scaladsl.server.Directives.{ complete, extractRequestContext, options }
import akka.http.scaladsl.server.{ Route, RouteConcatenation }
import com.quadstingray.mongo.camp.auth.AuthHolder
import com.quadstingray.mongo.camp.config.{ Config, GlobalConstants }
import com.quadstingray.mongo.camp.interceptor.cors.Cors
import com.quadstingray.mongo.camp.interceptor.cors.Cors.{ KeyCorsHeaderOrigin, KeyCorsHeaderReferer }
import com.quadstingray.mongo.camp.routes.docs.ApiDocsRoutes
import com.quadstingray.mongo.camp.service.ReflectionService
import com.typesafe.scalalogging.LazyLogging
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ ExecutionContext, Future }

trait RestServer extends LazyLogging with RouteConcatenation with Config {

  implicit val actorSystem: ActorSystem = GlobalConstants.actorSystem

  // init server parameter
  val interface: String = globalConfigString("server.interface", "127.0.0.1")
  val port: Int         = globalConfigInt("server.port", 3333)

  val serverEndpoints: List[ServerEndpoint[AkkaStreams with WebSockets, Future]]

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
          headers += `Access-Control-Allow-Methods`(Seq(OPTIONS, POST, PUT, PATCH, GET, DELETE))
          HttpResponse(StatusCodes.OK).withHeaders(headers.toList)
        })
      }
    }
  }

  def routeHandler(r: Route): Route = {
    preflightRequestHandler ~ r
  }

  def startServer()(implicit ex: ExecutionContext): Future[Unit] = {
    ReflectionService.loadPlugins()
    ReflectionService.registerClassLoaders(getClass)

    Http()
      .newServerAt(interface, port)
      .bindFlow(routeHandler(routes))
      .map(serverBinding => {
        logger.warn("init server with interface: %s at port: %s".format(interface, port))

        if (ApiDocsRoutes.isSwaggerEnabled) {
          println("For Swagger go to: http://%s:%s/docs".format(interface, port))
        }

        AuthHolder.handler

        serverBinding
      })
  }

}
