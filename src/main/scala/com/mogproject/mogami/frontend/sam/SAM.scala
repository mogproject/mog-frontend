package com.mogproject.mogami.frontend.sam

import scala.annotation.tailrec

/**
  * Implementation of the SAM (State-Action-Model) pattern
  *
  * @see http://sam.js.org/
  */
object SAM {

  private[this] final val VERBOSE_LOG_ENABLED: Boolean = false

  private[this] def debug(message: String): Unit = if (VERBOSE_LOG_ENABLED) println(message)

  private[this] var view: SAMView = NullView

  private[this] var state: SAMState = NullState

  private[this] var model: SAMModel = NullModel

  @tailrec
  def doAction(action: SAMAction): Unit = {
    debug(s"doAction: ${action}")

    val result = action.execute(model)
    debug(s"result: ${result}")

    result match {
      case Some(nextModel) =>
        val (nextState, nextAction) = state.render(nextModel, view)
        debug(s"nextState: ${nextState}")
        debug(s"nextAction: ${nextAction}")

        state = nextState
        model = nextModel
        nextAction match {
          case Some(a) => doAction(a)
          case None =>
        }
      case None =>
    }
  }

  def initialize(newView: SAMView, newState: SAMState, newModel: SAMModel): Unit = {
    view = newView
    state = newState
    model = newModel
    doAction(NullAction)
  }
}


