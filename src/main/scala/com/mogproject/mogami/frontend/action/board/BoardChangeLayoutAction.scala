package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.view.board.SVGAreaLayout

/**
  *
  */
case class BoardChangeLayoutAction(newLayout: SVGAreaLayout) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] =
    (model.config.layout != newLayout).option(model.copy(config = model.config.copy(layout = newLayout)))
}
