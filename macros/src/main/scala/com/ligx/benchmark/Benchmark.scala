package com.ligx.benchmark

import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.blackbox._

/**
  * Author: ligongxing.
  * Date: 2017年12月26日.
  */
class Benchmark extends StaticAnnotation{
  def profile(annottees: Any*): Any = macro Benchmark.profileImpl
}

object Benchmark {
  def profileImpl(c: Context)(annottees: c.Tree*): c.Tree = {
    import c.universe._

    annottees.head match {
      case q"$mod def $mname[..$tpes](...$args): $rettpe = {..$stats}" => {
        println("compile time....")
        q"""
           $mod def $mname[..$tpes](...$args): $rettpe = {
             val start = System.nanoTime()
             val result = {..$stats}
             val end = System.nanoTime()
             println(${mname.toString} + " elapsed time in nano second = " + (end - start).toString)
             result
           }
         """
      }
      case _ => c.abort(c.enclosingPosition, "invalid method signature")
    }
  }
}


