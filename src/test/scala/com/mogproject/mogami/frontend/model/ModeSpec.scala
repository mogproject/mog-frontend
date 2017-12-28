package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.core._
import com.mogproject.mogami.core.Player.BLACK
import com.mogproject.mogami.core.Ptype.PAWN
import com.mogproject.mogami.core.{Piece, Square}
import com.mogproject.mogami.core.game.{Branch, Game}
import com.mogproject.mogami.core.move.{IllegalMove, MoveBuilderCsa}
import com.mogproject.mogami.core.state.StateCache
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, MustMatchers}


class ModeSpec extends FlatSpec with MustMatchers with GeneratorDrivenPropertyChecks {

  "Mode#getBoardPieces" must "work with illegal states" in StateCache.withCache { implicit cache =>
    val tr = Branch()
      .makeMove(MoveBuilderCsa.parseCsaString("+7776FU")).get
      .makeMove(MoveBuilderCsa.parseCsaString("-8384FU")).get
      .makeMove(MoveBuilderCsa.parseCsaString("+8833UM")).get
      .makeMove(MoveBuilderCsa.parseCsaString("-2233KA")).get

    val mv = MoveBuilderCsa.parseCsaString("+0026FU").toMove(tr.lastState, tr.lastMoveTo, isStrict = false).get

    val g = Game(tr.updateFinalAction(Some(IllegalMove(mv))))

    PlayMode(GameControl(g, 0, 5)).getBoardPieces.get(Square(2, 6)) mustBe Some(Piece(BLACK, PAWN))
    PlayMode(GameControl(g, 0, 6)).getBoardPieces.get(Square(2, 6)) mustBe Some(Piece(BLACK, PAWN))
  }

  "Mode#getHandPieces" must "work with illegal states" in StateCache.withCache { implicit cache =>
    val tr = Branch()
      .makeMove(MoveBuilderCsa.parseCsaString("+7776FU")).get
      .makeMove(MoveBuilderCsa.parseCsaString("-8384FU")).get
      .makeMove(MoveBuilderCsa.parseCsaString("+8833UM")).get
      .makeMove(MoveBuilderCsa.parseCsaString("-2233KA")).get

    val mv = MoveBuilderCsa.parseCsaString("+0026FU").toMove(tr.lastState, tr.lastMoveTo, isStrict = false).get

    val g = Game(tr.updateFinalAction(Some(IllegalMove(mv))))

    PlayMode(GameControl(g, 0, 5)).getHandPieces.get(Hand(BLACK, PAWN)) mustBe Some(0)
    PlayMode(GameControl(g, 0, 6)).getHandPieces.get(Hand(BLACK, PAWN)) mustBe Some(0)
  }

}
