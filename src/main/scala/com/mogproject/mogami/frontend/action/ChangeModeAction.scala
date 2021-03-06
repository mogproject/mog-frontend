package com.mogproject.mogami.frontend.action

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache

import scala.util.{Failure, Success, Try}

/**
  *
  */
case class ChangeModeAction(newModeType: ModeType, confirmed: Boolean) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = if (model.mode.modeType == newModeType) {
    None
  } else {
    (model.mode, model.mode.getGameControl, newModeType) match {

      // (Play|View) -> Edit (ok)
      case (mode, Some(gc), EditModeType) if gc.isInitialState || confirmed =>
        Some(model.copy(
          mode = EditMode(gc.game.gameInfo, gc.getDisplayingTurn, gc.getDisplayingBoard, gc.getDisplayingHand, Some(mode.isHandicapped)))
        )

      // (Play|View) -> Edit (warning)
      case (_, _, EditModeType) =>
        Some(model.copy(messageBox = Some(HandleDialogMessage(EditWarningDialog))))

      // Edit -> (Play|View)
      case (EditMode(gi, t, b, h, _), None, _) =>
        Try(GameControl(Game(Branch(State(t, b, h), isFreeMode = model.config.freeMode), gameInfo = gi))) match {
          case Success(gc) =>
            Some(model.copy(mode = createMode(newModeType, gc, None)))
          case Failure(e) =>
            Some(model.copy(messageBox = Some(HandleDialogMessage(EditAlertDialog(e.getMessage)))))
        }

      // Play -> View | View -> Play
      case (mode, Some(gc), _) =>
        Some(model.copy(mode = createMode(newModeType, gc, Some(mode.isHandicapped))))

      // (unexpected)
      case _ =>
        None

    }
  }.map(_.copy(activeCursor = None, selectedCursor = None))

  private[this] def createMode(modeType: ModeType, gameControl: GameControl, isHandicappedHint: Option[Boolean]): Mode = {
    (modeType == PlayModeType).fold(PlayMode(gameControl, isHandicappedHint), ViewMode(gameControl, isHandicappedHint))
  }
}
