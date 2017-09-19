package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.board.SVGBoard
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom
import scalatags.JsDom.all._
import scalatags.JsDom.svgAttrs

/**
  *
  */
case class ForwardEffector(svgBoard: SVGBoard) extends ForegroundEffectorLike[Boolean] {
  import SVGBoard._

  override def autoDestruct: Option[Int] = Some(300)

  override def generateElements(x: Boolean): Seq[JsDom.TypedTag[SVGElement]] = {
    val sign = x.fold(1, -1)

    val rectX = svgBoard.offset.x + MARGIN_SIZE + PIECE_WIDTH * 3 + sign * PIECE_WIDTH * 2
    val rectY = svgBoard.offset.y + MARGIN_SIZE + PIECE_HEIGHT * 2

    val p1 = Coord(rectX + (15 - sign * 7) * PIECE_WIDTH / 10, rectY + PIECE_HEIGHT * 5 / 4)
    val p2 = Coord(rectX + (15 - sign * 7) * PIECE_WIDTH / 10, rectY + PIECE_HEIGHT * 15 / 4)
    val p3 = Coord(rectX + (15 + sign * 7) * PIECE_WIDTH / 10, rectY + PIECE_HEIGHT * 5 / 2)

    val r = Rect(Coord(rectX, rectY), SVGBoard.PIECE_WIDTH * 3, SVGBoard.PIECE_HEIGHT * 5)
    Seq(
      r.toSVGRect(cls := "board-forward-line", svgAttrs.rx := 100, svgAttrs.ry := 100),
      p1.toSVGPolygon(Seq(p2, p3), cls := "board-forward-triangle")
    )
  }
}
