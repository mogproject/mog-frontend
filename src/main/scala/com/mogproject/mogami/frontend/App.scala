package com.mogproject.mogami.frontend

import com.mogproject.mogami.frontend.state.board.BoardState
import scala.scalajs.js.JSApp

/**
  * Entry point for testing
  */
object App extends JSApp {
  override def main(): Unit = {
    SAM.initialize(BoardState())
  }
}
