package com.ligx.dao

import com.typesafe.config.ConfigFactory
import slick.backend.DatabaseConfig

/**
  * Author: ligongxing.
  * Date: 2017年03月16日.
  */
object SlickReadWriteTemplate extends App {
  private val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db")
  println(dbConfig.getString("url"))
}

class SlickReadWriteTemplate {
//  val db = Database.forURI(dbConfig.getString("url"), dbConfig.getString("username"), dbConfig.getString("password"), dbConfig.getString("driver"))
}
