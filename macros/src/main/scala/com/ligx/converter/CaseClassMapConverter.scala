package com.ligx.converter

import scala.reflect.macros.blackbox._

/**
  * Author: ligongxing.
  * Date: 2017年12月26日.
  */
trait CaseClassMapConverter[C] {
  def toMap(c: C): Map[String, Any]
  def fromMap(map: Map[String, Any]): C
}

//object CaseClassMapConverter {
//
//  implicit def Materializer[C]: CaseClassMapConverter[C] = macro convertMacro[C]
//
//  def convertMacro[C: c.WeakTypeTag](c: Context): c.Tree = {
//    import c.universe._
//
//    val tpe = weakTypeOf[C]
//
//    tpe.
//  }
//}
