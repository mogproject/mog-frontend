package com.mogproject.mogami.frontend.action.manage

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.io.FileWriter
import com.mogproject.mogami.frontend.model.PlaygroundModel
import com.mogproject.mogami.frontend.model.io.RecordFormat

/**
  *
  */
case class SaveRecordAction(format: RecordFormat, fileName: String) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    model.mode.getGameControl.foreach { gc => FileWriter.saveTextFile(gc.getRecord(format), fileName) }
    None
  }
}
