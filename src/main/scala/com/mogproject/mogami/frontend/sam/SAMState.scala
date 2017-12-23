package com.mogproject.mogami.frontend.sam

import com.mogproject.mogami.frontend.view.Observable

/**
  *
  */
trait SAMState[M <: SAMModel] {
  def model: M

  def view: SAMView

  def getObserveFlag(newModel: M): Int

  def render(newModel: M): (SAMState[M], Option[SAMAction[M]])

  def initialize(): Unit
}
