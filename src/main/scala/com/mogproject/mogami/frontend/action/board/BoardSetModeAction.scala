package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.frontend.model.Mode
import com.mogproject.mogami.frontend.model.board.BoardModel

/**
  *
  */
case class BoardSetModeAction(mode: Mode) extends BoardAction {


  // boolean confirmed

  override def execute(model: BoardModel): Option[BoardModel] = Some(model.copy(mode = mode))
}
