package com.ligx.db

import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend.Database
import slick.util.AsyncExecutor

/**
  * Author: ligongxing.
  * Date: 2017年03月17日.
  */
object Db {
  private val config = ConfigFactory.load()
  private val dbConfig = config.getConfig("db")
}

trait Db {

  import Db._

  val db = Database.forURL(
    url = dbConfig.getString("url"),
    user = dbConfig.getString("username"),
    password = dbConfig.getString("password"),
    driver = dbConfig.getString("driver"),
    executor = AsyncExecutor(name = "AsyncExecutor.Slick", numThreads = dbConfig.getInt("numThreads"), queueSize = 1000)
  )

}
