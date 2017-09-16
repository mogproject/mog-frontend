package com.mogproject.mogami.frontend.sam

/**
  *
  */
trait SAMState[M <: SAMModel] {
  def model: M

  def view: SAMView

  def render(newModel: M): (SAMState[M], Option[SAMAction[M]])
}
