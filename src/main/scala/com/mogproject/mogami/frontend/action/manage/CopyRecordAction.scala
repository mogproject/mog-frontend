package com.mogproject.mogami.frontend.action.manage

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, CopyResultMessage}
import com.mogproject.mogami.frontend.model.io.RecordFormat

/**
  *
  */
case class CopyRecordAction(format: RecordFormat) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    Some(model.copy(newMessageBox = Some(CopyResultMessage(format))))
  }
}
