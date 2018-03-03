package com.ligx.crawler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.ligx.util.{FileUtil, HttpClient, SimpleHttpResponse}
import net.ruippeixotog.scalascraper.browser.HtmlUnitBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

object AvGifCrawler {

  private val rootUrl = "http://www.neihan.net/tags/11_1.html"
  private val localPath = "F:\\电影\\av\\gif\\"

  implicit val system: ActorSystem = ActorSystem("AvGifCrawlerSystem")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  implicit val http = Http()

  def main(args: Array[String]): Unit = {
    getAvGif(rootUrl)
  }

  def getAvGif(url: String): Unit = {
    val browser = HtmlUnitBrowser()
    val doc = browser.get(url)
    doc >?> elementList("dl[class=main-list]") foreach { dlElems =>
      dlElems foreach { dlElem =>
        val gifUrl = dlElem >> elementList("dd") lift(0) map { _ >> "img" >> attr("src")} getOrElse ""
        downloadAvGif(gifUrl)
      }
    }
  }

  def downloadAvGif(gifUrl: String): Unit = {
    HttpClient.get(gifUrl)
      .run()
      .map {
      case res@SimpleHttpResponse(_, _, _, _, _) =>
        val gifFilePath = localPath + gifUrl.substring(gifUrl.lastIndexOf("/") + 1)
        FileUtil.writeByteArray(gifFilePath, res.bodyAsByteArray)
      case _ => println("unknown response!")
    }
  }
}
