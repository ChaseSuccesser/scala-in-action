package com.ligx.crawler

import java.util.concurrent.TimeUnit

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Author: ligongxing.
  * Date: 2018年02月08日.
  */
class MovieStorageSpec extends FlatSpec with Matchers{

  "saveMovie" should "" in {
    val movieList = List(
      AvMovie("test", "test_download_url", "test_image_url", "2333"),
      AvMovie("test", "test_download_url", "test_image_url", "2333"),
      AvMovie("test", "test_download_url", "test_image_url", "2333"),
      AvMovie("test", "test_download_url", "test_image_url", "2333")
    )

    val future: Future[Array[Int]] =  MovieStorage.saveMovie(movieList)

    val result = Await.result(future, Duration(2, TimeUnit.SECONDS))
    println(s"result = ${result.sum}")
  }
}
