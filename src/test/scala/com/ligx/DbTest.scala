package com.ligx

import java.util.Date
import java.util.concurrent.TimeUnit

import com.ligx.dao.{SlickDto, SlickReadWriteTemplate}
import org.scalatest.{FlatSpec, Matchers}
import slick.jdbc.GetResult

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Author: ligongxing.
  * Date: 2017年03月17日.
  */
class DbTest extends FlatSpec with Matchers {

  "insert" should "success" in {
    val template = new SlickReadWriteTemplate

    val time = new Date().getTime

    val future = template.insert(s"insert into slick_test(name, time, age) values('ligx', $time, 24)")

    val result = Await.result(future, Duration(3, TimeUnit.SECONDS))
    println(s"result: $result")
  }

  "select for case class instance" should "success" in {
    val template = new SlickReadWriteTemplate

    implicit val slickDtoResult = GetResult(r => SlickDto(r.<<, r.<<, r.<<, r.<<))
    val sql = "SELECT * FROM slick_test"
    val future = template select sql

    val result = Await.result(future, Duration(2, TimeUnit.SECONDS))
    result.foreach {
      case SlickDto(a, b, c, d) => println(s"SlickDto($a, $b, $c, $d)")
    }
  }

  "select for tuple" should "success" in {
    val template = new SlickReadWriteTemplate

    implicit val getResult = implicitly[GetResult[(Int, String, Long, Int)]]
    val sql = "select * from slick_test"
    val future = template select sql

    val result = Await.result(future, Duration(3, TimeUnit.SECONDS))

    result foreach {
      case (a, b, c, d) => println(s"SlickDto($a, $b, $c, $d)")
    }
  }
}