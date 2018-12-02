package com.ligx.crawler

import java.util.concurrent.TimeUnit

import net.ruippeixotog.scalascraper.browser.{HtmlUnitBrowser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

/**
  * Author: ligongxing.
  * Date: 2018年12月01日.
  */
object AvGirlCrawler {

  private val rootUrl = "http://www.tom97.com"

  def main(args: Array[String]): Unit = {
    val avGirlList = 1 to 12 flatMap parsePerMonth
    println(s"AvGirl list size ${avGirlList.size}")
  }

  private def parsePerMonth(month: Int): List[AvGirl] = {
    println(s"开始解析第$month 月.")
    val indexUrl = "http://www.tom97.com/"+ month + "fanhao/"

    val browser = JsoupBrowser()
    val doc = browser.get(indexUrl)
    val contentElemList = doc >> element("div[class=posts]") >> elementList("div[class=content]")
    val firstPageAvGirlList = contentElemList map parseContentElement

    AvStorage.saveAvGirls(firstPageAvGirlList)
    println(s"正解析 $month 月, 首页解析结束")

    val pageNumberElemList = (doc >> elementList("a[class=page-numbers]")).reverse
    val totalPageNum = Integer.parseInt((pageNumberElemList.head >> text).split("/")(1).trim)
    val restPageAvGirlList = (2 to totalPageNum) flatMap (pageNum => {
      val nextPageUrl = indexUrl + "index" + pageNum + ".html"
      println(s"正解析 $month 月, 当前页码: $pageNum")
      parsePerPage(nextPageUrl)
    })

    println(s"结束解析第$month 月.")
    firstPageAvGirlList.++(restPageAvGirlList)
  }

  private def parsePerPage(pageUrl: String): List[AvGirl] = {
    val browser = JsoupBrowser()
    val doc = browser.get(pageUrl)
    val contentElemList = doc >> element("div[class=posts]") >> elementList("div[class=content]")
    val avGirlList = contentElemList map parseContentElement

    AvStorage.saveAvGirls(avGirlList)

    avGirlList
  }

  private def parseContentElement(contentElem: Element): AvGirl = {
    val contentHeader = contentElem >> element("div[class=content-header]")
    val contentImage = contentElem >> element("div[class=content-img]")

    val aElem = contentHeader >> element("a")
    val detailUrl = aElem >> attr("href")
    val girlName = aElem >> attr("title")
    val h2Text = aElem >> text("h2")
    val fanhao = h2Text substring (h2Text.indexOf("番号:"), h2Text.length())

    val contentInfo = contentHeader >> element("div[class=content-info]")
    val date = (contentInfo >> elementList("span")).head >> text
    val favoriteCount = contentInfo >> text("span[class=count]") trim

    val picUrl = rootUrl + (contentImage >> element("img") >> attr("src"))

    AvGirl(girlName, fanhao, favoriteCount, date, picUrl::getDetailPicList(detailUrl))
  }

  private def getDetailPicList(detailUrl: String): List[String] = {
    val browser = JsoupBrowser()
    val doc = browser.get(detailUrl)
    val divList = doc >> elementList("div[class=post-content]>div")
    val div = divList(1)
    val imgElemList = div >> elementList("img")
    val picUrlList = imgElemList map (imgElem => {
      val imgSrc = imgElem >> attr("src")
      rootUrl + imgSrc
    })
    picUrlList
  }

}


case class AvGirl(girlName: String,
                  fanhao: String,
                  favoriteCount: String,
                  date: String,
                  picUrlList: List[String]
                 )
