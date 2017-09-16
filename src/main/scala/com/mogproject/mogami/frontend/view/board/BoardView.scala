package com.mogproject.mogami.frontend.view.board


import com.mogproject.mogami.core.state.State
import com.mogproject.mogami.frontend.sam.SAMView
import org.scalajs.dom

/**
  *
  */
trait BoardView extends SAMView {

  lazy val board = new SVGBoard()

  def initialize(): Unit = {
    val rootElem = dom.document.getElementById("app")
    rootElem.appendChild(board.element)

    board.drawPieces(State.HIRATE.board)
    board.drawPieces(State.HIRATE.board.mapValues(_.promoted))

    dom.window.setTimeout(() => board.resize(200), 2000)
    dom.window.setTimeout(() => board.resize(400), 4000)
  }

}
