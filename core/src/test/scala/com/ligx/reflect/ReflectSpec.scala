package com.ligx.reflect

import org.scalatest.{FlatSpec, Matchers}
import scala.reflect.runtime.universe._

/**
  * Author: ligongxing.
  * Date: 2017年12月26日.
  */
class ReflectSpec extends FlatSpec with Matchers{

  case class Person(name: String, age: Int)

  "test reflect api" should "" in {
    val person = Person("l", 23)

    test(person)
  }

  def test[C: WeakTypeTag](c: C) = {
    val tpe = weakTypeOf[C]

    val fields = tpe.decls.collectFirst{
      case symbol: MethodSymbol if symbol.isPrimaryConstructor => symbol
    }.get.paramLists.head

    val companion = tpe.typeSymbol.companion

    val (toParams, fromParams) = fields.map{ field =>
      val name = field.name.toTermName
      val decoded = name.decodedName.toString
      val rtype = tpe.decl(name).typeSignature
      (q"$decoded -> t.$name", q"map($decoded).asInstanceOf[$rtype]")
    }.unzip

    println(toParams)
    println(fromParams)
  }
}
