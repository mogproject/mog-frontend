package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.Square
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag

/**
  * Indicates legal moves
  */
case class LegalMoveEffector(svgBoard: SVGBoard) extends ForegroundEffectorLike[Set[Square]] {
  private[this] val effectWidth = SVGBoard.PIECE_WIDTH / 3

  override def generateAnimateElems(): Seq[TypedTag[SVGElement]] = Seq(createAnimateElem("opacity", -1.0, "2s"))

  override def generateElements(x: Set[Square]): Set[TypedTag[SVGElement]] =
    x.map(svgBoard.getRect(_).toInnerRect(effectWidth, effectWidth).toSVGRect(cls := "board-legal"))
}
