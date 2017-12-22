package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, EditMode}

/**
  *
  */
case class EditResetAction(initialState: State) extends PlaygroundAction{
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    model.mode.isEditMode.option(model.copy(newMode = EditMode(GameInfo(), initialState.turn, initialState.board, initialState.hand)))
  }
}
