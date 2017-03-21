package com.ligx

import java.util.Date
import java.util.concurrent.TimeUnit

import com.ligx.dao.SlickReadWriteTemplate
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Author: ligongxing.
  * Date: 2017年03月17日.
  */
class DbTest extends FlatSpec with Matchers {

  /*
  CREATE TABLE `slick_test` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `time` bigint(36) DEFAULT NULL,
  `age` int(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
   */

  "insert" should "success" in {
    val template = new SlickReadWriteTemplate

    val time = new Date().getTime

    val future = template.insert(s"insert into slick_test(name, time, age) values('ligx', $time, 24)")

    val result = Await.result(future, Duration(3, TimeUnit.SECONDS))
    println(s"result: $result")
  }

  "select" should "success" in {
    val template = new SlickReadWriteTemplate

    implicit val slickDtoResult = GetResult(r => )

    val future = template.select()

    val result = Await.result(future, Duration(2, TimeUnit.SECONDS))

    result.foreach(println _)
  }
}
