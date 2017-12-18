package com.mogproject.mogami.frontend.action

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.core.state.StateCache.Implicits._

import scala.util.Try

/**
  *
  */
case class ChangeModeAction(newModeType: ModeType, confirmed: Boolean) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = (model.mode, newModeType) match {
    case (PlayMode(gc, _), ViewModeType) =>
      Some(model.copy(newMode = ViewMode(gc), newActiveCursor = None, newSelectedCursor = None))
    case (PlayMode(gc, _), EditModeType) =>
      ??? // warn
    val st = gc.getDisplayingState
      Some(model.copy(newMode = EditMode(gc.game.gameInfo, st.turn, st.board, st.hand), newActiveCursor = None, newSelectedCursor = None))
    case (ViewMode(gc), PlayModeType) =>
      Some(model.copy(newMode = PlayMode(gc, newBranchMode = false), newActiveCursor = None, newSelectedCursor = None))
    case (ViewMode(gc), EditModeType) =>
      val st = gc.getDisplayingState
      Some(model.copy(newMode = EditMode(gc.game.gameInfo, st.turn, st.board, st.hand), newActiveCursor = None, newSelectedCursor = None))
    case (EditMode(gi, t, b, h), ViewModeType) =>
      Try(GameControl(Game(Branch(State(t, b, h)), gi))).toOption match {
        case Some(gc) => Some(model.copy(newMode = ViewMode(gc), newActiveCursor = None, newSelectedCursor = None))
        case None => ???
      }
    case (EditMode(gi, t, b, h), PlayModeType) =>
      Try(GameControl(Game(Branch(State(t, b, h)), gi))).toOption match {
        case Some(gc) => Some(model.copy(newMode = PlayMode(gc, newBranchMode = false), newActiveCursor = None, newSelectedCursor = None))
        case None => ???
      }
    case _ => None
  }
}
