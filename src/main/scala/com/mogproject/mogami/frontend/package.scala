package com.mogproject.mogami

package object frontend {

  val SAM = com.mogproject.mogami.frontend.sam.SAM
  val PlaygroundSAM = com.mogproject.mogami.frontend.sam.PlaygroundSAM

  type SAMModel = com.mogproject.mogami.frontend.sam.SAMModel
  type SAMAction[M <: SAMModel] = com.mogproject.mogami.frontend.sam.SAMAction[M]
  type SAMState[M <: SAMModel] = com.mogproject.mogami.frontend.sam.SAMState[M]
  type SAMView = com.mogproject.mogami.frontend.sam.SAMView

  type Coord = com.mogproject.mogami.frontend.view.coordinate.Coord
  val Coord = com.mogproject.mogami.frontend.view.coordinate.Coord

  type Rect = com.mogproject.mogami.frontend.view.coordinate.Rect
  val Rect = com.mogproject.mogami.frontend.view.coordinate.Rect

  type Language = com.mogproject.mogami.frontend.model.Language
  val Language = com.mogproject.mogami.frontend.model.Language
  val English = com.mogproject.mogami.frontend.model.English
  val Japanese = com.mogproject.mogami.frontend.model.Japanese

  type PieceFace = com.mogproject.mogami.frontend.model.PieceFace
  val PieceFace = com.mogproject.mogami.frontend.model.PieceFace

  type WebComponent = com.mogproject.mogami.frontend.view.WebComponent
  val WebComponent = com.mogproject.mogami.frontend.view.WebComponent
}
