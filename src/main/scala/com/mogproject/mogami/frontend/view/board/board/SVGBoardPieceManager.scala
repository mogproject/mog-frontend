package com.mogproject.mogami.frontend.view.board.board

import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.frontend.view.board.SVGPieceManager
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{Piece, Ptype, Square}

/**
  *
  */
trait SVGBoardPieceManager extends SVGPieceManager[Square, Piece] {
  self: SVGBoard =>

  override def getPieceRect(key: Square): Rect = getRect(key).toInnerRect(layout.PIECE_FACE_SIZE, layout.PIECE_FACE_SIZE)

  override protected def getNumberRect(key: Square): Rect = ???

  override protected def isFlipped(key: Square, value: Piece) = value.owner.isWhite ^ isFlipped

  override protected def getPtype(key: Square, value: Piece): Ptype = value.ptype

  override protected def shouldDrawNumber(value: Piece): Boolean = false

  override protected def shouldDrawPiece(value: Piece): Boolean = true

  override protected def shouldRemoveSameKey: Boolean = true

  override protected def resetPieceEffect(keepLastMove: Boolean = false): Unit = {
    unselect()
    keepLastMove.fold(effect.lastMoveEffector.restart(), effect.lastMoveEffector.stop())
  }
}
