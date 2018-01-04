package com.mogproject.mogami.frontend.view.event

import org.scalajs.dom

/**
  * Detects mouse/touch hold
  */
trait PointerHoldSensor extends PointerDownSensor with PointerUpSensor {

  //
  // Variables
  //
  // constants
  protected val holdInterval: Double = 1000 // ms

  // variables
  private[this] var activeHoldEvent: Option[Int] = None


  //
  // Actions
  //
  /**
    *
    * @return true if the interval timer should continue
    */
  protected def pointerHoldAction(): Boolean

  override protected def pointerDownWrapper(f: (Double, Double) => Unit): (Double, Double) => Unit = (x, y) => {
    f(x, y)
    registerHoldEvent()
  }

  override protected def pointerUpWrapper(f: (Double, Double) => Unit): (Double, Double) => Unit = (x, y) => {
    f(x, y)
    clearHoldEvent()
  }

  //
  // Utility
  //
  private[this] def clearHoldEvent(): Unit = activeHoldEvent.foreach { handle =>
    dom.window.clearInterval(handle)
    activeHoldEvent = None
  }

  private[this] def registerHoldEvent(): Unit = {
    clearHoldEvent() // prevent double registrations
    activeHoldEvent = Some(dom.window.setInterval(() => if (!pointerHoldAction()) clearHoldEvent(), holdInterval))
  }

  private[this] def initialize(): Unit = {
    def f(clientX: Double, clientY: Double): Unit = clearHoldEvent()

    registerMouseAction("mouseout", f, check = false, preventDefault = false)
    registerTouchAction("touchcancel", f, check = false, preventDefault = false)
  }

  initialize()
}
