package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
case class SVGArea(layout: SVGAreaLayout) extends WebComponent with SVGBoardEventHandler {

  // Local variables
  protected var boardFlipped: Boolean = false

  // components
  val svgBoard: SVGBoard = SVGBoard(layout.boardOffset, () => boardFlipped)

  private[this] lazy val svgDiv = div(
    svgBoard.element
  ).render

  override def element: Element = svgDiv

  def resize(boardWidth: Int): Unit = svgDiv.style.width = boardWidth.px

  //
  // Operation
  //
  def setFlip(flip: Boolean): Unit = if (boardFlipped != flip) {
    boardFlipped = flip
    svgBoard.setFlip(flip)
  }

  //
  // Event
  //
  svgDiv.addEventListener("mousemove", mouseMove)
}
