package com.mogproject.mogami.frontend.sam

/**
  *
  */
trait SAMObserver[M <: SAMModel] {

  def samObserveMask: Int

  def refresh(model: M): Unit

  SAM.addObserver(this)

}
