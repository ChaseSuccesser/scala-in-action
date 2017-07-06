package com.ligx.util

import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
  * Author: ligongxing.
  * Date: 2017年07月04日.
  */
class HttpClient {

  /**
    * 发送get请求
    *
    * response.body
    * response.code
    * response.headers
    * response.cookies
    *
    * @param url
    * @param params
    * @return
    */
  def get(url: String, params: Map[String, String]): String = {
    val request: HttpRequest = Http(url)

    if(params != null && params.nonEmpty){
      val requestWithParams = request.params(params)
      val response: HttpResponse[String] = requestWithParams.asString
      response.body
    } else {
      request.asString.body
    }
  }


  /**
    * 发送post请求
    *
    * @param url
    * @param params
    * @return
    */
  def post(url: String, params: scala.collection.mutable.Map[String, String]): String = {
    if(url == null || url.trim.equals("")){
      return ""
    }
    if(params == null || params.isEmpty){
      return ""
    }
    val paramSeq = params.toSeq

    val request: HttpRequest = Http(url)
    val response = request.postForm(paramSeq).asString
    response.body
  }
}
