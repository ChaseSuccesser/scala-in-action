package com.ligx.util

import org.scalatest.{FlatSpec, Matchers}

/**
  * Author: ligongxing.
  * Date: 2017年12月04日.
  */
class HttpUtilSpec extends FlatSpec with Matchers  {

  "http get" should "" in {
    println(HttpUtil.get("aaa"))
  }
}
