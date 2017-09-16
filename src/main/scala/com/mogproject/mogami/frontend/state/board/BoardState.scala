package com.mogproject.mogami.frontend.state.board

import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.board.BoardView

/**
  *
  */
case class BoardState(model: BoardModel = BoardModel(None), view: BoardView = new BoardView {}) extends SAMState[BoardModel] {

  override def render(newModel: BoardModel): (SAMState[BoardModel], Option[SAMAction[BoardModel]]) = {
    newModel.activeCursor match {
      case x if x == model.activeCursor =>
      case Some(sq) => view.board.startCursorEffect(sq)
      case None => view.board.stopCursorEffect()
    }

    (copy(model = newModel), None)
  }
}
