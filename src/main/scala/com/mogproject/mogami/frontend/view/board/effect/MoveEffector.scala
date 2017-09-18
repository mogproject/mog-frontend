package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.view.board.SVGBoard

/**
  * Circular move effect
  */
case class MoveEffector(svgBoard: SVGBoard) extends CircularEffectorLike {
  override val finalRadius: Int = SVGBoard.PIECE_WIDTH

  override val opacityValues: Seq[Double] = Seq(0.0)

  override def autoDestruct: Option[Int] = Some(400)
}