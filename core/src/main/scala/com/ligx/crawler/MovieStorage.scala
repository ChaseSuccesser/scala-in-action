package com.ligx.crawler

import java.util.concurrent.Executors

import com.ligx.db.DbReadWriteTemplate
import slick.jdbc.GetResult

import scala.concurrent.{ExecutionContext, Future}

object MovieStorage {

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  def saveMovie(movies: List[AvMovie]): Future[Array[Int]] = {
    if(movies != null && movies.nonEmpty) {
      val sqlList = movies.map(m =>
        s"""
           INSERT INTO av_movie(movie_name, download_url, image_url, ext)
           VALUES('${m.movieName}', '${m.downloadUrl}', '${m.imageUrl}', '${m.ext}');
         """)

      DbReadWriteTemplate.batchInsert(sqlList)
    } else {
      Future(Array(0))
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
