package com.ligx.hello

import java.text.SimpleDateFormat
import java.util.Date

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Author: ligongxing.
  * Date: 2017年12月22日.
  */
object HelloMacro {

  def greeting: String = macro greetingMacro

  def greetingMacro(c: Context): c.Tree = {
    import c.universe._

    val sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val now = sf.format(new Date())

    q"""
     "Hi! This code was compiled at " +
     $now
     """
  }
}
