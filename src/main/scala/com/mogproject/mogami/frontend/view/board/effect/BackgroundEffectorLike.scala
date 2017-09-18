package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

trait BackgroundEffectorLike[A] extends EffectorLike[A] {
  def svgBoard: SVGBoard

  override def materialize(elems: Seq[SVGElement]): Unit = svgBoard.materializeBackground(elems)

}
