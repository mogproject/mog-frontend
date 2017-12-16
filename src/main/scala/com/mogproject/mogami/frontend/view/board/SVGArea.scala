package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.board.SVGBoard
import com.mogproject.mogami.frontend.view.board.box.SVGBox
import com.mogproject.mogami.frontend.view.board.hand.SVGHand
import com.mogproject.mogami.frontend.view.board.player.SVGPlayer
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.all._
import scalatags.JsDom.svgAttrs
import scalatags.JsDom.svgTags.svg

/**
  *
  */
case class SVGArea(areaId: Int, layout: SVGAreaLayout) extends WebComponent with SVGAreaEventHandler {

  // Local variables
//  private[this] var control: SVGAreaControl = SVGAreaControl(isFlipped = false, Set.empty, playerNameSelectable = true, isViewMode = false)

  val board: SVGBoard = SVGBoard(layout.board)

  val hand: SVGHand = SVGHand(layout.hand)

  val player: SVGPlayer = SVGPlayer(layout.player)

  val box: SVGBox = SVGBox(layout.box)

//  def getControl: SVGAreaControl = control

  //
  // components
  //
  protected val svgElement: SVGElement = svg(
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
    svgElement,
    svgBox
  ).render

  override def element: Element = svgDiv

  //
  // Operation
  //
  def setFlip(flip: Boolean): Unit = {//if (control.isFlipped != flip) {
//    control = control.copy(isFlipped = flip)
    board.setFlip(flip)
    hand.setFlip(flip)
    player.setFlip(flip)
  }

  def resize(pieceWidth: Int): Unit = svgDiv.style.width = (pieceWidth * layout.viewBoxBottomRight.x / layout.mediumPiece.x).px

  def clearActiveCursor(): Unit = {
    board.effect.cursorEffector.stop()
    hand.effect.cursorEffector.stop()
    player.effect.cursorEffector.stop()
    box.effect.cursorEffector.stop()
  }

  def select(cursor: Cursor, effectEnabled: Boolean): Unit = {
    cursor match {
      case BoardCursor(sq) =>
        val r = board.getRect(sq)
        board.effect.selectedEffector.start(r)
        if (effectEnabled) board.effect.selectingEffector.start(r)
      case HandCursor(h) =>
        val r = hand.getRect(h)
        hand.effect.selectedEffector.start(r)
        if (effectEnabled) hand.effect.selectingEffector.start(r)
      case BoxCursor(pt) =>
        val r = box.getPieceRect(pt)
        box.effect.selectedEffector.start(r)
        if (effectEnabled) box.effect.selectingEffector.start(r)
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

  def showBox(): Unit = showElement(svgBox)

  def hideBox(): Unit = hideElement(svgBox)

  //
  // Event
  //
  registerEvents(svgDiv)

  //
  // Initialization
  //

}
