package com.mogproject.mogami.frontend.view.board


import com.mogproject.mogami.core.Square
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
    board.materialize(rootElem)

    board.drawPieces(State.HIRATE.board)

    dom.window.setTimeout(() => board.resize(200), 2000)
    dom.window.setTimeout(() => board.resize(400), 4000)
    dom.window.setTimeout(() => board.drawPieces(State.HIRATE.board.mapValues(_.promoted)), 8000)

    board.effect.startSelectedSquareEffect(Square(7, 7))
    dom.window.setTimeout(() => board.effect.startMoveEffect(Square(3, 3)), 5000)
  }

}
