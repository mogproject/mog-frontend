package com.mogproject.mogami.frontend.sam

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.PlaygroundModel


/**
  *
  */
object PlaygroundSAM {
  private[this] val env: SAMEnvironment[PlaygroundModel] = new SAMEnvironment[PlaygroundModel]()

  //
  // Environment accessors
  //
  def initialize(state: SAMState[PlaygroundModel]): Unit = env.initialize(state)

  def doAction(action: PlaygroundAction): Unit = env.doAction(action)

  def addObserver(observer: SAMObserver[PlaygroundModel]): Unit = env.addObserver(observer)

  def removeObserver(observer: SAMObserver[PlaygroundModel]): Unit = env.removeObserver(observer)

  def getState: SAMState[PlaygroundModel] = env.getState
}
