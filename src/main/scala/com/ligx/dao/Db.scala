package com.ligx.dao

import slick.basic.DatabaseConfig

/**
  * Author: ligongxing.
  * Date: 2017年03月17日.
  */
trait Db {

  private val config = DatabaseConfig.forConfig("db")

  val db = config.db
}
