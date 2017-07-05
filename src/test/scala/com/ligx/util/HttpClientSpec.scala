package com.ligx.util

import org.scalatest.{FlatSpec, Matchers}


/**
  * Author: ligongxing.
  * Date: 2017年07月04日.
  */
class HttpClientSpec extends FlatSpec with Matchers {

  "http get" should "" in {
    val httpClient = new HttpClient
    val response: String = httpClient.get("http://www.baidu.com", null)
    println(response)
  }

  "http post" should "" in {
    val paramMap = Map[String, String](
      "departCode" -> "CGO",
      "arriveCode" -> "SEL",
      "forwardDate" -> "2017-06-15",
      "tripType" -> "1",
      "src" -> "mt_native"
    )
    val httpClient = new HttpClient
    val response = httpClient.get("http://apiall.hotel.test.sankuai.com", paramMap)
    println(response)
  }
}
