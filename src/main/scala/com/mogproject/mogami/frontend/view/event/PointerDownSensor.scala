package com.mogproject.mogami.frontend.view.event


/**
  * React with Mousedown/Touchstart
  */
trait PointerDownSensor extends EventSensor {
  protected def pointerDownAction(clientX: Double, clientY: Double): Unit

  protected def pointerDownWrapper(f: (Double, Double) => Unit): (Double, Double) => Unit = f

  private[this] def initialize(): Unit = {
    registerMouseAction("mousedown", pointerDownWrapper(pointerDownAction), check = true)
    registerTouchAction("touchstart", pointerDownWrapper(pointerDownAction), check = true)
  }

  initialize()
}
