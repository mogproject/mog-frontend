package com.mogproject.mogami.frontend.action.manage

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, CopyRecordRequest}
import com.mogproject.mogami.frontend.model.io.RecordFormat

/**
  *
  */
case class CopyRecordAction(format: RecordFormat) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    Some(model.addRenderRequest(CopyRecordRequest(format)))
  }
}
