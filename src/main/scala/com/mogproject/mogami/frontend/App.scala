package com.mogproject.mogami.frontend

import com.mogproject.mogami.core.state.State
import com.mogproject.mogami.frontend.board.SVGBoard
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scalatags.JsDom.all._

/**
  * Entry point for testing
  */
object App extends JSApp {
  override def main(): Unit = {
    val rootElem = dom.document.getElementById("app")

    val board = new SVGBoard()
    rootElem.appendChild(div(width := 400.px, board.element).render)

    board.drawPieces(State.HIRATE.board)
    // board.clearPieces()
  }
}
