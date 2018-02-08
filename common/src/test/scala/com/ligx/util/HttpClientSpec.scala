package com.ligx.util

import org.scalatest.{FlatSpec, Matchers}


/**
  * Author: ligongxing.
  * Date: 2017年07月04日.
  */
class HttpClientSpec extends FlatSpec with Matchers {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher
  implicit val http = Http()

  "get" should "" in {
    HttpClient.get("https://github.com/ChaseSuccesser")
      .run
      .map {
        case r@SimpleHttpResponse(_, _, _, _, _) =>
          val bodyStr = r.bodyAsString
          println(bodyStr)
      }

    Thread.sleep(3000)
  }
}
