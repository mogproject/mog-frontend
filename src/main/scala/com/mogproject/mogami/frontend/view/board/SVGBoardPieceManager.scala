package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{Piece, Square}
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.Element

/**
  *
  */
trait SVGBoardPieceManager {
  self: SVGBoard =>

  // Local variables
  private[this] var currentPieces: Map[Square, Piece] = Map.empty

  private[this] var currentPieceFace: String = "jp1"

  private[this] var pieceMap: Map[Square, Element] = Map.empty


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
