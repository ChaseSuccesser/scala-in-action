package com.ligx.util

import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
  * Author: ligongxing.
  * Date: 2017年07月04日.
  */
class HttpClient {

  /**
    * response.body
    * response.code
    * response.headers
    * response.cookies
    *
    * @param url
    * @return
    */
  def get(url: String): String = {
    val response: HttpResponse[String] = Http("http://www.baidu.com").asString
    response.body
  }

  def post(url: String, params: Map[String, String]): String = {
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
