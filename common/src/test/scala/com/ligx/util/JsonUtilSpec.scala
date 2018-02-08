package com.ligx.util

import io.circe.Json
import io.circe.generic.auto._
import io.circe.optics.JsonPath._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Author: ligongxing.
  * Date: 2018年02月07日.
  */
class JsonUtilSpec extends FlatSpec with Matchers {


  case class Bar(xs: Vector[String])

  case class Qux(i: Int, d: Option[Double])

  class Foo(val name: String, val l: List[String]) {
    override def toString: String = {
      name + " , " + l.mkString(",")
    }
  }

  "toJson from case class and class" should "" in {
    val qux: Qux = Qux(13, Some(14.0))
    val bar: Bar = Bar(Vector("a", "b", "c"))
    val foo = new Foo("lgx", List("aa", "bb", "cc"))

    val json = JsonUtil.toJson(qux).getOrElse("")
    val json2 = JsonUtil.toJson(bar).getOrElse("")
    val json3 = JsonUtil.toJson(foo).getOrElse("")

    println(s"json: $json")
    println(s"json2: $json2")
    println(s"json3: $json3")
  }


  "fromJson to case class and class" should "" in {
    val qux: Qux = Qux(13, Some(14.0))
    val bar: Bar = Bar(Vector("a", "b", "c"))
    val foo = new Foo("lgx", List("aa", "bb", "cc"))

    val json = JsonUtil.toJson[Qux](qux).getOrElse("")
    val json2 = JsonUtil.toJson[Bar](bar).getOrElse("")
    val json3 = JsonUtil.toJson(foo).getOrElse("")

    JsonUtil.fromJson[Qux](json).foreach(qux => println(s"qux: $qux"))
    JsonUtil.fromJson[Bar](json2).foreach(bar => println(s"bar: $bar"))
    JsonUtil.fromJson[Foo](json3).foreach(foo => println(s"foo: $foo"))
  }


  "fromJson to Map[String, Int]" should "" in {
    val rawJson: String = "{\"i\":13,\"d\":14.0}"
    JsonUtil.fromJson[Map[String, Int]](rawJson).foreach(println)
  }


  def getJson = {
    JsonUtil.parseJson(
      """
{
  "order": {
    "customer": {
      "name": "Custy McCustomer",
      "contactDetails": {
        "address": "1 Fake Street, London, England",
        "phone": "0123-456-789"
      }
    },
    "items": [{
      "id": 123,
      "description": "banana",
      "quantity": 1
    }, {
      "id": 456,
      "description": "apple",
      "quantity": 2
    }],
    "total": 123.45
  }
}
""")
  }


  "getValue of String by jsonPath" should "" in {
    val json: Json = getJson
    val _phoneNum = root.order.customer.contactDetails.phone.string
    println(JsonUtil.getValue[String](json, _phoneNum).getOrElse(""))
  }

  "getMultiValue from json array" should "" in {
    val json = getJson
    val jsonPath = root.order.items.each.quantity.int
    println(JsonUtil.getMultiValue(json, jsonPath))
  }


  "modify json" should "" in {
    val json = getJson
    val modifiedJson = root.order.items.each.quantity.int.modify(_ * 2)(json)
    println(modifiedJson)
  }
}
