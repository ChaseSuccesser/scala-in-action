package com.ligx.util

import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}
import monocle.{Optional, Traversal}

import scala.util.{Failure, Success, Try}

/**
  * Author: ligongxing.
  * Date: 2018年02月07日.
  */
object JsonUtil {

  def toJson[T: Encoder](t: T): Option[String] = {
    Try(t.asJson.noSpaces) match {
      case Success(json) => Some(json)
      case Failure(error) =>
        error.printStackTrace()
        None
    }
  }

  def fromJson[T: Decoder](json: String): Option[T] = {
    decode[T](json) match {
      case Right(t) => Some(t)
      case Left(error) =>
        error.printStackTrace()
        None
    }
  }

  def parseJson(json: String): Json = {
    parse(json).getOrElse(Json.Null)
  }


  def getValue[T](json: Json, jsonPath: Optional[Json, T]): Option[T] = {
    jsonPath.getOption(json)
  }

  def getMultiValue[T](json: Json, jsonPath: Traversal[Json, T]): List[T] = {
    jsonPath.getAll(json)
  }
}
