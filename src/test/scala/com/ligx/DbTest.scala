package com.ligx

import java.util.Date

import com.ligx.dao.{Db, SlickReadWriteTemplate}
import org.scalatest.{FlatSpec, Matchers}

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
    val template = new SlickReadWriteTemplate with Db

    val time = new Date().getTime
    template.insert(s"insert into slick_test values('ligx', $time, 24)")
  }
}
