package com.mogproject.mogami.frontend.sam

import scala.annotation.tailrec

/**
  * Implementation of the SAM (State-Action-Model) pattern
  *
  *    ______Action_______
  *   |        :         |
  * Model      :       View
  *   |______State______|
  *
  * @see http://sam.js.org/
  */
class SAM[M <: SAMModel](private[this] var state: SAMState[M]) extends SAMLike {

  override def doAction[N <: SAMModel](action: SAMAction[N]): Unit = action match {
    case a: SAMAction[M] => doActionImpl(a)
    case _ => // do nothing
  }

  @tailrec
  private[this] def doActionImpl(action: SAMAction[M]): Unit = {
    SAM.debug(s"doAction: ${action}")

    val result = action.execute(state.model)
    SAM.debug(s"result: ${result}")

    result match {
      case Some(nextModel) =>
        val (nextState, nextAction) = state.render(nextModel)
        SAM.debug(s"nextState: ${nextState}")
        SAM.debug(s"nextAction: ${nextAction}")

        state = nextState

        nextAction match {
          case Some(a) => doActionImpl(a)
          case None =>
        }
      case None =>
    }
  }
}

object SAM {

  private[this] final val VERBOSE_LOG_ENABLED: Boolean = false

  protected def debug(message: String): Unit = if (VERBOSE_LOG_ENABLED) println(message)

  private[this] var samImpl: SAMLike = new SAMLike {}

  /**
    * Do action
    * @param action action
    * @tparam M model
    */
  def doAction[M <: SAMModel](action: SAMAction[M]): Unit = samImpl.doAction(action)

  def initialize[M <: SAMModel](state: SAMState[M]): Unit = {
    samImpl = new SAM(state)
    state.view.initialize()
  }
}


