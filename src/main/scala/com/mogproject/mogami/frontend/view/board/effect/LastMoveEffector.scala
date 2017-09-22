package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.Rect
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Last move effect
  */
case class LastMoveEffector[T <: EffectorTarget](target: T) extends BackgroundEffectorLike[Seq[Rect], T] {
  override def generateElements(x: Seq[Rect]): Seq[TypedTag[SVGElement]] = x.map(_.toSVGRect(cls := "board-last"))
}
