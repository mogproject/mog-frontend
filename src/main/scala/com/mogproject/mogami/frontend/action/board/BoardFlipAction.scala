package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board.BoardModel

/**
  *
  */
case class BoardFlipAction(flip: Boolean) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = (model.isFlipped != flip).option(model.copy(isFlipped = flip))
}
