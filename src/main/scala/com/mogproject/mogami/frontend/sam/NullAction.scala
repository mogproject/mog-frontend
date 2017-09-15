package com.mogproject.mogami.frontend.sam

object NullAction extends SAMAction {
  override def execute(model: SAMModel): Option[SAMModel] = Some(model)
}
