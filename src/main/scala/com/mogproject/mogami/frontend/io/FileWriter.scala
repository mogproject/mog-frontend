package com.mogproject.mogami.frontend.io

import com.mogproject.mogami.frontend.api.FileSaver
import org.scalajs.dom.raw.{Blob, BlobPropertyBag}

import scala.scalajs.js

/**
  * File writer
  */
object FileWriter {
  def saveTextFile(content: String, fileName: String): Unit = {
    val data = new Blob(js.Array(content.asInstanceOf[js.Any]), BlobPropertyBag("text/plain;charset=utf-8"))
    FileSaver.saveAs(data, fileName, noAutoBOM = true)
  }
}
