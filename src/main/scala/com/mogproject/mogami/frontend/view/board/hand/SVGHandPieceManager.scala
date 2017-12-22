package com.mogproject.mogami.frontend.view.board.hand

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.frontend.view.board.SVGPieceManager
import com.mogproject.mogami.util.Implicits._

/**
  *
  */
trait SVGHandPieceManager extends SVGPieceManager[Hand, Int] {
  self: SVGHand =>

  override def getPieceRect(key: Hand): Rect = layout.getPieceRect(key.toPiece, isFlipped)

  override protected def getNumberRect(key: Hand): Rect = layout.getNumberRect(key.toPiece, isFlipped)

  override protected def isFlipped(key: Hand, value: Int): Boolean = key.owner.isWhite ^ isFlipped

  override protected def getPtype(key: Hand, value: Int): Ptype = key.ptype

  override protected def shouldDrawNumber(value: Int): Boolean = value > 1

  override protected def shouldDrawPiece(value: Int): Boolean = value > 0

  override protected def shouldRemoveSameKey: Boolean = false

  override protected def resetPieceEffect(keepLastMove: Boolean = false): Unit = {
    unselect()
    keepLastMove.fold(effect.lastMoveEffector.restart(), effect.lastMoveEffector.stop())
  }
}
