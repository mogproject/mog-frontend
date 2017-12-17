package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.Rect
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

trait CircularEffectorLike[T <: EffectorTarget] extends ForegroundEffectorLike[Rect, T] {

  def duration: String = ""

  def finalRadiusRatio: Double

  def opacityValues: Seq[Double]

  override def generateElements(x: Rect): Seq[TypedTag[SVGElement]] = {
    val initialRadius = x.width * 2 / 5
    val finalRadius = (x.width * finalRadiusRatio).toInt
    val animateElems = Seq(
      "r" -> finalRadius,
      "opacity" -> opacityValues
    ).map { case (n, v) => createAnimateElem(n, v, duration) }

    Seq(x.center.toSVGCircle(initialRadius, cls := "board-move board-selecting", animateElems))
  }

}

/**
  * Circular move effect
  */
case class MoveEffector[T <: EffectorTarget](target: T) extends CircularEffectorLike[T] {
  override val finalRadiusRatio: Double = 1.0

  override val opacityValues: Seq[Double] = Seq(0.0)

  override def autoDestruct: Option[Int] = Some(400)
}

/**
  * Circular selecting effect
  */
case class SelectingEffector[T <: EffectorTarget](target: T) extends CircularEffectorLike[T] {
  override val duration = "4s"

  override val finalRadiusRatio: Double = 8.0

  override val opacityValues: Seq[Double] = Seq(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
}
