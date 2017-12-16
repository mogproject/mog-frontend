package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.Player
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel

/**
  *
  */
case class BoardSetPlayerNameAction[Model <: BasePlaygroundModel](playerNames: Map[Player, Option[String]]) extends PlaygroundAction[Model] {
  override def execute(model: Model): Option[Model] = {
    ???
  }
}
