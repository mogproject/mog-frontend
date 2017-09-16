package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.core.{Piece, Square}
import com.mogproject.mogami.frontend.WebComponent
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.SVGElement
import org.scalajs.dom.svg.{Circle, Line, RectElement}
import org.scalajs.dom.{Element, Node}

import scala.collection.mutable
import scalatags.JsDom.all._
import scalatags.JsDom.svgTags.svg
import scalatags.JsDom.{TypedTag, svgAttrs}

/**
  *
  */
class SVGBoard extends WebComponent with SVGBoardEffector with SVGBoardEventHandler {

  import SVGBoard._

  // Local variables
  private[this] val pieceMap: mutable.Map[Square, Node] = mutable.Map.empty

  protected var boardFlipped: Boolean = false

  //
  // Utility
  //
  protected def getCoord(fileIndex: Int, rankIndex: Int): Coord = Coord(MARGIN_SIZE + fileIndex * PIECE_WIDTH, MARGIN_SIZE + rankIndex * PIECE_HEIGHT)

  protected def getRect(fileIndex: Int, rankIndex: Int): Rect = Rect(getCoord(fileIndex, rankIndex), PIECE_WIDTH, PIECE_HEIGHT)

  protected def getRect(square: Square): Rect = {
    val s = boardFlipped.when[Square](!_)(square)
    getRect(9 - s.file, s.rank - 1)
  }

  //
  // Operation
  //
  def setFlip(flip: Boolean): Unit = boardFlipped = flip

  def resize(newWidth: Int): Unit = element.asInstanceOf[Div].style.width = newWidth.px

  def drawPieces(pieces: Map[Square, Piece], pieceFace: String = "jp1"): Unit = {
    val elems = pieces.map { case (sq, p) =>
      val path = s"assets/img/p/${pieceFace}/${p.ptype.toCsaString}.svg"
      val rc = getRect(sq).toInnerRect(PIECE_FACE_SIZE, PIECE_FACE_SIZE)
      val as = svgAttrs.xLinkHref := path
      sq -> (p.owner.isBlack ^ boardFlipped).fold(rc.toSVGImage(as), (-rc).toSVGImage(as, cls := "flip"))
    }

    clearPieces()
    pieceMap ++= elems.mapValues(_.render)
    pieceMap.values.foreach(svgElement.appendChild)
  }


  //
  // View
  //
  private[this] val boardBoarder = Rect(getCoord(0, 0), BOARD_WIDTH, BOARD_HEIGHT).toSVGRect(cls := "board-border")

  private[this] val boardLines: Seq[TypedTag[Line]] = for {
    i <- 1 to 8
    r <- Seq(Rect(getCoord(0, i), BOARD_WIDTH, 0), Rect(getCoord(i, 0), 0, BOARD_HEIGHT))
  } yield r.toSVGLine(cls := "board-line")

  private[this] lazy val boardCircles: Seq[TypedTag[Circle]] = (0 to 3).map { i =>
    getCoord(3 << (i & 1), 3 << ((i >> 1) & 1)).toSVGCircle(CIRCLE_SIZE, cls := "board-circle")
  }

  protected val borderElement: RectElement = boardBoarder.render

  protected val svgElement: SVGElement = svg(
    svgAttrs.width := 100.pct,
    svgAttrs.height := 100.pct,
    svgAttrs.viewBox := s"0 0 ${BOARD_WIDTH + MARGIN_SIZE * 2} ${BOARD_HEIGHT + MARGIN_SIZE * 2}",
    borderElement
  )(boardLines ++ boardCircles: _*).render

  override lazy val element: Element = div(
    svgElement
  ).render

  private[this] def clearPieces(): Unit = {
    pieceMap.values.foreach(svgElement.removeChild)
    pieceMap.clear()
  }

  element.addEventListener("mousemove", mouseMove)
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