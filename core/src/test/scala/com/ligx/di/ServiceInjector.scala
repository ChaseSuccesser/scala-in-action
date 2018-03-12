package com.ligx.di

import com.google.inject.Guice

/**
  * Author: ligongxing.
  * Date: 2018年03月12日.
  */
trait ServiceInjector {
  ServiceInjector.inject(this)
}

object ServiceInjector {
  private val injector = Guice.createInjector(new MyModule)

  def inject(obj: AnyRef): Unit = injector.injectMembers(obj)
}
