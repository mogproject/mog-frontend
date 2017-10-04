package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.frontend.model.board.{BoardConfiguration, BoardModel}

/**
  *
  */
case class BoardSetConfigAction(updateConfig: BoardConfiguration => BoardConfiguration) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = Some(model.copy(config = updateConfig(model.config)))
}
