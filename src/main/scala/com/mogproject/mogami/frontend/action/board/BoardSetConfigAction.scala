package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, BasePlaygroundModel}
import com.mogproject.mogami.frontend.model.board.BoardConfiguration

/**
  *
  */
case class BoardSetConfigAction[Model <: BasePlaygroundModel](updateConfig: BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends PlaygroundAction[Model] {
  override def execute(model: Model): Option[Model] = {
    ??? //Some(model.copy(config = updateConfig(model.config)))
  }
}
