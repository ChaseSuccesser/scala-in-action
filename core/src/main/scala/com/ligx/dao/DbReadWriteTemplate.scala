package com.ligx.dao

import slick.jdbc.DB2Profile.api._
import slick.dbio.DBIO
import slick.jdbc.GetResult

import scala.concurrent.Future

object DbReadWriteTemplate extends Db{

  def insert(sql: String): Future[Int] = {
    val insertAction: DBIO[Int] = sqlu"#$sql"
    db.run(insertAction)
  }

  def select[A: GetResult](sql: String): Future[Seq[A]] = {
    val selectAction = sql"#$sql".as[A]
    db.run(selectAction)
  }
}
