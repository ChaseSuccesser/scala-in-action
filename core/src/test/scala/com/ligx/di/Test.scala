package com.ligx.di

/**
  * Author: ligongxing.
  * Date: 2018年03月12日.
  */
object Test extends App {

  val barService = new BarService
  println(barService.bar())
}
