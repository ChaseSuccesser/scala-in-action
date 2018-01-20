package com.ligx.stream

import akka.stream._
import akka.stream.scaladsl._
import akka.{ NotUsed, Done }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

/**
  * Author: ligongxing.
  * Date: 2018年01月12日.
  */
object AkkaStreamDemo extends App{

  implicit val system = ActorSystem("AkkaStreamDemo")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val source: Source[Int, NotUsed] = Source(1 to 100)

  val future: Future[Done] = source.map(i => i + 1).runForeach(i => println(i))

  future onComplete {
    _ => system.terminate()
  }



  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s ⇒ ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
}
