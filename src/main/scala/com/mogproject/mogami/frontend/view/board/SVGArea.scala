package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.board.SVGBoard
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

  def getControl: SVGAreaControl = control

  //
  // components
  //
  protected val svgElement: SVGElement = svg(
    svgAttrs.width := 100.pct,
    svgAttrs.height := 100.pct,
    svgAttrs.viewBox := s"0 0 ${layout.viewBoxBottomRight.toString}",
    board.elements,
    hand.elements,
    player.elements
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

  //
  // Event
  //
  registerEvents(svgDiv)

  //
  // Initialization
  //
  player.drawSymbols()
}
