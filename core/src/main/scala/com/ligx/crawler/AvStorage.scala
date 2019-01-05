package com.ligx.crawler

import java.util.concurrent.Executors

import com.ligx.db.DbReadWriteTemplate
import slick.jdbc.GetResult

import scala.concurrent.{ExecutionContext, Future}

/**
  * Author: ligongxing.
  * Date: 2018年12月02日.
  */
object AvStorage {

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  def saveAvGirls(avGirlList: List[AvGirl]): Future[Int] = {
    if(avGirlList != null && avGirlList.nonEmpty) {
      val futures = avGirlList map (avGirl => {
        val picUrls = Option(avGirl.picUrlList).getOrElse(List()).mkString(",")
        val sql = s"""INSERT INTO av_girl(girl_name, fanhao, favorite_count, date, pic_url) VALUES('${avGirl.girlName}', '${avGirl.fanhao}', '${avGirl.favoriteCount}', '${avGirl.date}', '$picUrls');"""
        DbReadWriteTemplate.insert(sql).recover{case e: Exception => {
          e.printStackTrace()
          println(s"有问题的SQL: $sql")
          0
        }}
      })
      Future.sequence(futures).map(_.sum)
    } else {
      Future(0)
    }
  }

  def queryAvGirls(): Future[Seq[AvGirl]] = {
    implicit val avMovieResult: GetResult[AvGirl] =
      GetResult(r => AvGirl(r.nextString(), r.nextString(), r.nextString(), "", r.nextString().split(",").toList))

    DbReadWriteTemplate.select(
      s"""
         SELECT girl_name, fanhao, favorite_count, pic_url
         FROM av_girl
         ORDER BY favorite_count DESC;
       """)
  }

  // ----------------------------------------------------------------

  def saveMovies(movies: List[AvMovie]): Future[Array[Int]] = {
    if (movies != null && movies.nonEmpty) {
      val sqlList = movies.filter(_.downloadUrl != "").map(m =>
        s"""INSERT INTO av_movie(movie_name, download_url, image_url, ext) VALUES('${m.movieName}', '${m.downloadUrl}', '${m.imageUrl}', '${m.ext}');""")

      try {
        DbReadWriteTemplate.batchInsert(sqlList)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          sqlList.foreach(println)
          Future(Array(0))
      }
    } else {
      Future(Array(0))
    }
  }

  def saveMovie(movies: List[AvMovie]): Future[Int] = {
    if (movies != null && movies.nonEmpty) {
      val futures = movies.filter(_.movieName != "").map(m => {
        val sql = s"""INSERT INTO av_movie(movie_name, download_url, image_url, ext) VALUES('${m.movieName}', '${m.downloadUrl}', '${m.imageUrl}', '${m.ext}');"""
        DbReadWriteTemplate.insert(sql).recover { case e: Exception => {
          e.printStackTrace()
          println(s"有问题的SQL: $sql")
          0
        }
        }
      })
      Future.sequence(futures).map(_.sum)
    } else {
      Future(0)
    }
  }


  def queryMovies(name: String): Future[Seq[AvMovie]] = {
    implicit val avMovieResult: GetResult[AvMovie] = GetResult(r => AvMovie(r.<<, r.<<, r.<<, r.<<))

    DbReadWriteTemplate.select(
      s"""
         SELECT movie_name, download_url, image_url, ext
         FROM av_movie
         WHERE movie_name LIKE '%$name%';
       """)
  }
}
