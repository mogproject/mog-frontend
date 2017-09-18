package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Flash effect
  */
case class FlashEffector(svgBoard: SVGBoard) extends BackgroundEffectorLike[Square] {
  override def autoDestruct: Option[Int] = Some(300)

  override def generateElements(x: Square): Seq[TypedTag[SVGElement]] = Seq(svgBoard.getRect(x).shrink(12).toSVGRect(cls := "board-flash"))
}
