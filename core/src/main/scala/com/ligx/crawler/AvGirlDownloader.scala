package com.ligx.crawler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.ligx.util.{FileUtil, HttpClient, SimpleHttpResponse}

import scala.util.{Failure, Success}


/**
  * Author: ligongxing.
  * Date: 2018年12月14日.
  */
object AvGirlDownloader {

  implicit val system: ActorSystem = ActorSystem("AvGifCrawlerSystem")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val blockingDispatcher = system.dispatchers.lookup("gif-crawler-http-dispatcher")
  implicit val http = Http()

  private val localPath = "F:\\电影\\av\\girl\\"

  def main(args: Array[String]): Unit = {
    val avGirlsFuture = AvStorage.queryAvGirls()

    avGirlsFuture onComplete {
      case Success(avGirls) => downloadAvGirl(avGirls)
      case Failure(e) => e.printStackTrace()
    }
  }

  private def downloadAvGirl(avGirls: Seq[AvGirl]): Unit = {
    Option(avGirls).getOrElse(List()).foreach(downloadAvGirlImage)
  }

  def downloadAvGirlImage(avGirl: AvGirl): Unit = {
    println(s"开始下载【${avGirl.girlName}】的图片")

    val girlName = avGirl.girlName
    val picUrls = avGirl.picUrlList

    picUrls.zipWithIndex.foreach(tuple => {
      val picUrl = tuple._1.replace(".com", ".net")
      val index = tuple._2
      HttpClient.get(picUrl)
        .run()
        .map {
          case res@SimpleHttpResponse(_, _, _, _, _) =>
            if (res.statusCode.isSuccess()) {
              val gifFilePath = localPath + s"$girlName-$index" + ".jpg"
              FileUtil.writeByteArray(gifFilePath, res.bodyAsByteArray)
            } else {
              println(s"fail to download image, girlName=$girlName, picUrl=$picUrl")
            }
          case _ => println("unknown response!")
        }
    })

    println(s"【${avGirl.girlName}】的图片全部下载结束")
  }

}
