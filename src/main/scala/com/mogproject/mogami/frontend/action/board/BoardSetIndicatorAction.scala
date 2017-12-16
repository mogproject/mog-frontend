//package com.mogproject.mogami.frontend.action.board
//
//import com.mogproject.mogami.Player
//import com.mogproject.mogami.frontend.action.PlaygroundAction
//import com.mogproject.mogami.frontend.model.BasePlaygroundModel
//import com.mogproject.mogami.frontend.model.board.{BoardIndicator}
//
///**
//  *
//  */
//case class BoardSetIndicatorAction[Model <: BasePlaygroundModel](turn: Player, indicator: Option[BoardIndicator]) extends PlaygroundAction[Model] {
//  override def execute(model: Model): Option[Model] = {
//    Some(model.copy(indicators = model.indicators + (turn -> indicator)))
//  }
//}
