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
class SAMEnvironment[M <: SAMModel]() {
  private[this] var state: SAMState[M] = _

  def initialize(state: SAMState[M]): Unit = {
    state.view.initialize()
    this.state = state
    notifyObservers(-1, state.model) // -1: all bits on
  }

  @tailrec
  final def doAction(action: SAMAction[M]): Unit = {
    val result: Option[M] = action.execute(state.model)

    result match {
      case Some(nextModel) =>
        // process observers
        val flag = state.getObserveFlag(nextModel)
        val (nextState, nextAction) = state.render(nextModel)

        notifyObservers(flag, nextModel)

        state = nextState

        nextAction match {
          case Some(a) => doAction(a)
          case None =>
        }
      case None =>
    }
  }

  private[this] val observers: ListBuffer[SAMObserver[M]] = ListBuffer.empty

  def addObserver[N <: SAMModel](observer: SAMObserver[N]): Unit = observer match {
    case o: SAMObserver[M] => observers.+=:(o)
    case _ => // do nothing
  }

  def removeObserver[N <: SAMModel](observer: SAMObserver[N]): Unit = observer match {
    case o: SAMObserver[M] => observers -= o
    case _ => // do nothing
  }

  private[this] def notifyObservers(flag: Long, model: M): Unit = observers.foreach { o =>
    if (scalajs.js.isUndefined(o)) {
      observers -= o
    } else if ((o.samObserveMask & flag) != 0) {
      o.refresh(model, flag)
    }
  }
}
