package com.mogproject.mogami.frontend.view.event

import com.mogproject.mogami.frontend.view.BrowserInfo
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.{MouseEvent, TouchEvent}

/**
  *
  */
trait EventSensor {

  protected def eventTarget: HTMLElement

  private[this] def isValidMouseEvent(evt: MouseEvent): Boolean = evt.button == 0

  private[this] def isValidTouchEvent(evt: TouchEvent): Boolean = evt.changedTouches.length == 1

  private[this] def getClientPos(evt: MouseEvent): (Double, Double) = (evt.clientX, evt.clientY)

  private[this] def getClientPos(evt: TouchEvent): (Double, Double) = (evt.changedTouches(0).clientX, evt.changedTouches(0).clientY)

  protected def registerMouseAction(eventType: String, action: (Double, Double) => Unit, check: Boolean, preventDefault: Boolean = true): Unit = {
    def f(evt: MouseEvent): Unit = if (!check || isValidMouseEvent(evt)) {
      if (preventDefault) evt.preventDefault()
      val (x, y) = getClientPos(evt)
      action(x, y)
    }

    eventTarget.addEventListener(eventType, f, useCapture = false)
  }

  protected def registerTouchAction(eventType: String, action: (Double, Double) => Unit, check: Boolean, preventDefault: Boolean = true): Unit = if (BrowserInfo.hasTouchEvent) {
    def f(evt: TouchEvent): Unit = if (!check || isValidTouchEvent(evt)) {
      if (preventDefault) evt.preventDefault()
      val (x, y) = getClientPos(evt)
      action(x, y)
    }

    eventTarget.addEventListener(eventType, f, useCapture = false)
  }

}
