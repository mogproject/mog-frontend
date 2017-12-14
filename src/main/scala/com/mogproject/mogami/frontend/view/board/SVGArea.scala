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
case class SVGArea(layout: SVGAreaLayout) extends WebComponent with SVGAreaEventHandler {

  // Local variables
  private[this] var control: SVGAreaControl = SVGAreaControl(isFlipped = false, Set.empty, playerNameSelectable = true, isViewMode = false)

  val board: SVGBoard = SVGBoard(layout.board)

  val hand: SVGHand = SVGHand(layout.hand)

  val player: SVGPlayer = SVGPlayer(layout.player)

  val box: SVGBox = SVGBox(layout.box)

  def getControl: SVGAreaControl = control

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
  def setFlip(flip: Boolean): Unit = if (control.isFlipped != flip) {
    control = control.copy(isFlipped = flip)
    board.setFlip(flip)
    hand.setFlip(flip)
    player.setFlip(flip)
  }

  def resize(pieceWidth: Int): Unit = svgDiv.style.width = (pieceWidth * layout.viewBoxBottomRight.x / layout.mediumPiece.x).px

  def unselect(): Unit = {
    board.unselect()
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
