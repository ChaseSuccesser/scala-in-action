package com.ligx.json

import org.scalatest.{FlatSpec, Matchers}
import spray.json._

/**
  * Author: ligongxing.
  * Date: 2017年07月06日.
  */
class SprayJsonSpec extends FlatSpec with Matchers{



  "default json protocol" should "" in {
    val map = Map[String, String]("name" ->"lgx", "age" -> "23")
    val jsonAst = map.toJson
    println(jsonAst.prettyPrint)
  }

  "case class" should "" in {
    import PersonJsonProtocol._

    val person = Person("lgx", 23)
    val jsonAst = person.toJson
    println(jsonAst.prettyPrint)
  }

}


case class Person(name: String, age: Int)

object PersonJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat2(Person)
}