package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami._

/**
  * cursor
  */
sealed abstract class Cursor(private val regionId: Int) {
  def isSameRegion(cursor: Cursor): Boolean = this.regionId == cursor.regionId

  def isBoard: Boolean = regionId == 0

  def isPlayer: Boolean = regionId == 3

  def unary_! : Cursor
}

case class BoardCursor(square: Square) extends Cursor(0) {
  override def unary_! : Cursor = BoardCursor(!square)

  def moveFrom: MoveFrom = Left(square)
}

case class HandCursor(hand: Hand) extends Cursor(1) {
  override def unary_! : Cursor = HandCursor(!hand)

  def moveFrom: MoveFrom = Right(hand)
}

case class BoxCursor(ptype: Ptype) extends Cursor(2) {
  override def unary_! : Cursor = this
}

case class PlayerCursor(player: Player) extends Cursor(3) {
  override def unary_! : Cursor = PlayerCursor(!player)
}

object HandCursor {
  def apply(player: Player, ptype: Ptype): HandCursor = new HandCursor(Hand(player, ptype))

  def apply(piece: Piece): HandCursor = new HandCursor(Hand(piece))
}
