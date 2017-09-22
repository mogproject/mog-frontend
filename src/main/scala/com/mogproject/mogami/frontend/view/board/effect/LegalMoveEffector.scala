package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.Square
import com.mogproject.mogami.frontend.view.board.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag

/**
  * Indicates legal moves
  */
case class LegalMoveEffector(target: SVGBoard) extends ForegroundEffectorLike[Seq[Square], SVGBoard] {
  private[this] val effectWidth = target.layout.PIECE_WIDTH / 3

  private[this] def generateAnimateElems: Seq[TypedTag[SVGElement]] = Seq(createAnimateElem("opacity", Seq(0.8, 0, 0, 0), "2s"))

  override def generateElements(x: Seq[Square]): Seq[TypedTag[SVGElement]] =
    x.map(target.getRect(_).toInnerRect(effectWidth, effectWidth).toSVGRect(cls := "board-legal", generateAnimateElems))
}
