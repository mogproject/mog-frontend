package com.mogproject.mogami.frontend.sam

/**
  *
  */
trait SAMAction[M <: SAMModel] {
  def execute(model: M): Option[M]
}
