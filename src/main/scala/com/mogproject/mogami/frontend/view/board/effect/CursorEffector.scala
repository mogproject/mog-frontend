package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.Rect
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Cursor effect
  */
case class CursorEffector[T <: EffectorTarget](target: T) extends BackgroundEffectorLike[Rect, T] {
  override def generateElements(x: Rect): Seq[TypedTag[SVGElement]] = Seq(x.shrink(12).toSVGRect(cls := "board-cursor"))
}
