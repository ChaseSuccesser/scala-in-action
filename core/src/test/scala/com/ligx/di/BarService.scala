package com.ligx.di

import com.google.inject.Inject
import com.google.inject.name.Named

/**
  * Author: ligongxing.
  * Date: 2018年03月12日.
  */
class BarService extends ServiceInjector {

  @Inject
  @Named("fooService")
  private var fooService: FooService = _

  def bar(): String = {
    s"${fooService.foo()} bar"
  }
}