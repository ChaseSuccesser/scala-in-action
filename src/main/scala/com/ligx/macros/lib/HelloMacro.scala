package com.ligx.macros.lib

import java.util.Date

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Author: ligongxing.
  * Date: 2017年12月22日.
  */
object HelloMacro {

  def greeting: String = macro greetingMacro

  def greetingMacro(c: Context): c.Expr[String] = {
    import c.universe._

    val now = new Date().toString

    Expr()
    q"hello $now"
  }
}
