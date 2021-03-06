package com.ligx.db

import slick.dbio.DBIO
import slick.jdbc.DB2Profile.api._
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

  def batchInsert(sqlList: List[String]): Future[Array[Int]] = {
    val batchInsert = SimpleDBIO[Array[Int]]{
      session => {
        val conn = session.connection
        conn.setAutoCommit(false)

        val st = conn.createStatement()
        sqlList.foreach(sql => st.addBatch(sql))
        val result = st.executeBatch()

        conn.commit()

        result
      }
    }

    db.run(batchInsert)
  }
}
