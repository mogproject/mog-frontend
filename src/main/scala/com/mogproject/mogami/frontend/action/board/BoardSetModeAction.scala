//package com.mogproject.mogami.frontend.action.board
//
//import com.mogproject.mogami.frontend.action.PlaygroundAction
//import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, Mode}
//
///**
//  *
//  */
//case class BoardSetModeAction[+M <: BasePlaygroundModel](mode: Mode) extends PlaygroundAction[M] {
//
//
//  // boolean confirmed
//
//  override def execute[N <: BasePlaygroundModel](model: N): Option[M] = {
//    ??? //Some(model.copy(mode = mode))
//  }
//}
