package com.ligx.util

import java.nio.charset.{Charset, StandardCharsets}

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Accept, RawHeader}
import akka.stream.Materializer
import akka.util.ByteString

import scala.concurrent.ExecutionContext

/**
  * Author: ligongxing.
  * Date: 2017年12月05日.
  */
case class SimpleHttpResponse(statusCode: StatusCode,
                              charset: Charset,
                              contentType: ContentType,
                              headers: Seq[HttpHeader],
                              body: ByteString) {
  def bodyAsString: String = body.decodeString(charset)
}

case class HttpClient(request: HttpRequest) {

  def withHeaders(headers: (String, String)*): HttpClient = {
    HttpClient(request.mapHeaders(_ ++ headers.map((RawHeader.apply _).tupled)))
  }

  def withParams(params: (String, String)*): HttpClient = {
    val query = params.foldLeft(request.uri.query())((query, curr) => curr +: query)
    HttpClient(request.withUri(request.uri.withQuery(query)))
  }

  def withJsonBody(body: String): HttpClient = {
    HttpClient(request.withEntity(HttpEntity(ContentTypes.`application/json`, body)))
  }

  def withXmlBody(body: String): HttpClient = {
    HttpClient(request.withEntity(HttpEntity(ContentTypes.`text/xml(UTF-8)`, body)))
  }

  def withTextBody(body: String): HttpClient = {
    HttpClient(request.withEntity(HttpEntity(body)))
  }

  def withBinaryBody(body: Array[Byte]): HttpClient = {
    HttpClient(request.withEntity(HttpEntity(body)))
  }

  def withBinaryBody(body: ByteString): HttpClient = {
    HttpClient(request.withEntity(HttpEntity(body)))
  }

  def withFormData(fields: (String, String)*): HttpClient = {
    HttpClient(request.withEntity(FormData(fields: _*).toEntity))
  }

  def withFomData(fields: Map[String, String]): HttpClient = {
    HttpClient(request.withEntity(FormData(fields).toEntity))
  }

  def accept(mediaRanges: MediaRange*): HttpClient = {
    HttpClient(request.addHeader(Accept(mediaRanges: _*)))
  }

  def acceptJson: HttpClient = {
    HttpClient(request.addHeader(Accept(MediaRange(MediaTypes.`application/json`))))
  }

  def acceptXml: HttpClient = {
    HttpClient(request.addHeader(Accept(MediaRange(MediaTypes.`application/xml`))))
  }

  def run()(implicit system: ActorSystem, mat: Materializer, http: HttpExt, ec: ExecutionContext) = {
    for {
      response <- http.singleRequest(request)
      contentType: ContentType = response.entity.contentType
      charset: Charset = contentType.charsetOption.map(_.nioCharset()).getOrElse(StandardCharsets.UTF_8)
      body: ByteString <- response.entity.dataBytes.runFold(ByteString(""))(_ ++ _)
    } yield SimpleHttpResponse(response.status, charset, contentType, response.headers, body)
  }
}

object HttpClient {

  def get(url: String): HttpClient = {
    HttpClient(HttpRequest(method = HttpMethods.GET, uri = url))
  }

  def post(url: String): HttpClient = {
    HttpClient(HttpRequest(method = HttpMethods.POST, uri = url))
  }

  def put(url: String): HttpClient = {
    HttpClient(HttpRequest(method = HttpMethods.PUT, uri = url))
  }
}
