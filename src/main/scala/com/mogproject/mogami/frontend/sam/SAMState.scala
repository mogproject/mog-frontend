package com.mogproject.mogami.frontend.sam

import com.mogproject.mogami.frontend.view.Observable

/**
  *
  */
trait SAMState[M <: SAMModel] {
  def model: M

  def view: SAMView

  def render(newModel: M, observables: Map[M => Any, Observable[Any]]): (SAMState[M], Option[SAMAction[M]])

  def initialize(): Unit
}
