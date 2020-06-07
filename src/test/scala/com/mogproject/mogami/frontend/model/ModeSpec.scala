package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.core._
import com.mogproject.mogami.core.Player.BLACK
import com.mogproject.mogami.core.Ptype.PAWN
import com.mogproject.mogami.core.{Piece, Square}
import com.mogproject.mogami.core.game.{Branch, Game}
import com.mogproject.mogami.core.move.{IllegalMove, MoveBuilderCsa}
import com.mogproject.mogami.core.state.StateCache
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers


class ModeSpec extends AnyFlatSpec with Matchers {

  "Mode#getBoardPieces" must "work with illegal states" in StateCache.withCache { implicit cache =>
    val tr = Branch()
      .makeMove(MoveBuilderCsa.parseCsaString("+7776FU")).get
      .makeMove(MoveBuilderCsa.parseCsaString("-8384FU")).get
      .makeMove(MoveBuilderCsa.parseCsaString("+8833UM")).get
      .makeMove(MoveBuilderCsa.parseCsaString("-2233KA")).get

    val mv = MoveBuilderCsa.parseCsaString("+0026FU").toMove(tr.lastState, tr.lastMoveTo, isStrict = false).get

    val g = Game(tr.updateFinalAction(Some(IllegalMove(mv))))

    PlayMode(GameControl(g, 0, 5), None).getBoardPieces.get(Square(2, 6)) mustBe Some(Piece(BLACK, PAWN))
    PlayMode(GameControl(g, 0, 6), None).getBoardPieces.get(Square(2, 6)) mustBe Some(Piece(BLACK, PAWN))
  }

  "Mode#getHandPieces" must "work with illegal states" in StateCache.withCache { implicit cache =>
    val tr = Branch()
      .makeMove(MoveBuilderCsa.parseCsaString("+7776FU")).get
      .makeMove(MoveBuilderCsa.parseCsaString("-8384FU")).get
      .makeMove(MoveBuilderCsa.parseCsaString("+8833UM")).get
      .makeMove(MoveBuilderCsa.parseCsaString("-2233KA")).get

    val mv = MoveBuilderCsa.parseCsaString("+0026FU").toMove(tr.lastState, tr.lastMoveTo, isStrict = false).get

    val g = Game(tr.updateFinalAction(Some(IllegalMove(mv))))

    PlayMode(GameControl(g, 0, 5), None).getHandPieces.get(Hand(BLACK, PAWN)) mustBe Some(0)
    PlayMode(GameControl(g, 0, 6), None).getHandPieces.get(Hand(BLACK, PAWN)) mustBe Some(0)
  }

}
