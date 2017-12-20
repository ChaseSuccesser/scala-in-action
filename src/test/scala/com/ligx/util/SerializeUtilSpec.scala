package com.ligx.util

import org.scalatest.{FlatSpec, Matchers}

/**
  * Author: ligongxing.
  * Date: 2017年12月20日.
  */
class SerializeUtilSpec extends FlatSpec with Matchers {

  case class Person(name: String, age: Int)

  "serialize" should "" in {
    val p = Person("lgx", 25)
    val bytes = SerializeUtil.serialize(p)
    val pp = SerializeUtil.deserialize[Person](bytes)
    println(s"$pp.name: $pp.age")
  }
}
