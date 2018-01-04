package com.mogproject.mogami.frontend.view.event

/**
  * React with Mouseup/Touchend
  */
trait PointerUpSensor extends EventSensor {
  protected def pointerUpAction(clientX: Double, clientY: Double): Unit

  protected def pointerUpWrapper(f: (Double, Double) => Unit): (Double, Double) => Unit = f

  private[this] def initialize(): Unit = {
    registerMouseAction("mouseup", pointerUpWrapper(pointerUpAction), check = true)
    registerTouchAction("touchend", pointerUpWrapper(pointerUpAction), check = true)
  }

  initialize()
}
