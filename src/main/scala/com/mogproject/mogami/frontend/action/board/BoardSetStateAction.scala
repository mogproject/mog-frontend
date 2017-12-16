//package com.mogproject.mogami.frontend.action.board
//
//import com.mogproject.mogami.State
//import com.mogproject.mogami.frontend.action.PlaygroundAction
//import com.mogproject.mogami.frontend.model.BasePlaygroundModel
//
///**
//  *
//  */
//case class BoardSetStateAction[Model <: BasePlaygroundModel](state: State = State.empty) extends PlaygroundAction[Model] {
//  override def execute(model: BoardModel): Option[BoardModel] = Some(model.copy(activeBoard = state.board, activeHand = state.hand))
//}
