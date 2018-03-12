package com.ligx.di

import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Scopes}
import net.codingwell.scalaguice.ScalaModule

/**
  * Author: ligongxing.
  * Date: 2018年03月12日.
  */
class MyModule extends AbstractModule with ScalaModule{
  override def configure(): Unit = {
    bind[FooService].annotatedWith(Names.named("fooService")).to(classOf[FooService]).in(Scopes.SINGLETON)
  }
}
