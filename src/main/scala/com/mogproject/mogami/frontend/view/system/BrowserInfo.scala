package com.mogproject.mogami.frontend.view.system

import com.mogproject.mogami.frontend.api.{MobileScreen, MobileWindow}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom

import scala.scalajs.js.UndefOr

/**
  *
  */
object BrowserInfo {

  //
  // Constants
  //
  private[this] final val LANDSCAPE_MARGIN_HEIGHT: Int = 44
  private[this] final val PORTRAIT_MARGIN_HEIGHT: Int = LANDSCAPE_MARGIN_HEIGHT * 2 + 20


  private[this] def getAngle1: UndefOr[Int] = dom.window.asInstanceOf[MobileWindow].orientation

  private[this] def getAngle2: UndefOr[Int] = dom.window.screen.asInstanceOf[MobileScreen].orientation.flatMap(_.angle)

  // possibly using an in-app browser
  private[this] def isInAppBrowser: Boolean = isMobile && (isLandscape ^ dom.window.innerWidth > dom.window.innerHeight)

  /** true if the device accepts touches */
  lazy val hasTouchEvent: Boolean = dom.window.hasOwnProperty("ontouchstart")

  lazy val isMobile: Boolean = math.min(dom.window.screen.width, dom.window.screen.height) < 768

  var isAnimationSupported: Boolean = true

  lazy val isSoundSupported: Boolean = dom.window.hasOwnProperty("audioContext")

  /**
    *
    * @return true if the orientation is the landscape mode, false if the portrait mode
    */
  def isLandscape: Boolean = math.abs(getAngle1.getOrElse(getAngle2.getOrElse(0))) == 90

  def getScreenWidth: Double = (isMobile, isLandscape) match {
    case (true, true) => math.max(dom.window.screen.width, dom.window.screen.height)
    case (true, false) => math.min(dom.window.screen.width, dom.window.screen.height)
    case (false, _) => dom.window.screen.width
  }

  def getScreenHeight: Double = (isMobile, isLandscape) match {
    case (true, true) => math.min(dom.window.screen.width, dom.window.screen.height)
    case (true, false) => math.max(dom.window.screen.width, dom.window.screen.height)
    case (false, _) => dom.window.screen.height
  }

  def getClientWidth: Double = isInAppBrowser.fold(getScreenWidth, dom.window.innerWidth)

  def getClientHeight: Double = if (isInAppBrowser) {
    getScreenHeight - isLandscape.fold(LANDSCAPE_MARGIN_HEIGHT, PORTRAIT_MARGIN_HEIGHT)
  } else {
    math.min(dom.window.innerHeight, getScreenHeight - LANDSCAPE_MARGIN_HEIGHT)
  }

}
