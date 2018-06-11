package com.mogproject.mogami.frontend.sam

import com.mogproject.mogami.Move
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

  //
  // Callbacks
  //
  private[this] var makeMoveCallback: Move => Unit = { _ => {} }
  private[this] var resignCallback: () => Unit = () => {}

  def setMakeMoveCallback(f: Move => Unit): Unit = makeMoveCallback = f

  def setResignCallback(f: () => Unit): Unit = resignCallback = f

  def callbackMakeMove(move: Move): Unit = makeMoveCallback(move)

  def callbackResign(): Unit = resignCallback()
}
