package com.mogproject.mogami.frontend.view.board.box

import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.Ptype
import com.mogproject.mogami.frontend.view.board.SVGPieceManager

/**
  *
  */
trait SVGBoxPieceManager extends SVGPieceManager[Ptype, Int] {
  self: SVGBox =>

  override def getPieceRect(key: Ptype): Rect = layout.getPieceRect(key)

  override protected def getNumberRect(key: Ptype): Rect = layout.getNumberRect(key)

  override protected def isFlipped(key: Ptype, value: Int): Boolean = false

  override protected def getPtype(key: Ptype, value: Int): Ptype = key

  override protected def shouldDrawNumber(value: Int): Boolean = value > 1

  override protected def shouldDrawPiece(value: Int): Boolean = value > 0

  override protected def shouldRemoveSameKey: Boolean = false

  override protected def resetPieceEffect(keepLastMove: Boolean = false): Unit = unselect()
}
