package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Last move effect
  */
case class LastMoveEffector(svgBoard: SVGBoard) extends BackgroundEffectorLike[Set[Square]] {
  override def generateElements(x: Set[Square]): Set[TypedTag[SVGElement]] = x.map(svgBoard.getRect(_).toSVGRect(cls := "board-last"))
}
