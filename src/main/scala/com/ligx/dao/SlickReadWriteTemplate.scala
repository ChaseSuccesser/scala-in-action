package com.ligx.dao

import slick.jdbc.DB2Profile.api._

/**
  * Author: ligongxing.
  * Date: 2017年03月16日.
  */
class SlickReadWriteTemplate {

  this: Db =>

  def insert(sql: String): DBIO[Int] = {
    sqlu"sql"
  }
}
