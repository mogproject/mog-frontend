package com.mogproject.mogami.frontend.sam

/**
  *
  */
trait SAMObserver[M <: SAMModel] {

  def samObserveMask: Int

  /**
    *
    * @param model model
    * @param flag  -1: all bits on => refresh all
    */
  def refresh(model: M, flag: Int): Unit

  protected def isFlagUpdated(flag: Int, mask: Int): Boolean = (flag & mask) != 0

  SAM.addObserver(this)

}
