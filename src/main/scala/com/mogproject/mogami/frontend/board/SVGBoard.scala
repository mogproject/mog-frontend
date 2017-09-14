package com.mogproject.mogami.frontend.board

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.{Piece, Square}
import com.mogproject.mogami.frontend.WebComponent
import com.mogproject.mogami.frontend.coordinate.{Coord, Rect}
import org.scalajs.dom.{Element, Node}
import org.scalajs.dom.svg.{Circle, Line}

import scala.collection.mutable
import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag
import scalatags.JsDom.svgTags._
import scalatags.JsDom.svgAttrs

/**
  *
  */
class SVGBoard extends WebComponent {

  import SVGBoard._

  // Local variables
  private[this] val pieceMap: mutable.Map[Square, Node] = mutable.Map.empty


  //
  // Utility
  //
  private[this] def getCoord(fileIndex: Int, rankIndex: Int): Coord = Coord(MARGIN_SIZE + fileIndex * PIECE_WIDTH, MARGIN_SIZE + rankIndex * PIECE_HEIGHT)

  private[this] def getRect(fileIndex: Int, rankIndex: Int): Rect = Rect(getCoord(fileIndex, rankIndex), PIECE_WIDTH, PIECE_HEIGHT)

  //
  // View
  //
  private[this] val boardBoarder = Rect(getCoord(0, 0), BOARD_WIDTH, BOARD_HEIGHT).toSVGRect(cls := "board-border")

  private[this] val boardLines: Seq[TypedTag[Line]] = for {
    i <- 1 to 8
    r <- Seq(Rect(getCoord(0, i), BOARD_WIDTH, 0), Rect(getCoord(i, 0), 0, BOARD_HEIGHT))
  } yield {
    r.toSVGLine(cls := "board-line")
  }

  private[this] lazy val boardCircles: Seq[TypedTag[Circle]] = (0 to 3).map { i =>
    val pc = getCoord(3 << (i & 1), 3 << ((i >> 1) & 1))
    circle(cls := "board-circle", svgAttrs.cx := pc.x, svgAttrs.cy := pc.y, svgAttrs.r := CIRCLE_SIZE)
  }

  private[this] val svgElement = svg(
    svgAttrs.width := 100.pct,
    svgAttrs.height := 100.pct,
    svgAttrs.viewBox := s"0 0 ${BOARD_WIDTH + MARGIN_SIZE * 2} ${BOARD_HEIGHT + MARGIN_SIZE * 2}"
  )(Seq(boardBoarder) ++ boardLines ++ boardCircles: _*).render

  override lazy val element: Element = svgElement

  def drawPieces(pieces: Map[Square, Piece], flip: Boolean = false, pieceFace: String = "jp1"): Unit = {
    val elems = pieces.map { case (sq, p) =>
      val path = s"assets/img/p/${pieceFace}/${p.ptype.toCsaString}.svg"
      val rc = getRect(9 - sq.file, sq.rank - 1).toInnerRect(PIECE_FACE_SIZE, PIECE_FACE_SIZE)
      sq -> (if (p.owner.isBlack ^ flip) {
        rc.toSVGImage(svgAttrs.xLinkHref := path)
      } else {
        (-rc).toSVGImage(svgAttrs.xLinkHref := path, cls := "flip")
      })
    }

    pieceMap.clear()
    elems.foreach { case (sq, elem) =>
      val ee = elem.render
      pieceMap += (sq -> ee)
      svgElement.appendChild(ee)
    }
  }

  def clearPieces(): Unit = {
    pieceMap.values.foreach(svgElement.removeChild)
    pieceMap.clear()
  }

}

object SVGBoard {
  val VIEW_BOX_WIDTH: Int = 2048
  val PIECE_WIDTH: Int = 210
  val PIECE_HEIGHT: Int = 230
  val PIECE_FACE_SIZE: Int = 200
  val BOARD_WIDTH: Int = PIECE_WIDTH * 9
  val BOARD_HEIGHT: Int = PIECE_HEIGHT * 9
  val MARGIN_SIZE: Int = (VIEW_BOX_WIDTH - BOARD_WIDTH) / 2
  val CIRCLE_SIZE: Int = 14
}