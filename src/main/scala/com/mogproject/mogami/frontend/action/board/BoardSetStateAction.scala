package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.State
import com.mogproject.mogami.frontend.model.board.BoardModel

/**
  *
  */
case class BoardSetStateAction(state: State = State.empty) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = Some(model.copy(activeBoard = state.board, activeHand = state.hand))
}
