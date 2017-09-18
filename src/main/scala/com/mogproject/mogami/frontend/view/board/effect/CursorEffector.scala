package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Cursor effect
  */
case class CursorEffector(svgBoard: SVGBoard) extends BackgroundEffectorLike[Square] {
  override def generateElements(x: Square): Seq[TypedTag[SVGElement]] = Seq(svgBoard.getRect(x).shrink(12).toSVGRect(cls := "board-cursor"))
}
