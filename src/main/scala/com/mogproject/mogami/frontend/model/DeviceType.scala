package com.mogproject.mogami.frontend.model

/**
  * Device Types
  */
object DeviceType {

  sealed abstract class DeviceType(val isMobile: Boolean) {
    def setLandscape(isLandscape: Boolean): DeviceType = (isMobile, isLandscape) match {
      case (true, false) => MobilePortrait
      case (true, true) => MobileLandscape
      case _ => PC
    }
  }

  case object PC extends DeviceType(false)

  case object MobilePortrait extends DeviceType(true)

  case object MobileLandscape extends DeviceType(true)

}