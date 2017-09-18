package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{Piece, Ptype, Square}
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.Element
import org.scalajs.dom.raw.SVGImageElement

import scalatags.JsDom.{TypedTag, svgAttrs}
import scalatags.JsDom.all._

/**
  *
  */
trait SVGBoardPieceManager {
  self: SVGBoard =>

  import SVGBoard._

  // Local variables
  private[this] var currentPieces: Map[Square, Piece] = Map.empty

  private[this] var currentPieceFace: String = "jp1"

  private[this] var pieceMap: Map[Square, Element] = Map.empty

  //
  // Utility
  //
  private[this] def getImagePath(ptype: Ptype, pieceFace: String): String = s"assets/img/p/${pieceFace}/${ptype.toCsaString}.svg"

  private[this] def getPieceFace(square: Square, piece: Piece, pieceFace: String, modifiers: Modifier*): TypedTag[SVGImageElement] = {
    val rc = getRect(square).toInnerRect(PIECE_FACE_SIZE, PIECE_FACE_SIZE)
    val as = modifiers :+ (svgAttrs.xLinkHref := getImagePath(piece.ptype, pieceFace))
    (piece.owner.isBlack ^ boardFlipped).fold(rc.toSVGImage(as), (-rc).toSVGImage(as, cls := "flip"))
  }

  //
  // Operation
  //
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

    pieceMap = pieceMap -- removedPieces.map(_._1) ++ newPieceMap
  }

  def refreshPieces(): Unit = {
    val cp = currentPieces
    clearPieces()
    drawPieces(cp, currentPieceFace, keepLastMove = true)
  }

  private[this] def clearPieces(): Unit = {
    pieceMap.values.foreach(svgElement.removeChild)
    pieceMap = Map.empty
    currentPieces = Map.empty
  }

}
