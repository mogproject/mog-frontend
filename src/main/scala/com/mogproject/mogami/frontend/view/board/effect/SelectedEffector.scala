package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.Rect
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._


/**
  * Selected square effect
  */
case class SelectedEffector[T <: EffectorTarget](target: T) extends BackgroundEffectorLike[(Rect, String), T] {
  override def generateElements(x: (Rect, String)): Seq[TypedTag[SVGElement]] = Seq(x._1.shrink(9).toSVGRect(cls := "board-selected", style := s"fill:${x._2};"))
}