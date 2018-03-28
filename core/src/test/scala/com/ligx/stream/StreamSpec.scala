package com.ligx.stream

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  * Author: ligongxing.
  * Date: 2018年03月27日.
  */
class StreamSpec extends FlatSpec with Matchers{

  implicit val system = ActorSystem("test")
  implicit val materilizer = ActorMaterializer()
  implicit val ec = system.dispatcher

  "via and to" should "" in {
    Source(1 to 10).via(Flow[Int].map(_*2)).to(Sink.foreach(println(_)))
  }

  "TestSink.probe" should "" in {
    val sourceTest = Source(1 to 4).filter(_ % 2 == 0).map(_ * 2)
    sourceTest.runWith(TestSink.probe[Int])
      .request(2)
      .expectNext(4, 8)
      .expectComplete()
  }

  "TestSource.probe by cancel" should "" in {
    val sinkTest = Sink.cancelled
    TestSource.probe[Int]
      .toMat(sinkTest)(Keep.left)
      .run()
      .expectCancellation()
  }

  "TestSource.probe by exception" should "" in {
    val sinkTest = Sink.head[Int]
    val (probe, future) = TestSource.probe[Int]
      .toMat(sinkTest)(Keep.both)
      .run()

    probe.sendError(new Exception("2333"))

    Await.ready(future, Duration(3, TimeUnit.SECONDS))

    future onComplete {
      case Success(result) => println(result)
      case Failure(ex) => assert("2333" == ex.getMessage)
    }
  }
}
