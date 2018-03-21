package com.ligx.other

import org.scalatest.{FlatSpec, Matchers}

import scala.annotation.tailrec

/**
  * Author: ligongxing.
  * Date: 2018年03月16日.
  */
class OtherTest extends FlatSpec with Matchers{

  "tailrec" should "" in {
    @tailrec
    def f(list: List[Int], sum: Int): Int = {
      list match {
        case item::Nil => sum + item
        case head::tail => f(tail, sum + head)
      }
    }
    println(f(List.range(1, 11), 0))
  }

  "fibonacci" should "" in {
    def fibonacci(n: Int): Int = {
      if(n <= 2) {
        1
      } else {
        fibonacci(n - 2) + fibonacci(n - 1)
      }
    }
    println(fibonacci(7))
  }
}
