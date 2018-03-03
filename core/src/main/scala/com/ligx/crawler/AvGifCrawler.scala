package com.ligx.crawler

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import com.ligx.crawler.AvGifCrawlerActor.DownloadRequest
import com.ligx.util.{FileUtil, HttpClient, SimpleHttpResponse}
import net.ruippeixotog.scalascraper.browser.HtmlUnitBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

import scala.concurrent.ExecutionContext

object AvGifCrawler {

  private val rootUrl = "http://www.neihan.net/tags/11_1.html"
  private val localPath = "F:\\电影\\av\\gif\\"

  implicit val system: ActorSystem = ActorSystem("AvGifCrawlerSystem")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val blockingDispatcher = system.dispatchers.lookup("gif-crawler-http-dispatcher")
  implicit val http = Http()

  def main(args: Array[String]): Unit = {
    val browser = HtmlUnitBrowser()
    val doc = browser.get(rootUrl)
    val hrefAttrValue = doc >> element("div[class=page]") >> element("a[class=end]") >> attr("href")
    val hrefAttrValue2 = hrefAttrValue.substring(hrefAttrValue.lastIndexOf("/") + 1)
    val endPageNum = Integer.parseInt(hrefAttrValue2.substring(0, hrefAttrValue2.lastIndexOf(".")).split("_")(1))

    val actors = 0 to 39 map { i=>
      system.actorOf(AvGifCrawlerActor.props(), s"AvGifCrawlerActor-$i")
    }

    1 to endPageNum foreach { pageNum =>
      val pageUrl = s"http://www.neihan.net/tags/11_$pageNum.html"
      actors(pageNum % 40) ! AvGifCrawlerActor.DownloadRequest(pageNum, pageUrl)
    }
  }
}

object AvGifCrawlerActor {
  def props(): Props = Props[AvGifCrawlerActor]

  case class DownloadRequest(pageNum: Int, pageUrl: String)
}

class AvGifCrawlerActor extends Actor {

  private val localPath = "F:\\电影\\av\\gif\\"

  implicit val system: ActorSystem = context.system
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val blockingDispatcher: ExecutionContext = context.system.dispatchers.lookup("gif-crawler-http-dispatcher")
  implicit val http: HttpExt = Http()

  override def receive: Receive = {
    case request: DownloadRequest => getAvGif(request.pageNum, request.pageUrl)
    case _ => println("unknown message!")
  }

  /**
    * 获取某个HTML中所有的gif url
    *
    * @param pageUrl
    */
  def getAvGif(pageNum: Int, pageUrl: String): Unit = {
    println(s"load page=$pageUrl")
    val browser = HtmlUnitBrowser()
    val doc = browser.get(pageUrl)
    doc >?> elementList("dl[class=main-list]") foreach { dlElems =>
      dlElems foreach { dlElem =>
        val gifUrl = dlElem >> elementList("dd") lift(0) map { _ >> "img" >> attr("src")} getOrElse ""
        downloadAvGif(pageNum, gifUrl)
      }
    }
  }

  /**
    * 通过gif url，下载gif到本地
    *
    * @param gifUrl
    */
  def downloadAvGif(pageNum: Int, gifUrl: String): Unit = {
    println(s"download gif, pageNum=$pageNum, gifUrl=$gifUrl")
    HttpClient.get(gifUrl)
      .run()
      .map {
        case res@SimpleHttpResponse(_, _, _, _, _) =>
          if(res.statusCode.isSuccess()) {
            val gifFilePath = localPath + s"$pageNum-${gifUrl.substring(gifUrl.lastIndexOf("/") + 1)}"
            FileUtil.writeByteArray(gifFilePath, res.bodyAsByteArray)
          } else {
            println(s"fail to download gif, pageNum=$pageNum, gifUrl=$gifUrl")
          }
        case _ => println("unknown response!")
      }
  }
}
