package com.ligx.db

import java.util.Date
import java.util.concurrent.TimeUnit

import org.scalatest.{FlatSpec, Matchers}
import slick.jdbc.GetResult

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Author: ligongxing.
  * Date: 2017年03月17日.
  */
class DBSpec extends FlatSpec with Matchers {

  "insert" should "success" in {
    val time = new Date().getTime

    val future = DbReadWriteTemplate.insert(s"insert into slick_test(name, time, age) values('ligx', $time, 24)")

    val result = Await.result(future, Duration(3, TimeUnit.SECONDS))
    println(s"result: $result")
  }

  "select for case class instance" should "success" in {
    implicit val slickDtoResult: GetResult[SlickDto] = GetResult(r => SlickDto(r.<<, r.<<, r.<<, r.<<))

    val sql = "SELECT * FROM slick_test"
    val future = DbReadWriteTemplate select sql

    val result = Await.result(future, Duration(2, TimeUnit.SECONDS))
    result.foreach {
      case SlickDto(a, b, c, d) => println(s"SlickDto($a, $b, $c, $d)")
    }
  }

  "select for tuple" should "success" in {
    implicit val getResult = implicitly[GetResult[(Int, String, Long, Int)]]

    val sql = "select * from slick_test"
    val future = DbReadWriteTemplate select sql

    val result = Await.result(future, Duration(3, TimeUnit.SECONDS))
    result foreach {
      case (a, b, c, d) => println(s"SlickDto($a, $b, $c, $d)")
    }
  }

  "test batchInsert" should "" in {
    val time = new Date().getTime

    val sql1 = s"insert into slick_test(name, time, age) values('ligx', $time, 24)"
    val sql2 = s"insert into slick_test(name, time, age) values('ligx', $time, 24)"

    val sqlList = List(sql1, sql2)

    val future = DbReadWriteTemplate.batchInsert(sqlList)

    val result = Await.result(future, Duration(2, TimeUnit.SECONDS))
    result.foreach(println)
  }
}

case class SlickDto(id: Int, name: String, time: Long, age: Int)