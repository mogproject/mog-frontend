package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.board.SVGBoard
import com.mogproject.mogami.frontend.view.board.box.SVGBox
import com.mogproject.mogami.frontend.view.board.hand.SVGHand
import com.mogproject.mogami.frontend.view.board.player.SVGPlayer
import com.mogproject.mogami.frontend.view.coordinate.Coord
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
    svgAttrs.viewBox := s"0 0 ${layout.viewBoxBottomRight.toString}",
    player.elements,
    board.elements,
    hand.elements,
    box.elements
  ).render

  /**
    * The container of the svg element.
    *
    * Event listeners belong here.
    */
  private[this] lazy val svgDiv: Div = div(
    svgElement
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

  def resize(boardWidth: Int): Unit = svgDiv.style.width = boardWidth.px

  def unselect(): Unit = {
    board.unselect()
  }

  def showBox(): Unit = {
    svgElement.setAttribute("viewBox", s"0 0 ${(layout.viewBoxBottomRight + Coord(0, layout.box.extendedHeight)).toString}")
  }

  def hideBox(): Unit = {
    svgElement.setAttribute("viewBox", s"0 0 ${layout.viewBoxBottomRight.toString}")
  }

  //
  // Event
  //
  registerEvents(svgDiv)

  //
  // Initialization
  //

}
