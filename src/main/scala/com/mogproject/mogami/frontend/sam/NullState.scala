package com.mogproject.mogami.frontend.sam

object NullState extends SAMState {
  override def render(model: SAMModel, view: SAMView): (SAMState, Option[SAMAction]) = (this, None)
}
