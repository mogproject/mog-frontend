//package com.mogproject.mogami.frontend.action.board
//
//import com.mogproject.mogami.frontend.action.PlaygroundAction
//import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, BasePlaygroundModel}
//
///**
//  *
//  */
//case class BoardSetConfigAction[M <: BasePlaygroundModel](updateConfig: BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends PlaygroundAction[M] {
//  override def execute(model: M): Option[M] = {
//    ??? //Some(model.copy(config = updateConfig(model.config)))
//  }
//}
