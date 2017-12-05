package com.ligx.util

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  * Author: ligongxing.
  * Date: 2017年12月04日.
  */
object HttpUtil {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def get(url: String): String = {
    HttpRequest()
    val responseFuture = Http().singleRequest(HttpRequest(uri = "http://akka.io"))

    //    responseFuture onComplete {
    //      case Success(resp) => println(resp)
    //      case Failure(e) => e.printStackTrace()
    //    }

    val response: HttpResponse = Await.result(responseFuture, Duration(5, TimeUnit.SECONDS))
    response.toString()
  }
}
