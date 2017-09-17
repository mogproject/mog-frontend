package com.mogproject.mogami.frontend.view.board.effect
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

trait BackgroundEffectorLike[A] extends EffectorLike[A] {
  def svgBoard: SVGBoard

  override def materialize(elem: SVGElement): Unit = svgBoard.svgElement.insertBefore(elem, svgBoard.borderElement)

}
