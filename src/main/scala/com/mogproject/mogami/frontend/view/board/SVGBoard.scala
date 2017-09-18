package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.Ptype
import com.mogproject.mogami.core.{Piece, Square}
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.{SVGElement, SVGImageElement}
import org.scalajs.dom.svg.{Circle, Line, RectElement}
import org.scalajs.dom.Element

import scalatags.JsDom.all._
import scalatags.JsDom.svgTags.svg
import scalatags.JsDom.{TypedTag, svgAttrs}

/**
  *
  */
class SVGBoard extends WebComponent with SVGBoardEffector with SVGBoardEventHandler {

  import SVGBoard._

  // Local variables
  private[this] var currentPieces: Map[Square, Piece] = Map.empty

  private[this] var currentPieceFace: String = "jp1"

  private[this] var pieceMap: Map[Square, Element] = Map.empty

  protected var boardFlipped: Boolean = false

  //
  // Utility
  //
  protected def getCoord(fileIndex: Int, rankIndex: Int): Coord = Coord(MARGIN_SIZE + fileIndex * PIECE_WIDTH, MARGIN_SIZE + rankIndex * PIECE_HEIGHT)

  protected def getRect(fileIndex: Int, rankIndex: Int): Rect = Rect(getCoord(fileIndex, rankIndex), PIECE_WIDTH, PIECE_HEIGHT)

  def getRect(square: Square): Rect = {
    val s = boardFlipped.when[Square](!_)(square)
    getRect(9 - s.file, s.rank - 1)
  }

  protected def getPieceFacePath(ptype: Ptype, pieceFace: String): String = s"assets/img/p/${pieceFace}/${ptype.toCsaString}.svg"

  protected def getPieceFace(square: Square, piece: Piece, pieceFace: String, modifiers: Modifier*): TypedTag[SVGImageElement] = {
    val rc = getRect(square).toInnerRect(PIECE_FACE_SIZE, PIECE_FACE_SIZE)
    val as = modifiers :+ (svgAttrs.xLinkHref := getPieceFacePath(piece.ptype, pieceFace))
    (piece.owner.isBlack ^ boardFlipped).fold(rc.toSVGImage(as), (-rc).toSVGImage(as, cls := "flip"))
  }

  //
  // Operation
  //
  def setFlip(flip: Boolean): Unit = if (boardFlipped != flip) {
    boardFlipped = flip

    // re-draw pieces
    val cp = currentPieces
    clearPieces()
    drawPieces(cp, currentPieceFace, keepLastMove = true)

    // todo: indexes
  }

  def resize(newWidth: Int): Unit = element.asInstanceOf[Div].style.width = newWidth.px

  def drawPieces(pieces: Map[Square, Piece], pieceFace: String = "jp1", keepLastMove: Boolean = false): Unit = {
    // unselect and stop/restart effects
    unselect()
    keepLastMove.fold(effect.lastMoveEffector.restart(), effect.lastMoveEffector.stop())

    // get diffs
    val (xs, ys) = (currentPieces.toSet, pieces.toSet)

    val removedPieces = xs -- ys
    val newPieces = ys -- xs

    // clear old pieces
    removedPieces.foreach(x => WebComponent.removeElement(pieceMap(x._1)))

    // update local variables
    currentPieces = pieces
    currentPieceFace = pieceFace

    // render and materialize
    val newPieceMap = newPieces.map { case (sq, p) =>
      val elem = getPieceFace(sq, p, pieceFace).render
      svgElement.appendChild(elem)
      sq -> elem
    }

    println(s"remove: ${removedPieces.size}, new: ${newPieces.size}")

    pieceMap = pieceMap -- removedPieces.map(_._1) ++ newPieceMap
  }

  private[this] def clearPieces(): Unit = {
    pieceMap.values.foreach(svgElement.removeChild)
    pieceMap = Map.empty
    currentPieces = Map.empty
  }

  def unselect(): Unit = {
    effect.selectedEffector.stop() // a selected square is released here
    effect.cursorEffector.stop()
    effect.selectingEffector.stop()
    effect.legalMoveEffector.stop()
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

  val borderElement: RectElement = boardBoarder.render

  val svgElement: SVGElement = svg(
    svgAttrs.width := 100.pct,
    svgAttrs.height := 100.pct,
    svgAttrs.viewBox := s"0 0 ${BOARD_WIDTH + MARGIN_SIZE * 2} ${BOARD_HEIGHT + MARGIN_SIZE * 2}",
    borderElement
  )(boardLines ++ boardCircles: _*).render

  override lazy val element: Element = div(
    svgElement
  ).render

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