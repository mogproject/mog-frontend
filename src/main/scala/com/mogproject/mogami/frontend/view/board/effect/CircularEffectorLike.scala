package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.api.AnimateElementExtended
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.all._
import scalatags.JsDom.svgTags.animate

trait CircularEffectorLike extends ForegroundEffectorLike[Square] {

  import scalatags.JsDom.svgAttrs._

  def durTime: String = ""

  def finalRadius: Int

  def finalOpacity: Double

  private[this] def initialRadius: Int = SVGBoard.PIECE_WIDTH * 2 / 5

  private[this] def propsFromAutoDestruct: Seq[Modifier] = autoDestruct.map { n =>
    Seq(dur := f"${n / 1000.0}%.2fs", repeatCount := 1)
  }.getOrElse(
    Seq(dur := durTime, repeatCount := "indefinite")
  )

  private[this] def props = Seq(begin := "indefinite", fill := "freeze") ++ propsFromAutoDestruct

  override lazy val animateElems: Seq[AnimateElementExtended] = Seq(
    animate(attributeName := "r", to := finalRadius, props),
    animate(attributeName := "opacity", to := finalOpacity, props)
  ).map(_.render.asInstanceOf[AnimateElementExtended])

  override def generateElements(x: Square): Set[SVGElement] = Set(svgBoard.getRect(x).center.toSVGCircle(initialRadius, cls := "board-move", animateElems).render)

}
