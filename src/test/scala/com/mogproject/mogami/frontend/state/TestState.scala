package com.mogproject.mogami.frontend.state

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model.{CursorFlashRequest, GameInfoDialogRequest, PromotionDialogRequest, TestModel}
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.board.Cursor
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
      (renderAll || isUpdated(newModel, _.config.layout, _.mode.getGameControl.map(_.getDisplayingLastMove)), renderLastMove),
      (newModel.mode.boxAvailable && (renderAll || isUpdated(newModel, _.config.layout, _.mode.boxAvailable, _.config.pieceFace, _.mode.getBoardPieces, _.mode.getHandPieces)), renderBoxPieces),
      (renderAll || isUpdated(newModel, _.activeCursor), renderActiveCursor),
      (renderAll || isUpdated(newModel, _.selectedCursor), renderSelectedCursor),
      (newModel.renderRequests.nonEmpty, processRenderRequests)
    )

    val nextModel = fs.foldLeft(newModel) { case (m, (cond, f)) => if (cond) f(m) else m }
    if (newModel.mode.isJustMoved(model.mode)) renderMove(newModel)
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
    view.renderPlayerNames(newModel.mode.getPlayerNames, newModel.config.messageLang)
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

  private[this] def renderLastMove(newModel: Model): Model = {
    view.renderLastMove(newModel.mode.getLastMove)
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
    val attack = for {
      c <- newModel.selectedCursor.toSet if newModel.config.visualEffectEnabled
      from = c.moveFrom
      atk <- newModel.mode.getLegalMoves(from)
    } yield atk
    view.renderSelectedCursor(newModel.selectedCursor, newModel.config.visualEffectEnabled, attack)
    newModel
  }

  private[this] def processRenderRequests(newModel: Model): Model = {
    newModel.renderRequests.foreach {
      case PromotionDialogRequest(rawMove: Move) => ???
      case CursorFlashRequest(cursor: Cursor) => view.mainPane.updateSVGArea(_.flashCursor(cursor))
      case GameInfoDialogRequest => ???
    }
    TestModel.adapter(newModel, newModel.copy(newRenderRequests = Seq.empty))
  }

  private[this] def renderMove(newModel: Model): Unit = {
    newModel.mode.getLastMove.foreach(view.renderMoveEffect(_, newModel.config.pieceFace, newModel.config.visualEffectEnabled, newModel.config.soundEffectEnabled))
  }

}