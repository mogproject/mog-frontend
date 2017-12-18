package com.mogproject.mogami.frontend.model

/**
  * Mode Type
  */
sealed trait ModeType

case object PlayModeType extends ModeType

case object ViewModeType extends ModeType

case object LiveModeType extends ModeType

case object EditModeType extends ModeType
