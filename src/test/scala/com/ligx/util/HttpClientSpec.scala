package com.ligx.util

import org.scalatest.{FlatSpec, Matchers}

/**
  * Author: ligongxing.
  * Date: 2017年07月04日.
  */
class HttpClientSpec extends FlatSpec with Matchers {

  "http get" should "" in {
    val httpClient = new HttpClient

    val response: String = httpClient.get("http://www.baidu.com")

    println(response)
  }
}
