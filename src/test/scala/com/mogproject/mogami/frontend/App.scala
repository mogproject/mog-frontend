package com.mogproject.mogami.frontend

import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.state.TestState
import com.mogproject.mogami.frontend.view.TestView
import org.scalajs.dom

import scala.scalajs.js.JSApp

/**
  * Entry point for testing
  */
object App extends JSApp {
  override def main(): Unit = {
    SAM.initialize(TestState(BoardModel(), TestView(dom.document.getElementById("app"))))
  }
}

