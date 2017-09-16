package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.Square
import org.scalajs.dom.svg.RectElement

import scalatags.JsDom.all._

/**
  * Visual effects
  */
trait SVGBoardEffector {
  self: SVGBoard =>

  private[this] var cursorElem: Option[RectElement] = None

  def startCursorEffect(square: Square): Unit = {
    val elem = getRect(square).shrink(12).toSVGRect(cls := "board-cursor").render
    stopCursorEffect()
    cursorElem = Some(elem)
    svgElement.insertBefore(elem, borderElement)
  }

  def stopCursorEffect(): Unit = {
    cursorElem.foreach(svgElement.removeChild)
    cursorElem = None
  }

}
