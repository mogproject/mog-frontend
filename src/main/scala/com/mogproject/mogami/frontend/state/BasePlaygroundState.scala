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

    val fs: Seq[(Boolean, M => Unit)] = Seq(
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas), renderLayout),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas, _.config.pieceWidth), renderSize),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas, _.config.recordLang), renderIndex),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType), renderFlip),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas, _.mode.getPlayerNames, _.config.messageLang), renderPlayerNames),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas, _.mode.getIndicators), renderIndicators),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas, _.mode.boxAvailable), renderBox),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas, _.config.pieceFace, _.mode.getBoardPieces), renderBoardPieces),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas, _.config.pieceFace, _.mode.getHandPieces), renderHandPieces),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType, _.mode.getGameControl.map(_.getDisplayingLastMove)), renderLastMove),
      (newModel.mode.isEditMode && (renderAll || isUpdated(newModel, _.config.layout, _.mode.boxAvailable, _.config.pieceFace, _.mode.getBoardPieces, _.mode.getHandPieces)), renderBoxPieces),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType.numAreas, _.mode.getGameControl, _.config.recordLang), renderControlBars),
      (renderAll || isUpdated(newModel, _.mode.getGameControl), renderGameControl),
      (renderAll || isUpdated(newModel, _.mode.modeType, _.mode.getGameControl, _.config.recordLang, _.config.newBranchMode), renderBranchArea),
      (renderAll || isUpdated(newModel, _.config), renderConfigMenu),
      (renderAll || isUpdated(newModel, _.mode.modeType), renderModeType),
      (renderAll || isUpdated(newModel, _.activeCursor), renderActiveCursor),
      (newModel.analyzeResult.isDefined, renderAnalyzeResult),
      ((!newModel.mode.isViewMode || newModel.selectedCursor.isEmpty) && (renderAll || isUpdated(newModel, _.selectedCursor)), renderSelectedCursor),
      (newModel.renderRequests.nonEmpty, processRenderRequests)
    )

    fs.foreach { case (cond, f) => if (cond) f(newModel) }

    // Notify observers
    observables.foreach { case (f, obs) => if (renderAll || isUpdated(newModel, f)) obs.notifyObservers(f(newModel)) }

    // Just moved
    if (newModel.mode.isJustMoved(model.mode)) renderMove(newModel)
    if (newModel.mode.isPrevious(model.mode)) view.renderForward(false)
    if (newModel.mode.isNext(model.mode)) view.renderForward(true)

    (this.copy(model = adapter(newModel, newModel.copy(newAnalyzeResult = None, newRenderRequests = Seq.empty))), None)
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
  private[this] def renderLayout(newModel: M): Unit = {
    view.renderLayout(newModel.config.flipType.numAreas, newModel.config.pieceWidth, newModel.config.layout)
  }

  private[this] def renderSize(newModel: M): Unit = {
    view.renderSize(newModel.config.pieceWidth, newModel.config.layout)
  }

  private[this] def renderIndex(newModel: M): Unit = {
    view.renderIndex(newModel.config.recordLang == Japanese)
  }

  private[this] def renderFlip(newModel: M): Unit = {
    view.renderFlip(newModel.config.flipType)
  }

  private[this] def renderPlayerNames(newModel: M): Unit = {
    view.renderPlayerNames(newModel.mode.getPlayerNames, newModel.config.messageLang)
  }

  private[this] def renderIndicators(newModel: M): Unit = {
    view.renderIndicators(newModel.mode.getIndicators)
  }

  private[this] def renderBox(newModel: M): Unit = {
    view.renderBox(newModel.mode.boxAvailable)
  }

  private[this] def renderBoardPieces(newModel: M): Unit = {
    view.renderBoardPieces(newModel.mode.getBoardPieces, newModel.config.pieceFace)
  }

  private[this] def renderHandPieces(newModel: M): Unit = {
    view.renderHandPieces(newModel.mode.getHandPieces, newModel.config.pieceFace)
  }

  private[this] def renderLastMove(newModel: M): Unit = {
    view.renderLastMove(newModel.mode.getLastMove)
  }

  private[this] def renderBoxPieces(newModel: M): Unit = {
    view.renderBoxPieces(newModel.mode.getBoxPieces, newModel.config.pieceFace)
  }

  private[this] def renderControlBars(newModel: M): Unit = {
    view.renderControlBars(newModel.mode.getGameControl, newModel.config.recordLang)
  }

  private[this] def renderGameControl(newModel: M): Unit = {
    view.renderComment(newModel.mode.modeType, newModel.mode.getGameControl.flatMap(_.getComment).getOrElse(""))
    view.website.analyzeMenu.pointCountButton.clearMessage()
  }

  private[this] def renderBranchArea(newModel: M): Unit = {
    view.renderBranchArea(newModel.mode.getGameControl, newModel.config.recordLang, newModel.mode.modeType, newModel.config.newBranchMode)
  }

  private[this] def renderModeType(newModel: M): Unit = {
    view.updateModeType(newModel.mode.modeType)
  }

  private[this] def renderConfigMenu(newModel: M): Unit = {
    view.updateConfigMenu(newModel.config)
  }

  private[this] def renderActiveCursor(newModel: M): Unit = {
    view.renderActiveCursor(newModel.activeCursor)
  }

  private[this] def renderSelectedCursor(newModel: M): Unit = {
    val legalMoves = for {
      (_, c) <- newModel.selectedCursor.toSet if newModel.config.visualEffectEnabled && !c.isBox
      from = c.moveFrom
      lm <- newModel.mode.getLegalMoves(from)
    } yield lm
    view.renderSelectedCursor(newModel.selectedCursor.map(_._2), newModel.config.visualEffectEnabled, legalMoves)
  }

  private[this] def renderAnalyzeResult(newModel: M): Unit = {
    newModel.analyzeResult.foreach(r => view.renderAnalyzeResult(r, newModel.config.recordLang))
  }

  private[this] def processRenderRequests(newModel: M): Unit = {
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
      case EditAlertDialogRequest(msg) =>
        view.showEditAlertDialog(msg, newModel.config.messageLang)
      case CommentDialogRequest =>
        val comment = newModel.mode.getGameControl.flatMap(gc => gc.game.getComment(gc.gamePosition)).getOrElse("")
        view.showCommentDialog(newModel.config.messageLang, comment)
      case AskDeleteBranchRequest =>
        newModel.mode.getGameControl.foreach { gc =>
          view.askDeleteBranch(newModel.config.messageLang, gc.displayBranchNo)
        }
    }
  }

  private[this] def renderMove(newModel: M): Unit = {
    if (newModel.mode.isViewMode) view.renderForward(true)
    newModel.mode.getLastMove.foreach(view.renderMoveEffect(_, newModel.config.pieceFace, newModel.config.visualEffectEnabled, newModel.config.soundEffectEnabled))
  }

}