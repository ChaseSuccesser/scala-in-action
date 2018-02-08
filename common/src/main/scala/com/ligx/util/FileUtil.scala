package com.ligx.util

import java.nio.charset.Charset

import better.files.File

object FileUtil {

  def writeLines(filePath: String, lines: List[String], charSet: Charset = Charset.forName("UTF-8")): Unit = {
    val file = File(filePath).createIfNotExists()
    file.appendLines(lines: _*)(charSet)
  }

  def writeString(filePath: String, content: String, charSet: Charset = Charset.forName("UTF-8")): Unit = {
    val file = File(filePath).createIfNotExists()
    file.write(content)(charset = charSet)
  }

  def readLines(filePath: String, charSet: Charset = Charset.forName("UTF-8")): Option[Traversable[String]] = {
    val file = File(filePath)
    if (file.exists) {
      Option(file.lines(charSet))
    } else {
      None
    }
  }

  def readString(filePath: String, charSet: Charset = Charset.forName("UTF-8")): String = {
    val file = File(filePath)
    if (file.exists) {
      file.contentAsString(charSet)
    } else {
      ""
    }
  }

  /**
    * also works on directories as expected (deletes children recursively)
    *
    * @param filePath
    */
  def deleteFile(filePath: String): Unit = {
    val file = File(filePath)
    file.deleteOnExit()
  }

  /**
    * If directory, deletes all children;
    * if file clears contents
    *
    * @param filePath
    */
  def clearFile(filePath: String): Unit = {
    val file = File(filePath)
    if (file.exists) file.clear()
  }

  /**
    * also works on directories (copies recursively)
    *
    * @param sourcePath
    * @param targetPath
    */
  def copyTo(sourcePath: String, targetPath: String): Unit = {
    val sourceFile = File(sourcePath)
    val targetFile = File(targetPath)

    if (sourceFile.exists && targetFile.exists) {
      sourceFile.copyTo(targetFile, overwrite = true)
    }
  }

  def isSameContent(filePath1: String, filePath2: String): Boolean = {
    val file1 = File(filePath1)
    val file2 = File(filePath2)

    file1.isSameContentAs(file2)
  }
}
