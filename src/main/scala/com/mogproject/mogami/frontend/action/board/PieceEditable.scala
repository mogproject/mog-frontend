package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.core.{Hand, Piece, Player}
import com.mogproject.mogami.core.Ptype.KING
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.EditMode
import com.mogproject.mogami.frontend.model.board.cursor._
import com.mogproject.mogami.util.MapUtil

/**
  *
  */
trait PieceEditable {
  protected def editPiece(mode: EditMode, from: Cursor, to: Cursor): EditMode = {
    (from, to) match {

      // board -> board
      case (BoardCursor(s1), BoardCursor(s2)) =>
        (mode.board(s1), mode.board.get(s2)) match {
          case (p1, Some(_)) if s1 == s2 =>
            // change piece attributes
            mode.copy(board = mode.board.updated(s1, p1.canPromote.fold(p1.promoted, !p1.demoted)))
          case (p1, Some(p2)) =>
            // change pieces
            mode.copy(board = mode.board.updated(s1, p2).updated(s2, p1))
          case (p1, None) =>
            // move to an empty square
            mode.copy(board = mode.board.updated(s2, p1) - s1)

        }

      // board -> hand
      case (BoardCursor(s), HandCursor(h)) if mode.board(s).ptype != KING =>
        val newHand = Hand(h.owner, mode.board(s).ptype.demoted)
        mode.copy(board = mode.board - s, hand = MapUtil.incrementMap(mode.hand, newHand))


      // board -> box
      case (BoardCursor(s), BoxCursor(_)) =>
        mode.copy(board = mode.board - s)

      // hand -> board
      case (HandCursor(h), BoardCursor(s)) if !mode.board.get(s).exists(_.ptype == KING) =>
        val hx = MapUtil.decrementMap(mode.hand, h)
        val hy = mode.board.get(s).map { p => MapUtil.incrementMap(hx, Hand(h.owner, p.ptype.demoted)) }.getOrElse(hx)
        mode.copy(board = mode.board.updated(s, h.toPiece), hand = hy)

      // hand -> hand
      case (HandCursor(h1), HandCursor(h2)) if h1.owner != h2.owner =>
        val hx = MapUtil.decrementMap(mode.hand, h1)
        val hy = MapUtil.incrementMap(hx, Hand(!h1.owner, h1.ptype))
        mode.copy(hand = hy)

      // hand -> box
      case (HandCursor(h), BoxCursor(_)) =>
        mode.copy(hand = MapUtil.decrementMap(mode.hand, h))

      // box -> board
      case (BoxCursor(pt), BoardCursor(s)) =>
        mode.copy(board = mode.board.updated(s, Piece(Player.BLACK, pt)))

      // box -> hand
      case (BoxCursor(pt), HandCursor(h)) if pt != KING =>
        mode.copy(hand = MapUtil.incrementMap(mode.hand, Hand(h.owner, pt)))

      case _ =>
        mode
    }
  }
}
