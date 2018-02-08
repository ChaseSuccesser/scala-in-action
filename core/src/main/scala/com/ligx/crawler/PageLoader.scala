package com.ligx.crawler

import java.util.concurrent.TimeUnit

import com.ligx.util.FileUtil
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object PageLoader {

  private val rootUrl = "https://www.1124q.com"

  def main(args: Array[String]): Unit = {
    val avMovies = loadSpecifiedCategoryAllPages("https://www.1124q.com/Html/100/")

//    avMovies.foreach(println)

    val future: Future[Int] = MovieStorage.saveMovie(avMovies)
    val result = Await.result(future, Duration(3, TimeUnit.SECONDS))
    println(s"result = $result")
    // FileUtil.writeLines("F:\\av_movie_list.txt", avMovies.map(_.toString()))
    println(s"avMovies size ${avMovies.size}")
  }

  /**
    * 获取感兴趣的分类的首页url TODO 这种方式获取不到
    *
    * @param categories
    * @return
    */
  def getCategoriesUrl(categories: List[String]): Option[ListBuffer[String]] = {
    if (categories == null || categories.isEmpty) {
      Option.empty
    } else {
      val browser = JsoupBrowser()
      val doc = browser.get(rootUrl)
      println(doc.toHtml)

      val result = ListBuffer[String]()

      val navBarList = doc >> elementList("div[class=nav_bar]")
      if (navBarList != null && navBarList.nonEmpty) {
        navBarList.foreach(navBar => {
          val liElems = navBar >> element("div[class=wrap]") >> element("ul[class=nav_menu]") >> elementList("li")
          if (liElems != null && liElems.nonEmpty) {
            liElems.foreach(liElem => {
              val categoryName = liElem >> text("a")
              //              if(categories contains categoryName) {
              //                val categoryUrl = rootUrl + (liElem >> element("a") >> attr("href"))
              //                result += categoryUrl
              //              }
              val categoryUrl = rootUrl + (liElem >> element("a") >> attr("href"))
              println(s"$categoryName : $categoryUrl")
            })
          }
        })
      }

      Option(result)
    }
  }

  def loadSpecifiedCategoryAllPages(url: String): List[AvMovie] = {
    val browser = JsoupBrowser()
    val doc = browser.get(url)
    val homePageMovies = loadSpecifiedCategoryCurrPage(doc)

    val nextPageElems = doc >> elementList("a[class=next pagegbk]")
    val restPageMovies = if (nextPageElems != null && nextPageElems.nonEmpty) {
      val totalPageCount: Int = nextPageElems.filter(nextPageElem => "尾页" == (nextPageElem >> text))
        .lift(0)
        .map(nextPageElem => {
          val dataAttr = nextPageElem >> attr("data")
          Integer.parseInt(dataAttr.split("-")(1))
        }).getOrElse(0)
      loadSpecifiedCategoryRestPages(url, totalPageCount)
    } else {
      List()
    }

    homePageMovies ::: restPageMovies
  }

  def loadSpecifiedCategoryRestPages(url: String, totalPageCount: Int): List[AvMovie] = {
    if (totalPageCount != 0) {
      val newUrl = if (url.endsWith("/")) url else url + "/"

      val browser = JsoupBrowser()
      val result = for {
        i <- 2 to totalPageCount
        doc = browser.get(newUrl + s"index-$i" + ".html")
      } yield loadSpecifiedCategoryCurrPage(doc)
      result.flatten.toList
    } else {
      List()
    }
  }

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
    val doc = browser.get(movieUrl)

    Option(doc >> element("ul[class=downurl]") >> element("a") >> attr("href"))
  }
}

case class AvMovie(movieName: String, downloadUrl: String, imageUrl: String, ext: String)
