package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.model.board.cursor._
import com.mogproject.mogami.{Move, Piece, Square}
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.board.SVGBoard
import com.mogproject.mogami.frontend.view.board.box.SVGBox
import com.mogproject.mogami.frontend.view.board.hand.SVGHand
import com.mogproject.mogami.frontend.view.board.player.SVGPlayer
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import com.mogproject.mogami.frontend.view.system.SVGImageCache
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
case class SVGArea(areaId: Int, layout: SVGAreaLayout)(implicit imageCache: SVGImageCache) extends WebComponent with SVGAreaEventHandler {

  private[this] lazy val boxSize = layout.viewBoxBottomRight.copy(y = layout.box.extendedHeight)

  /**
    * Transparent foremost rect element for each SVG regions.
    *
    * @note `touchend` event will not fire if an SVG element is removed from dom while being touched. To prevent this,
    *       Create a region-wide rectangle element as foremost in order to manage user interactions.
    */
  private[this] lazy val boardForemostElement: SVGRectElement = Rect(Coord(0, 0), layout.viewBoxBottomRight.x, layout.viewBoxBottomRight.y).toSVGRect(
    svgAttrs.fillOpacity := 0
  ).render

  private[this] lazy val boxForemostElement: SVGRectElement = Rect(Coord(0, 0), boxSize.x, boxSize.y).toSVGRect(
    svgAttrs.fillOpacity := 0
  ).render

  // Local variables
  lazy val board: SVGBoard = SVGBoard(layout.board, boardForemostElement)

  lazy val hand: SVGHand = SVGHand(layout.hand, boardForemostElement)

  lazy val player: SVGPlayer = SVGPlayer(layout.player, boardForemostElement)

  lazy val box: SVGBox = SVGBox(layout.box, boxForemostElement)

  //
  // components
  //
  private[this] lazy val svgElement: SVGElement = svg(
    svgAttrs.xmlns := "http://www.w3.org/2000/svg",
    svgAttrs.attr("version") := "1.1",
    svgAttrs.width := 100.pct,
    svgAttrs.height := 100.pct,
    svgAttrs.viewBox := s"0 0 ${layout.viewBoxBottomRight}",
    player.elements,
    board.elements,
    hand.elements,
    boardForemostElement
  ).render

  private[this] lazy val svgBox: Div = div(
    display := display.none.v,
    marginTop := (-5).px,
    svg(
      svgAttrs.xmlns := "http://www.w3.org/2000/svg",
      svgAttrs.attr("version") := "1.1",
      svgAttrs.width := 100.pct,
      svgAttrs.height := 100.pct,
      svgAttrs.viewBox := s"0 0 ${boxSize}",
      box.elements,
      boxForemostElement
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

  def select(cursor: Cursor, effectEnabled: Boolean, legalMoves: Set[Square], color: String): Unit = {
    cursor match {
      case BoardCursor(sq) =>
        val r = board.getRect(sq)
        board.effect.selectedEffector.start((r, color))
        if (effectEnabled) {
          //          board.effect.selectingEffector.start(r)
          board.effect.legalMoveEffector.start(legalMoves.toSeq)
        }
      case HandCursor(h) =>
        val r = hand.getRect(h)
        hand.effect.selectedEffector.start((r, color))
        if (effectEnabled) {
          //          hand.effect.selectingEffector.start(r)
          board.effect.legalMoveEffector.start(legalMoves.toSeq)
        }
      case BoxCursor(pt) =>
        val r = box.getPieceRect(pt)
        box.effect.selectedEffector.start((r, color))
      //        if (effectEnabled) box.effect.selectingEffector.start(r)
      case _ =>
    }
  }

  def unselect(): Unit = {
    board.unselect()
    hand.unselect()
    box.unselect()
  }

  def drawCursor(cursor: Cursor, color: String): Unit = {
    cursor match {
      case BoardCursor(sq) => board.effect.cursorEffector.start((board.getRect(sq), color))
      case HandCursor(h) => hand.effect.cursorEffector.start((hand.getRect(h), color))
      case BoxCursor(pt) => box.effect.cursorEffector.start((box.layout.getRect(pt), color))
      case PlayerCursor(pl) => player.effect.cursorEffector.start((player.getRect(pl), color))
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

  def drawLastMove(lastMove: Option[Move], color: String): Unit = {
    board.effect.lastMoveEffector.stop()
    hand.effect.lastMoveEffector.stop()

    lastMove match {
      case Some(mv) =>
        mv.moveFrom match {
          case Left(sq) =>
            board.effect.lastMoveEffector.start((Seq(sq, mv.to).map(board.getRect), color))
          case Right(h) =>
            board.effect.lastMoveEffector.start((Seq(board.getRect(mv.to)), color))
            hand.effect.lastMoveEffector.start((Seq(hand.getRect(h)), color))
        }
      case None =>
    }
  }

  def drawBackgroundColor(color: String): Unit = {
    board.drawBackgroundColor(color)
    hand.drawBackgroundColor(color)
    box.drawBackgroundColor(color)
  }

  def showBox(): Unit = WebComponent.showElement(svgBox)

  def hideBox(): Unit = WebComponent.hideElement(svgBox)

  //
  // Event
  //
  override protected def eventTarget: HTMLElement = svgDiv

}
