package com.ligx.json

import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.reflect._

/**
  * Author: ligongxing.
  * Date: 2017年07月07日.
  */
class ClassTagSpec extends FlatSpec with Matchers {

  // --------------------
  def foo[T: TypeTag] = typeTag[T]

  // 如果是这样: def bar[T] = foo[T], 就会报错：No TypeTag available for T
  def bar[T: TypeTag] = foo[T]

  "TypeTag2" should "" in {
    println(foo[Int])
    println(foo[List[Int]])
    println(bar[Int])
  }

  // --------------------
  def foo2[T: WeakTypeTag] = weakTypeTag[T]

  // 不需要使用def bar2[T: TypeTag] = foo2[T]，照样正常运行
  def bar2[T] = foo2[T]

  "WeakTypeTag" should "" in {
    println(foo2[Int])
    println(foo2[String])
    println(bar2[Int])
  }

  // --------------------
  "typeOf and weakTypeOf" should "" in {
    def foo[T: WeakTypeTag] = println(weakTypeOf[T])

    def bar[T] = foo[T]

    foo[List[Int]]
    bar[List[Int]]

  }

  // --------------------
  def classTagDemo[A: ClassTag](a: A) = {
    val clazz = implicitly[ClassTag[A]].runtimeClass
    println(clazz.getSimpleName)
  }

  "ClassTag" should "" in {
    classTagDemo(1)
    classTagDemo("a")
    classTagDemo(List(1, 2, 3))
    classTagDemo(Map("1"->"a"))
  }

  "TypeTag" should "" in {
    val tt = typeTag[List[String]]
    println(tt)

    val ct = classTag[List[String]]
    println(ct)

    val wtt = weakTypeTag[List[_]]
    println(wtt)
  }
}
