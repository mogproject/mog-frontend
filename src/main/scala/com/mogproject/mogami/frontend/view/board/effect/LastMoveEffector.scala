package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.Rect
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Last move effect
  */
case class LastMoveEffector[T <: EffectorTarget](target: T) extends BackgroundEffectorLike[(Seq[Rect], String), T] {
  override def generateElements(x: (Seq[Rect], String)): Seq[TypedTag[SVGElement]] = x._1.map(_.toSVGRect(cls := "board-last", style := s"fill:${x._2};"))
}
