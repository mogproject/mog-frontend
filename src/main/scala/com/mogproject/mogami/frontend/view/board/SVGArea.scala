package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.model.board.cursor._
import com.mogproject.mogami.{Move, Piece, Square}
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.board.SVGBoard
import com.mogproject.mogami.frontend.view.board.box.SVGBox
import com.mogproject.mogami.frontend.view.board.hand.SVGHand
import com.mogproject.mogami.frontend.view.board.player.SVGPlayer
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw._

import scalatags.JsDom.all._
import scalatags.JsDom.svgAttrs
import scalatags.JsDom.svgTags.svg

/**
  *
  */
case class SVGArea(areaId: Int, layout: SVGAreaLayout) extends WebComponent with SVGAreaEventHandler {

  // Local variables
  val board: SVGBoard = SVGBoard(layout.board)

  val hand: SVGHand = SVGHand(layout.hand)

  val player: SVGPlayer = SVGPlayer(layout.player)

  val box: SVGBox = SVGBox(layout.box)

  //
  // components
  //
  protected val svgElement: SVGElement = svg(
    svgAttrs.xmlns := "http://www.w3.org/2000/svg",
    svgAttrs.attr("version") := "1.1",
    svgAttrs.width := 100.pct,
    svgAttrs.height := 100.pct,
    svgAttrs.viewBox := s"0 0 ${layout.viewBoxBottomRight}",
    player.elements,
    board.elements,
    hand.elements
  ).render

  private[this] val svgBox: Div = div(
    display := display.none.v,
    marginTop := (-5).px,
    svg(
      svgAttrs.xmlns := "http://www.w3.org/2000/svg",
      svgAttrs.attr("version") := "1.1",
      svgAttrs.width := 100.pct,
      svgAttrs.height := 100.pct,
      svgAttrs.viewBox := s"0 0 ${layout.viewBoxBottomRight.copy(y = box.layout.extendedHeight)}",
      box.elements
    )
  ).render

  /**
    * The container of the svg element.
    *
    * Event listeners belong here.
    */
  private[this] lazy val svgDiv: Div = div(
    marginBottom := (-5).px,
    svgElement,
    svgBox
  ).render

  override def element: Element = svgDiv

  //
  // Operation
  //
  def setFlip(flip: Boolean): Unit = {
    board.setFlip(flip)
    hand.setFlip(flip)
    player.setFlip(flip)
  }

  def clearActiveCursor(): Unit = {
    board.effect.cursorEffector.stop()
    hand.effect.cursorEffector.stop()
    player.effect.cursorEffector.stop()
    box.effect.cursorEffector.stop()
  }

  def select(cursor: Cursor, effectEnabled: Boolean, legalMoves: Set[Square]): Unit = {
    cursor match {
      case BoardCursor(sq) =>
        val r = board.getRect(sq)
        board.effect.selectedEffector.start(r)
        if (effectEnabled) {
          //          board.effect.selectingEffector.start(r)
          board.effect.legalMoveEffector.start(legalMoves.toSeq)
        }
      case HandCursor(h) =>
        val r = hand.getRect(h)
        hand.effect.selectedEffector.start(r)
        if (effectEnabled) {
          //          hand.effect.selectingEffector.start(r)
          board.effect.legalMoveEffector.start(legalMoves.toSeq)
        }
      case BoxCursor(pt) =>
        val r = box.getPieceRect(pt)
        box.effect.selectedEffector.start(r)
      //        if (effectEnabled) box.effect.selectingEffector.start(r)
      case _ =>
    }
  }

  def unselect(): Unit = {
    board.unselect()
    hand.unselect()
    box.unselect()
  }

  def drawCursor(cursor: Cursor): Unit = {
    cursor match {
      case BoardCursor(sq) => board.effect.cursorEffector.start(board.getRect(sq))
      case HandCursor(h) => hand.effect.cursorEffector.start(hand.getRect(h))
      case BoxCursor(pt) => box.effect.cursorEffector.start(box.layout.getRect(pt))
      case PlayerCursor(pl) => player.effect.cursorEffector.start(player.getRect(pl))
    }
  }

  def flashCursor(cursor: Cursor): Unit = {
    cursor match {
      case BoardCursor(sq) => board.effect.flashEffector.start(board.getRect(sq))
      case HandCursor(h) => hand.effect.flashEffector.start(hand.getRect(h))
      case BoxCursor(pt) => box.effect.flashEffector.start(box.getPieceRect(pt))
      case PlayerCursor(pl) => player.effect.flashEffector.start(player.getRect(pl))
    }
  }

  def drawLastMove(lastMove: Option[Move]): Unit = {
    board.effect.lastMoveEffector.stop()
    hand.effect.lastMoveEffector.stop()

    lastMove match {
      case Some(mv) =>
        mv.moveFrom match {
          case Left(sq) =>
            board.effect.lastMoveEffector.start(Seq(sq, mv.to).map(board.getRect))
          case Right(h) =>
            board.effect.lastMoveEffector.start(Seq(board.getRect(mv.to)))
            hand.effect.lastMoveEffector.start(Seq(hand.getRect(h)))
        }
      case None =>
    }
  }

  def showBox(): Unit = WebComponent.showElement(svgBox)

  def hideBox(): Unit = WebComponent.hideElement(svgBox)

  //
  // Event
  //
  registerEvents(svgDiv)

}
