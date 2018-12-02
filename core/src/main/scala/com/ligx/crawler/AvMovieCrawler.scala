package com.ligx.crawler

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorSystem, Props}
import net.ruippeixotog.scalascraper.browser.{HtmlUnitBrowser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

case class AvMovie(movieName: String, downloadUrl: String, imageUrl: String, ext: String)

object AvMovieCrawlerConstants {
  val rootUrl = "https://www.9906g.com"
}

object AvMovieCrawler {

  import AvMovieCrawlerConstants._

  def main(args: Array[String]): Unit = {
    val categories = List("成人动漫", "经典三级", "无码在线", "S级女优", "宇都宫紫苑", "水菜麗")
    val urls = getCategoriesUrl(categories).getOrElse(ListBuffer())

    val system = ActorSystem("AvMovieCrawlerSystem")

    val atomic = new AtomicInteger(0)

    urls foreach { url =>
      val actor = system.actorOf(AvMovieCrawlerActor.props, String.valueOf(atomic.incrementAndGet()))
      actor ! url
    }
  }

  /**
    * 获取感兴趣的分类的首页url
    *
    * @param categories
    * @return
    */
  def getCategoriesUrl(categories: List[String]): Option[ListBuffer[String]] = {
    if (categories == null || categories.isEmpty) {
      Option.empty
    } else {
      val browser = HtmlUnitBrowser()
      val doc = browser.get(rootUrl)

      val result = ListBuffer[String]()

      val navBarList = doc >> elementList("div[class=nav_bar]")
      if (navBarList != null && navBarList.nonEmpty) {
        navBarList.foreach(navBar => {
          navBar >?> element("div[class=wrap]") foreach { wrapDiv =>
            val liElems = wrapDiv >> element("ul[class=nav_menu]") >> elementList("li")
            if (liElems != null && liElems.nonEmpty) {
              liElems.foreach(liElem => {
                liElem >?> text("a") foreach { categoryName =>
                  if (categories contains categoryName) {
                    val categoryUrl = rootUrl + (liElem >> element("a") >> attr("href"))
                    result += categoryUrl
                  }
                }
              })
            }
          }
        })
      }
      Option(result)
    }
  }
}

object AvMovieCrawlerActor {
  def props: Props = Props[AvMovieCrawlerActor]
}

class AvMovieCrawlerActor extends Actor {

  import AvMovieCrawlerConstants._

  override def receive: Receive = {
    case categoryUrl: String =>
      println(s"categoryUrl=$categoryUrl")
      val movies = loadSpecifiedCategoryAllPages(categoryUrl)
      val future = AvStorage.saveMovie(movies)
      val result = Await.result(future, Duration(2, TimeUnit.SECONDS))
      println(s"categoryUrl=$categoryUrl, movies size=${movies.size}, insert count=$result")
    case _ => println("unknown message!")
  }

  /**
    * 获取某个category所有的视频
    *
    * @param categoryUrl
    * @return
    */
  def loadSpecifiedCategoryAllPages(categoryUrl: String): List[AvMovie] = {
    val browser = JsoupBrowser()
    val docResult = Try(browser.get(categoryUrl))
    docResult match {
      case Success(doc) =>
        val homePageMovies = loadSpecifiedCategoryCurrPage(doc)

        val nextPageElems = doc >> elementList("a[class=next pagegbk]")
        val restPageMovies = if (nextPageElems != null && nextPageElems.nonEmpty) {
          val totalPageCount: Int = nextPageElems.filter(nextPageElem => "尾页" == (nextPageElem >> text))
            .lift(0)
            .map(nextPageElem => {
              val dataAttr = nextPageElem >> attr("data")
              Integer.parseInt(dataAttr.split("-")(1))
            }).getOrElse(0)
          loadSpecifiedCategoryRestPages(categoryUrl, totalPageCount)
        } else {
          List()
        }

        homePageMovies ::: restPageMovies
      case Failure(e) =>
        println(s"有问题的categoryUrl=$categoryUrl")
        println(e.getMessage)
        Nil
    }
  }

  /**
    * 获取某个category从第二页开始的所有视频
    *
    * @param categoryUrl
    * @param totalPageCount
    * @return
    */
  def loadSpecifiedCategoryRestPages(categoryUrl: String, totalPageCount: Int): List[AvMovie] = {
    if (totalPageCount != 0) {
      val newCategoryUrl = if (categoryUrl.endsWith("/")) categoryUrl else categoryUrl + "/"

      val browser = JsoupBrowser()

      val result = for {
        i <- 2 to totalPageCount
        categoryIndexUrl = newCategoryUrl + s"index-$i" + ".html"
        docResult = Try(browser.get(categoryIndexUrl))
      } yield {
        docResult match {
          case Success(doc) => loadSpecifiedCategoryCurrPage(doc)
          case Failure(e) =>
            println(s"有问题的categoryIndexUrl=$categoryIndexUrl")
            println(e.getMessage)
            Nil
        }
      }
      result.flatten.toList
    } else {
      List()
    }
  }

  /**
    * 获取某一页所有的视频的详细信息
    *
    * @param doc
    * @return
    */
  def loadSpecifiedCategoryCurrPage(doc: Document): List[AvMovie] = {
    val movieList = doc >> element("div[class=box movie_list]") >> elementList("li")
    if (movieList != null && movieList.nonEmpty) {
      movieList.map(liElem => {
        val aElem = liElem >> element("a")
        val movieName = aElem >> text("h3")
        val downloadUrl = getDownloadUrl(s"""$rootUrl/${aElem >> attr("href")}""").getOrElse("")
        val imageUrl = aElem >> element("img") >> attr("src")
        val ext = aElem >> text("span[class=movie_date]")

        AvMovie(movieName, downloadUrl, imageUrl, ext)
      })
    } else {
      List()
    }
  }

  /**
    * 跳转到详情页获取download url
    *
    * @param movieUrl
    * @return
    */
  def getDownloadUrl(movieUrl: String): Option[String] = {
    val browser = JsoupBrowser()
    try {
      val doc = browser.get(movieUrl)
      Option(doc >> element("ul[class=downurl]") >> element("a") >> attr("href"))
    } catch {
      case e: Exception =>
        println(s"发生错误的detail page url: $movieUrl")
        println(e.getMessage)
        None
    }
  }
}
