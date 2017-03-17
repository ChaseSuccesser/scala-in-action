package com.ligx.dao

import com.typesafe.config.ConfigFactory

/**
  * Author: ligongxing.
  * Date: 2017年03月16日.
  */
object SlickReadWriteTemplate {
  private val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db")
}

class SlickReadWriteTemplate {
//  val db = Database.forURI(dbConfig.getString("url"), dbConfig.getString("username"),
  // dbConfig.getString("password"), dbConfig.getString("driver"))
}
