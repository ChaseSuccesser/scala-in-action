package com.ligx.macros

import com.ligx.benchmark.Benchmark
import org.scalatest.{FlatSpec, Matchers}

/**
  * Author: ligongxing.
  * Date: 2017年12月26日.
  */
class MacrosSpec extends FlatSpec with Matchers {

  "Benchmark macro annotation" should "" in {
    println(testMethodWithArgs(1L, 2L))
  }

  @Benchmark
  def testMethodWithArgs(x: Double, y: Double) = {
    val z = x + y
    Math.pow(z, z)
  }
}
