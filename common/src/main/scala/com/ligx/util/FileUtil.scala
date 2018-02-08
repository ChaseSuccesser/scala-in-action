package com.ligx.util

import better.files.File

object FileUtil {

  def writeLines(filePath: String, lines: List[String]): Unit = {
    val file = File(filePath).createIfNotExists()

    file.appendLines(lines: _*)
  }
}
