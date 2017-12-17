package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.sam.SAMAction

/**
  *
  */
trait PlaygroundAction extends SAMAction[BasePlaygroundModel] {

//  implicit protected def converter: (M, BasePlaygroundModel) => M
//
//  protected def executeImpl(model: BasePlaygroundModel): Option[BasePlaygroundModel]
//
//  override def execute(model: M): Option[M] = {
//    model match {
//      case m: BasePlaygroundModel => executeImpl(m).map(converter(model, _))
//    }
//  }

}
