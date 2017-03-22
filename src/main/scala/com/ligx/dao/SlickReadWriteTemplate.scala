package com.ligx.dao

import slick.jdbc.DB2Profile.api._
import slick.jdbc.GetResult

import scala.concurrent.Future

/**
  * Author: ligongxing.
  * Date: 2017年03月16日.
  */
class SlickReadWriteTemplate extends Db {

  def insert(sql: String): Future[Int] = {
    val insertAction: DBIO[Int] = sqlu"#$sql"
    db.run(insertAction)
  }

  def select: Future[Seq[_]] = {
    val selectAction = sql"SELECT * FROM slick_test".as[(Int, String, Long, Int)]
    db.run(selectAction)
  }

  def selectForObject[A: GetResult]: Future[Seq[_]] = {
    val selectAction = sql"SELECT * FROM slick_test".as[A]
    db.run(selectAction)
  }
}
