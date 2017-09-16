package com.mogproject.mogami

package object frontend {

  val SAM = com.mogproject.mogami.frontend.sam.SAM

  type SAMModel = com.mogproject.mogami.frontend.sam.SAMModel
  type SAMAction[M <: SAMModel] = com.mogproject.mogami.frontend.sam.SAMAction[M]
  type SAMState[M <: SAMModel] = com.mogproject.mogami.frontend.sam.SAMState[M]
  type SAMView = com.mogproject.mogami.frontend.sam.SAMView

  type Coord = com.mogproject.mogami.frontend.view.coordinate.Coord
  val Coord = com.mogproject.mogami.frontend.view.coordinate.Coord

  type Rect = com.mogproject.mogami.frontend.view.coordinate.Rect
  val Rect = com.mogproject.mogami.frontend.view.coordinate.Rect

}
