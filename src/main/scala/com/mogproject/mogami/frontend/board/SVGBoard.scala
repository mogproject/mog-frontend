package com.mogproject.mogami.frontend.board

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.{Piece, Square}
import com.mogproject.mogami.frontend.WebComponent
import com.mogproject.mogami.frontend.coordinate.{Coord, Rect}
import org.scalajs.dom.Element
import org.scalajs.dom.svg.{Circle, Line}

import scalatags.JsDom.all.{cls, src}
import scalatags.JsDom.TypedTag
import scalatags.JsDom.implicits._
import scalatags.JsDom.svgTags._
import scalatags.JsDom.svgAttrs._

/**
  *
  */
case class SVGBoard(boardWidth: Int) extends WebComponent {

  val pieceWidth: Int = boardWidth * 107 / 1027
  val pieceHeight: Int = boardWidth * 113 / 1027
  val margin: Int = boardWidth * 32 / 1027
  val circleSize: Int = math.max(0, math.min(3, boardWidth / 200))

  //
  // Utility
  //
  private[this] def getCoord(fileIndex: Int, rankIndex: Int): Coord = Coord(margin + fileIndex * pieceWidth, margin + rankIndex * pieceHeight)

  private[this] def getRect(fileIndex: Int, rankIndex: Int): Rect = Rect(getCoord(fileIndex, rankIndex), pieceWidth, pieceHeight)

  //
  // View
  //
  private[this] val boardBoarder = Rect(getCoord(0, 0), pieceWidth * 9, pieceHeight * 9).toSVGRect(cls := "board-border")

  private[this] val boardLines: Seq[TypedTag[Line]] = for {
    i <- 1 to 8
    r <- Seq(Rect(getCoord(0, i), pieceWidth * 9, 0), Rect(getCoord(i, 0), 0, pieceHeight * 9))
  } yield {
    r.toSVGLine(cls := "board-line")
  }

  private[this] lazy val boardCircles: Seq[TypedTag[Circle]] = (0 to 3).map { i =>
    val pc = getCoord(3 << (i & 1), 3 << ((i >> 1) & 1))
    circle(cls := "board-circle", cx := pc.x, cy := pc.y, r := circleSize)
  }

  override lazy val element: Element = svg(
    width := boardWidth,
    height := pieceHeight * 9 + margin * 2
  )(Seq(boardBoarder) ++ boardLines ++ boardCircles: _*).render

  def drawPieces(pieces: Map[Square, Piece], flip: Boolean = false, pieceFace: String = "jp1"): Unit = {
    val elems = pieces.map { case (sq, p) =>
      val path = s"assets/img/p/${pieceFace}/${p.ptype.toCsaString}.svg"
      val rc = getRect(9 - sq.file, sq.rank - 1).toInnerRect(pieceWidth, pieceWidth)
      if (p.owner.isBlack ^ flip) {
        rc.toSVGImage(xLinkHref := path)
      } else {
        (-rc).toSVGImage(xLinkHref := path, cls := "flip")
      }
    }

    elems.foreach { elem => element.appendChild(elem.render)}
  }
}
