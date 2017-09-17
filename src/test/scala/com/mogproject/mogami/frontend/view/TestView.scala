package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.core.state.State
import com.mogproject.mogami.frontend.sam.SAMView
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom
import org.scalajs.dom.Element

/**
  *
  */
case class TestView(rootElem: Element) extends SAMView {

  lazy val boardTest = new BoardTestView

  def board = boardTest.board

  override def initialize(): Unit = {

    boardTest.materialize(rootElem)
    board.resize(400)

    //    board.drawPieces(State.HIRATE.board)
    //
    //    dom.window.setTimeout(() => board.resize(200), 2000)
    //    dom.window.setTimeout(() => board.resize(400), 4000)
    //    dom.window.setTimeout(() => board.drawPieces(State.HIRATE.board.mapValues(_.promoted)), 8000)
    //
    //    board.effect.startSelectedSquareEffect(Square(7, 7))
    //    dom.window.setTimeout(() => board.effect.startMoveEffect(Square(3, 3)), 5000)
    //    board.effect.startSelectingEffect(Square(7, 7))
    //    board.effect.startLastMoveEffect(Set(Square(1, 2), Square(1, 3)))
  }
}
