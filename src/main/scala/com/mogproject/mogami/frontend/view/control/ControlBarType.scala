package com.mogproject.mogami.frontend.view.control

object ControlBarType {

  sealed trait ControlBarType

  case object Normal extends ControlBarType

  case object Small extends ControlBarType

  case object LongList extends ControlBarType

}



