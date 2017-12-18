package com.mogproject.mogami.frontend.sam

import com.mogproject.mogami.frontend.view.Observer

/**
  *
  */
trait SAMLike {
  def doAction[M <: SAMModel](action: SAMAction[M]): Unit = {}

  def addModelObserver[M <: SAMModel, A](extractor: M => A, observer: Observer[Any]): Unit = {}
}
