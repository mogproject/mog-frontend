package com.mogproject.mogami.frontend

import com.mogproject.mogami.frontend.board.SVGBoard
import org.scalajs.dom

import scala.scalajs.js.JSApp

/**
  * Entry point for testing
  */
object App extends JSApp {
  override def main(): Unit = {
    val rootElem = dom.document.getElementById("app")
    SVGBoard(600).materialize(rootElem)
  }
}
