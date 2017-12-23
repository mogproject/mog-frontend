package com.mogproject.mogami.frontend.sam

import com.mogproject.mogami.frontend.view.{Observable, Observer}

import scala.annotation.tailrec
import scala.collection.mutable
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

  //  def addModelObserver[A](extractor: Any => A, observer: Observer[Any]): Unit = {}
}


class SAM[M <: SAMModel](private[this] var state: SAMState[M]) extends SAMLike {

  override def doAction[N <: SAMModel](action: SAMAction[N]): Unit = action match {
    case a: SAMAction[M] => doActionImpl(a)
    case _ => // do nothing
  }

  @tailrec
  private[this] def doActionImpl(action: SAMAction[M]): Unit = {
    SAM.debug(s"doAction: ${action}")

    val result: Option[M] = action.execute(state.model)
    SAM.debug(s"result: ${result}")

    result match {
      case Some(nextModel) =>
        // process observers
        val flag = state.getObserveFlag(nextModel)
        SAM.debug(s"Observer flag: ${flag}")

        observers.foreach { o =>
          if ((o.samObserveMask & flag) != 0) {
            SAM.debug(s"Refreshing: ${o}")
            o.refresh(nextModel)
          }
        }

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

  private[this] val observers: ListBuffer[SAMObserver[M]] = ListBuffer.empty

  override def addObserver[N <: SAMModel](observer: SAMObserver[N]): Unit = observer match {
    case o: SAMObserver[M] => observers.+=:(o)
    case _ => // do nothing
  }

  //  private[this] val observables: mutable.Map[M => Any, Observable[Any]] = mutable.Map.empty
  //
  //  override def addModelObserver[A](extractor: Any => A, observer: Observer[Any]): Unit = extractor match {
  //    case f: (M => A) => addModelObserverImpl(f, observer)
  //    case _ => //do nothing
  //  }
  //
  //  def addModelObserverImpl[A](extractor: M => A, observer: Observer[Any]): Unit = {
  //    observables.get(extractor) match {
  //      case Some(obs) => obs.addObserver(observer)
  //      case None =>
  //        val obs = new Observable[Any] {}
  //        obs.addObserver(observer)
  //        observables += (extractor -> obs)
  //    }
  //  }

  override def initialize(): Unit = {
    state.view.initialize()
//    state.initialize(observables.toMap)
  }
}

object SAM {

  private[this] final val VERBOSE_LOG_ENABLED: Boolean = false

  protected def debug(message: String): Unit = if (VERBOSE_LOG_ENABLED) println(message)

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
    debug("SAM Debug mode enabled.")
  }

//  @deprecated
//  def addModelObserver[A](extractor: Any => A, observer: Observer[Any]): Unit = {
//    samImpl.addModelObserver(extractor, observer)
//  }


  def addObserver[M <: SAMModel](observer: SAMObserver[M]): Unit = {
    samImpl.addObserver(observer)
  }
}


