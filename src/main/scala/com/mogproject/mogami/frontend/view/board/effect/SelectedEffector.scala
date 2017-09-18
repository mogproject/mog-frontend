package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Selected square effect
  */
case class SelectedEffector(svgBoard: SVGBoard) extends BackgroundEffectorLike[Square] {
  override def generateElements(x: Square): Set[TypedTag[SVGElement]] = Set(svgBoard.getRect(x).shrink(9).toSVGRect(cls := "board-selected"))
}