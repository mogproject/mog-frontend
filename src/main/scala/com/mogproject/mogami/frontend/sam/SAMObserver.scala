package com.mogproject.mogami.frontend.sam

/**
  *
  */
trait SAMObserver[M <: SAMModel] {

  def samObserveMask(): Long

  /**
    *
    * @param model model
    * @param flag  -1: all bits on => refresh all
    */
  def refresh(model: M, flag: Long): Unit

  protected def isFlagUpdated(flag: Long, mask: Long): Boolean = (flag & mask) != 0

}
