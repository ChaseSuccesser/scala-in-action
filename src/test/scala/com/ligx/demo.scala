package com.ligx

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Sorting

/**
  * Author: ligongxing.
  * Date: 2017年05月20日.
  */
class Demo extends FlatSpec with Matchers {

  "ordering on method" should "success" in {
    val arr = Array(("a", 5, 2), ("c", 3, 1), ("b", 1, 3))

    Sorting.quickSort(arr)(Ordering[(Int, String)].on(t => (t._3, t._1)))

    for(a <- arr) {
      println(a)
    }
  }

  "ordering by method" should "success" in {
    val arr = Array(("a", 5, 2), ("c", 3, 1), ("b", 1, 3))

    Sorting.quickSort(arr)(Ordering.by[(String, Int, Int), (Int, String)](t => (t._3, t._1)))

    for(a <- arr){
      println(a)
    }
  }
}
