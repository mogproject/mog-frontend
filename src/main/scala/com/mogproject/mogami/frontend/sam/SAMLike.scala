package com.mogproject.mogami.frontend.sam

/**
  *
  */
trait SAMLike {
  def doAction[M <: SAMModel](action: SAMAction[M]): Unit = {}
}
