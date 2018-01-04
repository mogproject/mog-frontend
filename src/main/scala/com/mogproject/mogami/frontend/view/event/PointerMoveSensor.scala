package com.mogproject.mogami.frontend.view.event

/**
  * React with Mousemove/Touchmove
  */
trait PointerMoveSensor extends EventSensor {

  protected def pointerMoveAction(clientX: Double, clientY: Double): Unit

  private[this] def initialize(): Unit = {
    registerMouseAction("mousemove", pointerMoveAction, check = false)
    registerTouchAction("touchmove", pointerMoveAction, check = true)
  }

  initialize()

}
