package com.ligx.crawler

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document

import scala.collection.mutable.ListBuffer

object PageLoader {

  private val rootUrl = "https://www.1124n.com"

  def main(args: Array[String]): Unit = {
    loadSpecifiedCategoryAllPages("https://www.1124n.com/Html/128/")
  }

  /**
    * 获取感兴趣的分类的首页url TODO 这种方式获取不到
    *
    * @param categories
    * @return
    */
  def getCategoriesUrl(categories: List[String]): Option[ListBuffer[String]] = {
    if(categories == null || categories.isEmpty) {
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

  def loadSpecifiedCategoryAllPages(url: String): Unit = {
    val browser = JsoupBrowser()
    val doc = browser.get(url)
    println("------- load home page --------")
    loadSpecifiedCategoryCurrPage(doc)

    val nextPageElems = doc >> elementList("a[class=next pagegbk]")
    if(nextPageElems != null && nextPageElems.nonEmpty) {
      val totalPageCount: Int = nextPageElems.filter(nextPageElem => "尾页" == (nextPageElem >> text))
        .lift(0)
        .map(nextPageElem => {
          val dataAttr = nextPageElem >> attr("data")
          Integer.parseInt(dataAttr.split("-")(1))
        }).getOrElse(0)
      loadSpecifiedCategoryRestPages(url, totalPageCount)
    }
  }

  def loadSpecifiedCategoryRestPages(url: String, totalPageCount: Int) : Unit = {
    val newUrl = if(url.endsWith("/")) url else url + "/"

    for (i <- 2 to totalPageCount) {
      println(s"------- load $i page --------")
      val browser = JsoupBrowser()
      val doc = browser.get(newUrl + s"index-$i" + ".html")
      loadSpecifiedCategoryCurrPage(doc)
    }
  }

  def loadSpecifiedCategoryCurrPage(doc: Document): Unit = {
    val movieList = doc >> element("div[class=box movie_list]") >> elementList("li")
    if (movieList != null && movieList.nonEmpty) {
      movieList.foreach(liElem => {
        val aElem = liElem >> element("a")
        val movieName = aElem >> text("h3")
        val downloadUrl = getDownloadUrl(s"""$rootUrl/${aElem >> attr("href")}""").getOrElse("")
        val imageUrl = aElem >> element("img") >> attr("src")
        val createTime = aElem >> text("span[class=movie_date]")

        println(s"$movieName\n$downloadUrl\n$imageUrl\n$createTime")
        println("-------")
      })
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
