package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.action.board.BoardCursorEventAction
import com.mogproject.mogami.frontend.model.board.cursor._
import com.mogproject.mogami.frontend.sam.SAM
import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, TouchEvent}
import org.scalajs.dom.raw.HTMLElement

/**
  * Mouse/mobile event handler
  */
trait SVGAreaEventHandler {
  self: SVGArea =>

  //
  // Variables
  //
  // constants
  protected val holdInterval: Double = 1000 // ms
  private[this] val touchEndInterval: Double = 350 // ms

  // variables
  private[this] var activeHoldEvent: Option[Int] = None
  private[this] var lastTouchEnd: Double = -touchEndInterval

  lazy val hasTouchEvent: Boolean = dom.window.hasOwnProperty("ontouchstart")

  //
  // Utility
  //
  def clearHoldEvent(): Unit = activeHoldEvent.foreach { handle =>
    dom.window.clearInterval(handle)
    activeHoldEvent = None
  }

  /**
    * Convert Mouse position to Cursor
    *
    * @param clientX x-coordinate
    * @param clientY y-coordinate
    * @return None if the position is out of interests
    */
  private[this] def getCursor(clientX: Double, clientY: Double): Option[Cursor] = {
    Seq(board, hand, player, box).toStream.flatMap(t => t.clientPos2Cursor(clientX, clientY)).headOption
  }

  private[this] def isValidMouseEvent(evt: MouseEvent): Boolean = evt.button == 0

  private[this] def isValidTouchEvent(evt: TouchEvent): Boolean = evt.changedTouches.length == 1 && lastTouchEnd < evt.timeStamp - touchEndInterval

  private[this] def registerHoldEvent(): Unit = {
    clearHoldEvent() // prevent double registrations
    activeHoldEvent = Some(dom.window.setInterval(() => SAM.doAction(BoardCursorEventAction(MouseHoldEvent)), holdInterval))
  }

  //
  // Event handlers
  //
  private[this] def mouseMove(evt: MouseEvent): Unit = {
    evt.preventDefault()
    SAM.doAction(BoardCursorEventAction(MouseMoveEvent(getCursor(evt.clientX, evt.clientY))))
  }

  private[this] def mouseDown(evt: MouseEvent): Unit = if (isValidMouseEvent(evt)) {
    evt.preventDefault()
    SAM.doAction(BoardCursorEventAction(MouseDownEvent(getCursor(evt.clientX, evt.clientY))))
    registerHoldEvent()
  }

  private[this] def mouseUp(evt: MouseEvent): Unit = if (isValidMouseEvent(evt)) {
    evt.preventDefault()
    SAM.doAction(BoardCursorEventAction(MouseUpEvent(getCursor(evt.clientX, evt.clientY))))
    clearHoldEvent()
  }

  private[this] def mouseOut(evt: MouseEvent): Unit = {
    evt.preventDefault()
    clearHoldEvent()
  }

  private[this] def touchStart(evt: TouchEvent): Unit = if (isValidTouchEvent(evt)) {
    evt.preventDefault()
    SAM.doAction(BoardCursorEventAction(MouseDownEvent(getCursor(evt.changedTouches(0).clientX, evt.changedTouches(0).clientY))))
    registerHoldEvent()
  }

  private[this] def touchEnd(evt: TouchEvent): Unit = {
    evt.preventDefault()
    lastTouchEnd = evt.timeStamp
    SAM.doAction(BoardCursorEventAction(MouseUpEvent(getCursor(evt.changedTouches(0).clientX, evt.changedTouches(0).clientY))))
    clearHoldEvent()
  }

  private[this] def touchCancel(evt: TouchEvent): Unit = {
    evt.preventDefault()
    clearHoldEvent()
  }

  def registerEvents(elem: HTMLElement): Unit = {
    if (hasTouchEvent) {
      elem.addEventListener("touchstart", touchStart, useCapture = false)
      elem.addEventListener("touchend", touchEnd, useCapture = false)
      elem.addEventListener("touchcancel", touchCancel, useCapture = false)
    }

    elem.addEventListener("mousemove", mouseMove, useCapture = false)
    elem.addEventListener("mousedown", mouseDown, useCapture = false)
    elem.addEventListener("mouseup", mouseUp, useCapture = false)
    elem.addEventListener("mouseout", mouseOut, useCapture = false)

  }
}
