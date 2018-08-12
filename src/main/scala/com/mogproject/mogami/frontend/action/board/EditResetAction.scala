package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{PlaygroundModel, EditMode}

/**
  *
  */
case class EditResetAction(initialState: State) extends PlaygroundAction{
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    model.mode.isEditMode.option(model.copy(mode = EditMode(GameInfo(), initialState.turn, initialState.board, initialState.hand, None), selectedCursor = None))
  }
}
