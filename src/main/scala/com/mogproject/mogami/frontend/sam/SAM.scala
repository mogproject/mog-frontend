package com.mogproject.mogami.frontend.sam

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
  * Implementation of the SAM (State-Action-Model) pattern
  *
  * .  ______Action_______
  * . |        :         |
  * Model      :       View
  * . |______State______|
  *
  * @see http://sam.js.org/
  */
trait SAMLike {
  def initialize(): Unit = {}

  def doAction[M <: SAMModel](action: SAMAction[M]): Unit = {}

  def addObserver[M <: SAMModel](observer: SAMObserver[M]): Unit = {}

  def removeObserver[M <: SAMModel](observer: SAMObserver[M]): Unit = {}
}


class SAM[M <: SAMModel](private[this] var state: SAMState[M]) extends SAMLike {

  override def doAction[N <: SAMModel](action: SAMAction[N]): Unit = action match {
    case a: SAMAction[M] => doActionImpl(a)
    case _ => // do nothing
  }

  @tailrec
  private[this] def doActionImpl(action: SAMAction[M]): Unit = {
    //    SAM.debug(s"doAction: ${action}")

    val result: Option[M] = action.execute(state.model)
    //        println(s"result: ${result}")

    result match {
      case Some(nextModel) =>
        // process observers
        val flag = state.getObserveFlag(nextModel)
        //        SAM.debug(s"Observer flag: ${flag}")

        val (nextState, nextAction) = state.render(nextModel)
        //        SAM.debug(s"nextState: ${nextState}")
        //        SAM.debug(s"nextAction: ${nextAction}")

        notifyObservers(flag, nextModel)

        state = nextState

        nextAction match {
          case Some(a) => doActionImpl(a)
          case None =>
        }
      case None =>
    }
  }

  private[this] val observers: ListBuffer[SAMObserver[M]] = ListBuffer.empty

  override def addObserver[N <: SAMModel](observer: SAMObserver[N]): Unit = observer match {
    case o: SAMObserver[M] => observers.+=:(o)
    case _ => // do nothing
  }

  override def removeObserver[N <: SAMModel](observer: SAMObserver[N]): Unit = observer match {
    case o: SAMObserver[M] => observers -= o
    case _ => // do nothing
  }

  private[this] def notifyObservers(flag: Int, model: M): Unit = observers.foreach { o =>
    //    println(s"Notifying: ${o}")
    if (scalajs.js.isUndefined(o)) {
      //      println(s"Found undefined: ${o}}")
      observers -= o
    } else if ((o.samObserveMask & flag) != 0) {
      //      SAM.debug(s"Refreshing: ${o}")
      o.refresh(model, flag)
    }
  }

  override def initialize(): Unit = {
    state.view.initialize()
    state.initialize()
    notifyObservers(-1, state.model) // -1: all bits on
  }
}

object SAM {

  //  private[this] var verboseLogEnabled: Boolean = false

  //  def setDebugLog(enabled: Boolean): Unit = verboseLogEnabled = enabled

  //  def debug(message: String): Unit = if (verboseLogEnabled) println(message)

  private[this] var samImpl: SAMLike = new SAMLike {}

  /**
    * Do action
    *
    * @param action action
    * @tparam M model
    */
  def doAction[M <: SAMModel](action: SAMAction[M]): Unit = samImpl.doAction(action)

  def initialize[M <: SAMModel](state: SAMState[M]): Unit = {
    samImpl = new SAM(state)
    samImpl.initialize()
    //    debug("SAM Debug mode enabled.")
  }

  def addObserver[M <: SAMModel](observer: SAMObserver[M]): Unit = {
    //    println(s"Adding observer: ${observer}")
    samImpl.addObserver(observer)
  }

  def removeObserver[M <: SAMModel](observer: SAMObserver[M]): Unit = {
    samImpl.removeObserver(observer)
  }

}


