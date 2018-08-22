package com.ligx.restapi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol
import spray.json._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpResponse, MessageEntity, StatusCode}
import akka.http.scaladsl.server.ExceptionHandler
import com.ligx.restapi.commons.CommonResult
import com.ligx.restapi.exception.{ParamError, RestException}

import scala.collection.mutable

/**
  * Created by ligongxing on 2016/8/4.
  */
case class Person(name: String, age: Int)

object PersonJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val personJsonFormat = jsonFormat2(Person)
}

object Server extends App{

  import PersonJsonProtocol._

  implicit val system = ActorSystem("webserver")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def overwriteResultStatus(http_code: Int)(response: HttpResponse): HttpResponse = {
    response.copy(http_code)
  }

  implicit def exceptionHandler: ExceptionHandler = {
    ExceptionHandler {
      case e: RestException =>
        val restExceptionFactor = e.restExceptionFactor

        mapResponse(overwriteResultStatus(restExceptionFactor.http_code))(
          extractRequest { request =>
            val map = mutable.Map[String, Any]()
            map ++= List("error_code" -> restExceptionFactor.error_code, "request_method" -> request.method.name(), "request_uri" -> request.uri.path.toString(), "detail_msg" -> restExceptionFactor.detail_msg)
            val immutableMap = Map.empty[String, Any] ++ map
            complete(CommonResult.mapCommonResult(immutableMap))
          }
        )
    }
  }

  val route =
    pathPrefix("order") {
      (path("create_order.json") & post) {
        formFieldMap { formFieldMap =>
          def formParamsString(param: (String, String)): String = s"${param._1} = ${param._2}"
          complete(CommonResult.mapCommonResult(formFieldMap))
        }
      } ~
      (path("get_order.json") & get) {
        parameterMap { parameterMap =>
          def queryParamsString(params: (String, String)): String = s"${params._1} = ${params._2}"
          complete(s"get request parameters are: ${parameterMap.map(queryParamsString).mkString(", ")}")
        }
      } ~
      (path("ping") & get) {
        parameterMap { parameterMap =>
          complete(Marshal("pong").to[MessageEntity])
        }
      } ~
      (path("get_person_info.json") & get) {
        complete(Person("ligx", 23))
      } ~
      (path("test_exception") & get) {
        failWith(new RestException(ParamError))
      }
    }

  Http().bindAndHandle(route, interface = "localhost", port = 8888)
}
