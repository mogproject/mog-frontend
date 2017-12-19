package com.mogproject.mogami.frontend.action

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.core.state.StateCache.Implicits._

import scala.util.{Failure, Success, Try}

/**
  *
  */
case class ChangeModeAction(newModeType: ModeType, confirmed: Boolean) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = if (model.mode.modeType == newModeType) {
    None
  } else {
    (model.mode, model.mode.getGameControl, newModeType) match {

      // (Play|View) -> Edit (ok)
      case (_, Some(gc), EditModeType) if gc.isInitialState || confirmed =>
        val st = gc.getDisplayingState
        Some(model.copy(newMode = EditMode(gc.game.gameInfo, st.turn, st.board, st.hand)))

      // (Play|View) -> Edit (warning)
      case (_, _, EditModeType) =>
        Some(model.addRenderRequest(EditWarningDialogRequest))

      // Edit -> (Play|View)
      case (EditMode(gi, t, b, h), None, _) =>
        Try(GameControl(Game(Branch(State(t, b, h)), gi))) match {
          case Success(gc) =>
            Some(model.copy(newMode = createMode(newModeType, gc)))
          case Failure(e) =>
            Some(model.addRenderRequest(EditAlertDialogRequest(e.getMessage)))
        }

      // Play -> View | View -> Play
      case (_, Some(gc), _) =>
        Some(model.copy(newMode = createMode(newModeType, gc)))

      // (unexpected)
      case _ =>
        None

    }
  }.map(_.copy(newActiveCursor = None, newSelectedCursor = None))

  private[this] def createMode(modeType: ModeType, gameControl: GameControl): Mode = {
    (modeType == PlayModeType).fold(PlayMode(gameControl), ViewMode(gameControl))
  }
}
