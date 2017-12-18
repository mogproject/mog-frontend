package com.mogproject.mogami.frontend.view

import scala.collection.mutable.ListBuffer

/**
  *
  */
trait Observable[T] {
  //  this: T =>

  private[this] val observers = ListBuffer[Observer[T]]()

  def addObserver(observer: Observer[T]): Unit = observer match {
    case obs: Observer[T] => observers.+=:(obs)
    case _ =>
  }

  def notifyObservers(value: T): Unit = {
    observers.foreach(_.handleUpdate(value))
  }
}
