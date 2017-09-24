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
    val sign = x.fold(1, -1)

    val rectX = offset.x + MARGIN_SIZE + pieceWidth * 3 + sign * pieceWidth * 2
    val rectY = offset.y + MARGIN_SIZE + pieceHeight * 2

    val p1 = Coord(rectX + (15 - sign * 7) * pieceWidth / 10, rectY + pieceHeight * 5 / 4)
    val p2 = Coord(rectX + (15 - sign * 7) * pieceWidth / 10, rectY + pieceHeight * 15 / 4)
    val p3 = Coord(rectX + (15 + sign * 7) * pieceWidth / 10, rectY + pieceHeight * 5 / 2)

    val r = Rect(Coord(rectX, rectY), pieceWidth * 3, pieceHeight * 5)
    Seq(
      r.toSVGRect(cls := "board-forward-line", svgAttrs.rx := 100, svgAttrs.ry := 100),
      p1.toSVGPolygon(Seq(p2, p3), cls := "board-forward-triangle")
    )
  }
}
