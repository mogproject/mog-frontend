package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

trait CircularEffectorLike extends ForegroundEffectorLike[Square] {

  def duration: String = ""

  def finalRadius: Int

  def opacityValues: Seq[Double]

  private[this] def initialRadius: Int = SVGBoard.PIECE_WIDTH * 2 / 5

  override def generateElements(x: Square): Seq[TypedTag[SVGElement]] = Seq(svgBoard.getRect(x).center.toSVGCircle(initialRadius, cls := "board-move board-selecting"))

  override def generateAnimateElems(): Seq[TypedTag[SVGElement]] =
    Seq("r" -> finalRadius, "opacity" -> opacityValues).map { case (n, v) => createAnimateElem(n, v, duration) }
}
