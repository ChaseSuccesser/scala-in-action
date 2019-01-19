package com.ligx.crawler

import org.scalatest.{FlatSpec, Matchers}

/**
  * Author: ligongxing.
  * Date: 2018年12月14日.
  */
class AvGirlDownloaderSpec extends FlatSpec with Matchers {

  "downloadAvGirlImage" should "" in {
    // http://www.tom97.com/pic/uploadimg/2017-6/201767942236423.jpg,http://www.tom97.com/pic/fckimg/image/201706/573.jpg,http://www.tom97.com/pic/fckimg/image/201706/572.jpg,http://www.tom97.com/pic/fckimg/image/201706/571.jpg
    // ok http://www.tom97.net/pic/uploadimg/2017-11/2017113010143714615.jpg
    val avGirl = AvGirl("test", "", "", "", List("http://www.tom97.com/pic/uploadimg/2017-6/201767942236423.jpg"))
    AvGirlDownloader.downloadAvGirlImage(avGirl)

    Thread.sleep(2000)
  }
}
