package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.board.board.SVGBoard
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom
import scalatags.JsDom.all._
import scalatags.JsDom.svgAttrs

/**
  *
  */
case class ForwardEffector(target: SVGBoard) extends ForegroundEffectorLike[Boolean, SVGBoard] {
  import target.layout._

  override def autoDestruct: Option[Int] = Some(300)

  override def generateElements(x: Boolean): Seq[JsDom.TypedTag[SVGElement]] = {
    val rectX = offset.x + MARGIN_SIZE + pieceWidth * x.fold(6, 1)
    val rectY = offset.y + MARGIN_SIZE + pieceHeight * 3

    val r = Rect(Coord(rectX, rectY), pieceWidth * 2, pieceHeight * 3).shrink(-60)
    val d = pieceWidth * 4 / 5

    val p1 = Coord(x.fold(r.left + d, r.right - d), r.top + d)
    val p2 = Coord(p1.x, r.bottom - d)
    val p3 = Coord(x.fold(r.right - d, r.left + d), r.center.y)

    Seq(
      r.toSVGRect(cls := "board-forward-line", svgAttrs.rx := 60, svgAttrs.ry := 60),
      p1.toSVGPolygon(Seq(p2, p3), cls := "board-forward-triangle")
    )
  }
}
