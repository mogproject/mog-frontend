package com.mogproject.mogami.frontend.sam

/**
  *
  */
trait SAMState[M <: SAMModel] {
  def model: M

  def view: SAMView

  def getObserveFlag(newModel: M): Long

  def render(newModel: M): (SAMState[M], Option[SAMAction[M]])

  def initialize(): Unit
}
