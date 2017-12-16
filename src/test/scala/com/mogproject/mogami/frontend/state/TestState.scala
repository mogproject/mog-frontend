package com.mogproject.mogami.frontend.state

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model.TestModel
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.{Japanese, TestView}

/**
  *
  */
case class TestState(model: TestModel, view: TestView) extends SAMState[TestModel] {
  type Model = TestModel

  override def render(newModel: Model): (SAMState[Model], Option[SAMAction[Model]]) = renderImpl(newModel)

  private[this] def renderImpl(newModel: TestModel, renderAll: Boolean = false): (SAMState[Model], Option[SAMAction[Model]]) = {

    val fs: Seq[(Boolean, Model => Model)] = Seq(
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas), renderLayout),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.pieceWidth), renderSize),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.recordLang), renderIndex),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType), renderFlip),
      (renderAll || isUpdated(newModel, _.config.layout, _.mode.getPlayerNames), renderPlayerNames),
      (renderAll || isUpdated(newModel, _.config.layout, _.mode.getIndicators), renderIndicators),
      (renderAll || isUpdated(newModel, _.config.layout, _.mode.boxAvailable), renderBox),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.pieceFace, _.mode.getBoardPieces), renderBoardPieces),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.pieceFace, _.mode.getHandPieces), renderHandPieces),
      (newModel.mode.boxAvailable && (renderAll || isUpdated(newModel, _.config.layout, _.mode.boxAvailable, _.config.pieceFace, _.mode.getBoardPieces, _.mode.getHandPieces)), renderBoxPieces),
      (renderAll || isUpdated(newModel, _.activeCursor), renderActiveCursor),
      (renderAll || isUpdated(newModel, _.selectedCursor), renderSelectedCursor)
    )

    val nextModel = fs.foldLeft(newModel) { case (m, (cond, f)) => if (cond) f(m) else m }
    (this.copy(model = nextModel), None)
  }

  override def initialize(): Unit = {
    renderImpl(model, renderAll = true)
  }

  //
  // Utilities
  //
  private[this] def isUpdated[A](newModel: Model, fs: (Model => A)*): Boolean = {
    fs.exists(f => f(model) != f(newModel))
  }

  //
  // Rendering functions
  //
  private[this] def renderLayout(newModel: Model): Model = {
    view.renderLayout(newModel.config.flipType.numAreas, newModel.config.layout)
    newModel
  }

  private[this] def renderSize(newModel: Model): Model = {
    view.renderSize(newModel.config.getPieceWidth)
    newModel
  }

  private[this] def renderIndex(newModel: Model): Model = {
    view.renderIndex(newModel.config.recordLang == Japanese)
    newModel
  }

  private[this] def renderFlip(newModel: Model): Model = {
    view.renderFlip(newModel.config.flipType)
    newModel
  }

  private[this] def renderPlayerNames(newModel: Model): Model = {
    view.renderPlayerNames(newModel.mode.getPlayerNames)
    newModel
  }

  private[this] def renderIndicators(newModel: Model): Model = {
    view.renderIndicators(newModel.mode.getIndicators)
    newModel
  }

  private[this] def renderBox(newModel: Model): Model = {
    view.renderBox(newModel.mode.boxAvailable)
    newModel

  }

  private[this] def renderBoardPieces(newModel: Model): Model = {
    view.renderBoardPieces(newModel.mode.getBoardPieces, newModel.config.pieceFace)
    newModel
  }

  private[this] def renderHandPieces(newModel: Model): Model = {
    view.renderHandPieces(newModel.mode.getHandPieces, newModel.config.pieceFace)
    newModel
  }

  private[this] def renderBoxPieces(newModel: Model): Model = {
    view.renderBoxPieces(newModel.mode.getBoxPieces, newModel.config.pieceFace)
    newModel
  }

  private[this] def renderActiveCursor(newModel: Model): Model = {
    view.renderActiveCursor(newModel.activeCursor)
    newModel
  }

  private[this] def renderSelectedCursor(newModel: Model): Model = {
    view.renderSelectedCursor(newModel.selectedCursor, newModel.config.visualEffectEnabled)
    newModel
  }


}