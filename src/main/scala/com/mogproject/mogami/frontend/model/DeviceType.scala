package com.mogproject.mogami.frontend.model

/**
  * Device Types
  */
object DeviceType {

  sealed abstract class DeviceType(val isMobile: Boolean, val isLandscape: Boolean)

  case object PC extends DeviceType(false, false)

  case object MobilePortrait extends DeviceType(true, false)

  case object MobileLandscape extends DeviceType(true, true)

  def apply(isMobile: Boolean, isLandscape: Boolean): DeviceType = (isMobile, isLandscape) match {
    case (true, false) => MobilePortrait
    case (true, true) => MobileLandscape
    case _ => PC
  }
}