package com.mogproject.mogami.frontend.view.board.board

import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{Piece, Ptype, Square}
import org.scalajs.dom.Element
import org.scalajs.dom.raw.SVGImageElement

import scalatags.JsDom.all._
import scalatags.JsDom.{TypedTag, svgAttrs}

/**
  *
  */
trait SVGBoardPieceManager {
  self: SVGBoard =>

  import layout._

  // Local variables
  private[this] var currentPieces: Map[Square, Piece] = Map.empty

  private[this] var currentPieceFace: String = "jp1"

  private[this] var pieceMap: Map[Square, Element] = Map.empty

  //
  // Utility
  //
  private[this] def getImagePath(ptype: Ptype, pieceFace: String): String = s"assets/img/p/${pieceFace}/${ptype.toCsaString}.svg"

  private[this] def isPieceFlipped(piece: Piece): Boolean = piece.owner.isWhite ^ isFlipped

  def getPieceRect(square: Square, piece: Piece): Rect =
    isPieceFlipped(piece).when[Rect](-_)(getRect(square).toInnerRect(PIECE_FACE_SIZE, PIECE_FACE_SIZE))

  def generatePieceElement(square: Square, piece: Piece, pieceFace: String, modifiers: Modifier*): TypedTag[SVGImageElement] = {
    getPieceRect(square, piece).toSVGImage(getImagePath(piece.ptype, pieceFace), isPieceFlipped(piece), modifiers)
  }

  //
  // Operation
  //
  /**
    * Draw pieces on board
    *
    * @param pieces
    * @param pieceFace
    * @param keepLastMove
    */
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
      sq -> materializeForeground(generatePieceElement(sq, p, pieceFace).render)
    }

    pieceMap = pieceMap -- removedPieces.map(_._1) ++ newPieceMap
  }

  /**
    * Refresh pieces on board
    */
  def refreshPieces(): Unit = {
    val cp = currentPieces
    clearPieces()
    drawPieces(cp, currentPieceFace, keepLastMove = true)
  }

  private[this] def clearPieces(): Unit = {
    WebComponent.removeElements(pieceMap.values)
    pieceMap = Map.empty
    currentPieces = Map.empty
  }
}
