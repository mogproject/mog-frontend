package com.mogproject.mogami

package object frontend {

  val SAM = com.mogproject.mogami.frontend.sam.SAM
  val PlaygroundSAM = com.mogproject.mogami.frontend.sam.PlaygroundSAM

  type SAMModel = com.mogproject.mogami.frontend.sam.SAMModel
  type SAMAction[M <: SAMModel] = com.mogproject.mogami.frontend.sam.SAMAction[M]
  type SAMState[M <: SAMModel] = com.mogproject.mogami.frontend.sam.SAMState[M]
  type SAMView = com.mogproject.mogami.frontend.sam.SAMView

  type BasePlaygroundModel = com.mogproject.mogami.frontend.model.BasePlaygroundModel

  type GameControl = com.mogproject.mogami.frontend.model.GameControl
  val GameControl = com.mogproject.mogami.frontend.model.GameControl

  type BasePlaygroundConfiguration = com.mogproject.mogami.frontend.model.BasePlaygroundConfiguration
  val BasePlaygroundConfiguration = com.mogproject.mogami.frontend.model.BasePlaygroundConfiguration

  type SAMObserver[M <: SAMModel] = com.mogproject.mogami.frontend.sam.SAMObserver[M]
  val ObserveFlag = com.mogproject.mogami.frontend.state.ObserveFlag

  type Coord = com.mogproject.mogami.frontend.view.coordinate.Coord
  val Coord = com.mogproject.mogami.frontend.view.coordinate.Coord

  type Rect = com.mogproject.mogami.frontend.view.coordinate.Rect
  val Rect = com.mogproject.mogami.frontend.view.coordinate.Rect

  type DeviceType = com.mogproject.mogami.frontend.model.DeviceType.DeviceType
  val DeviceType = com.mogproject.mogami.frontend.model.DeviceType

  type Language = com.mogproject.mogami.frontend.model.Language
  val Language = com.mogproject.mogami.frontend.model.Language
  val English = com.mogproject.mogami.frontend.model.English
  val Japanese = com.mogproject.mogami.frontend.model.Japanese

  type PieceFace = com.mogproject.mogami.frontend.model.PieceFace
  val PieceFace = com.mogproject.mogami.frontend.model.PieceFace

  type WebComponent = com.mogproject.mogami.frontend.view.WebComponent
  val WebComponent = com.mogproject.mogami.frontend.view.WebComponent

  type Mode = com.mogproject.mogami.frontend.model.Mode
  type ModeType = com.mogproject.mogami.frontend.model.ModeType
  val PlayModeType = com.mogproject.mogami.frontend.model.PlayModeType
  val ViewModeType = com.mogproject.mogami.frontend.model.ViewModeType
  val EditModeType = com.mogproject.mogami.frontend.model.EditModeType
  val LiveModeType = com.mogproject.mogami.frontend.model.LiveModeType

  val Tooltip = com.mogproject.mogami.frontend.api.bootstrap.Tooltip
  type BootstrapJQuery = com.mogproject.mogami.frontend.api.bootstrap.BootstrapJQuery

}
