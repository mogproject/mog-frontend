package com.mogproject.mogami.frontend.sam

/**
  *
  */
trait SAMState {
  def render(model: SAMModel, view: SAMView): (SAMState, Option[SAMAction])
}
