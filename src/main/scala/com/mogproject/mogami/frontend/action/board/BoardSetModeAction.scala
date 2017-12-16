package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, Mode}

/**
  *
  */
case class BoardSetModeAction[Model <: BasePlaygroundModel](mode: Mode) extends PlaygroundAction[Model] {


  // boolean confirmed

  override def execute(model: Model): Option[Model] = {
    ??? //Some(model.copy(mode = mode))
  }
}
