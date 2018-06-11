package com.mogproject.mogami.frontend.action.manage

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{PlaygroundModel, CopyResultMessage}
import com.mogproject.mogami.frontend.model.io.RecordFormat

/**
  *
  */
case class CopyRecordAction(format: RecordFormat) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    Some(model.copy(messageBox = Some(CopyResultMessage(format))))
  }
}
