package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.Move
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model._

/**
  *
  */
case class AddMovesAction(moves: Seq[Move]) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    val m = model.mode
    m.getGameControl.flatMap { gc =>
      val basePosition = gc.gamePosition
      moves.foldLeft[Option[GameControl]](Some(gc)) {
        case (Some(gcx), mv) =>
          gcx.makeMove(mv, newBranchMode = true, moveForward = true)
        case (None, _) => None
      } map { gc =>
        // go back to the original position
        val nextGC = gc.copy(displayPosition = basePosition.position)
        model.copy(mode = m.setGameControl(nextGC))
      }
    }
  }
}
