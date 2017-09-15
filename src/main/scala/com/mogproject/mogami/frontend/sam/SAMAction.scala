package com.mogproject.mogami.frontend.sam

/**
  *
  */
trait SAMAction {
  def execute(model: SAMModel): Option[SAMModel]
}
