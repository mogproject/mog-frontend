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
      Some(model.copy(newMode = ViewMode(gc)))
    case (PlayMode(gc, _), EditModeType) =>
      val st = gc.getDisplayingState
      Some(model.copy(newMode = EditMode(gc.game.gameInfo, st.turn, st.board, st.hand)))
    case (ViewMode(gc), PlayModeType) =>
      Some(model.copy(newMode = PlayMode(gc, newBranchMode = false)))
    case (ViewMode(gc), EditModeType) =>
      val st = gc.getDisplayingState
      Some(model.copy(newMode = EditMode(gc.game.gameInfo, st.turn, st.board, st.hand)))
    case (EditMode(gi, t, b, h), ViewModeType) =>
      Try(GameControl(Game(Branch(State(t, b, h)), gi))).toOption match {
        case Some(gc) => Some(model.copy(newMode = ViewMode(gc)))
        case None => ???
      }
    case (EditMode(gi, t, b, h), PlayModeType) =>
      Try(GameControl(Game(Branch(State(t, b, h)), gi))).toOption match {
        case Some(gc) => Some(model.copy(newMode = PlayMode(gc, newBranchMode = false)))
        case None => ???
      }
    case _ => None
  }
}
