package com.mogproject.mogami.frontend.state

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model.board.cursor.Cursor
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.{BasePlaygroundView, Japanese, Observable}

/**
  *
  */
trait BasePlaygroundState[M <: BasePlaygroundModel, V <: BasePlaygroundView] extends SAMState[M] {
  def model: M

  def view: V

  def adapter(m: M, b: BasePlaygroundModel): M

  def copy(model: M = model, view: V = view): BasePlaygroundState[M, V]

  override def render(newModel: M, observables: Map[M => Any, Observable[Any]]): (SAMState[M], Option[SAMAction[M]]) = renderImpl(newModel, observables = observables)

  private[this] def renderImpl(newModel: M, renderAll: Boolean = false, observables: Map[M => Any, Observable[Any]]): (BasePlaygroundState[M, V], Option[SAMAction[M]]) = {

    val fs: Seq[(Boolean, M => M)] = Seq(
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
      (newModel.mode.isEditMode && (renderAll || isUpdated(newModel, _.config.layout, _.mode.boxAvailable, _.config.pieceFace, _.mode.getBoardPieces, _.mode.getHandPieces)), renderBoxPieces),
      (renderAll || isUpdated(newModel, _.activeCursor), renderActiveCursor),
      ((!newModel.mode.isViewMode || newModel.selectedCursor.isEmpty) && (renderAll || isUpdated(newModel, _.selectedCursor)), renderSelectedCursor),
      (newModel.renderRequests.nonEmpty, processRenderRequests)
    )

    val nextModel = fs.foldLeft(newModel) { case (m, (cond, f)) => if (cond) f(m) else m }

    // Notify observers
    observables.foreach { case (f, obs) => if (renderAll || isUpdated(newModel, f)) obs.notifyObservers(f(newModel)) }

    // Just moved
    if (newModel.mode.isJustMoved(model.mode)) renderMove(newModel)
    if (newModel.mode.isPrevious(model.mode)) view.renderForward(false)
    if (newModel.mode.isNext(model.mode)) view.renderForward(true)

    (this.copy(model = nextModel), None)
  }

  override def initialize(observables: Map[M => Any, Observable[Any]]): Unit = {
    renderImpl(model, renderAll = true, observables)
  }

  //
  // Utilities
  //
  private[this] def isUpdated[A](newModel: M, fs: (M => A)*): Boolean = {
    fs.exists(f => f(model) != f(newModel))
  }

  //
  // Rendering functions
  //
  private[this] def renderLayout(newModel: M): M = {
    view.renderLayout(newModel.config.flipType.numAreas, newModel.config.layout)
    newModel
  }

  private[this] def renderSize(newModel: M): M = {
    view.renderSize(newModel.config.getPieceWidth)
    newModel
  }

  private[this] def renderIndex(newModel: M): M = {
    view.renderIndex(newModel.config.recordLang == Japanese)
    newModel
  }

  private[this] def renderFlip(newModel: M): M = {
    view.renderFlip(newModel.config.flipType)
    newModel
  }

  private[this] def renderPlayerNames(newModel: M): M = {
    view.renderPlayerNames(newModel.mode.getPlayerNames, newModel.config.messageLang)
    newModel
  }

  private[this] def renderIndicators(newModel: M): M = {
    view.renderIndicators(newModel.mode.getIndicators)
    newModel
  }

  private[this] def renderBox(newModel: M): M = {
    view.renderBox(newModel.mode.boxAvailable)
    newModel

  }

  private[this] def renderBoardPieces(newModel: M): M = {
    view.renderBoardPieces(newModel.mode.getBoardPieces, newModel.config.pieceFace)
    newModel
  }

  private[this] def renderHandPieces(newModel: M): M = {
    view.renderHandPieces(newModel.mode.getHandPieces, newModel.config.pieceFace)
    newModel
  }

  private[this] def renderLastMove(newModel: M): M = {
    view.renderLastMove(newModel.mode.getLastMove)
    newModel
  }

  private[this] def renderBoxPieces(newModel: M): M = {
    view.renderBoxPieces(newModel.mode.getBoxPieces, newModel.config.pieceFace)
    newModel
  }

  private[this] def renderActiveCursor(newModel: M): M = {
    view.renderActiveCursor(newModel.activeCursor)
    newModel
  }

  private[this] def renderSelectedCursor(newModel: M): M = {
    val legalMoves = for {
      (_, c) <- newModel.selectedCursor.toSet if newModel.config.visualEffectEnabled
      from = c.moveFrom
      lm <- newModel.mode.getLegalMoves(from)
    } yield lm
    view.renderSelectedCursor(newModel.selectedCursor.map(_._2), newModel.config.visualEffectEnabled, legalMoves)
    newModel
  }

  private[this] def processRenderRequests(newModel: M): M = {
    newModel.renderRequests.foreach {
      case PromotionDialogRequest(rawMove, rotate) =>
        view.askPromote(newModel.config.messageLang, newModel.config.pieceFace, newModel.config.layout.mediumPiece, rawMove, rotate)
      case CursorFlashRequest(cursor: Cursor) =>
        view.mainPane.updateSVGArea(_.flashCursor(cursor))
      case GameInfoDialogRequest =>
        newModel.mode.getGameControl.foreach { gc =>
          view.showGameInfoDialog(newModel.config.messageLang, gc.game.gameInfo)
        }
      case EditWarningDialogRequest =>
        view.showEditWarningDialog(newModel.config.messageLang)
    }
    adapter(newModel, newModel.copy(newRenderRequests = Seq.empty))
  }

  private[this] def renderMove(newModel: M): Unit = {
    if (newModel.mode.isViewMode) view.renderForward(true)
    newModel.mode.getLastMove.foreach(view.renderMoveEffect(_, newModel.config.pieceFace, newModel.config.visualEffectEnabled, newModel.config.soundEffectEnabled))
  }

}