package com.ligx.crawler

import scala.util.{Failure, Success}

/**
  * Author: ligongxing.
  * Date: 2018年12月15日.
  */
object AvGirlBrowser {

  def main(args: Array[String]): Unit = {
    val avGirlsFuture = AvStorage.queryAvGirls()

    avGirlsFuture onComplete {
      case Success(avGirls) =>
      case Failure(e) => e.printStackTrace()
    }
  }



}
