package com.mogproject.mogami.frontend.state

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.BasePlaygroundView
import com.mogproject.mogami.frontend.view.i18n.Messages

/**
  *
  */
trait BasePlaygroundState[M <: BasePlaygroundModel, V <: BasePlaygroundView] extends SAMState[M] {
  def model: M

  def view: V

  def adapter(m: M, b: BasePlaygroundModel): M

  def copy(model: M = model, view: V = view): BasePlaygroundState[M, V]

  override def getObserveFlag(newModel: M): BranchNo = ObserveFlag.getObserveFlag(model, newModel)

  override def render(newModel: M): (SAMState[M], Option[SAMAction[M]]) = renderImpl(newModel, renderAll = false)

  private[this] def renderImpl(newModel: M, renderAll: Boolean): (BasePlaygroundState[M, V], Option[SAMAction[M]]) = {
    // update message lang
    Messages.setLanguage(newModel.config.messageLang)

    // process flashed cursor
    newModel.flashedCursor.foreach(cursor => view.mainPane.flashCursor(cursor))

    // process message box
    newModel.messageBox.foreach {
      case AnalyzeResultMessage(result) =>
        view.renderAnalyzeResult(result, newModel.config.recordLang)
      case CopyResultMessage(format) =>
        newModel.mode.getGameControl.foreach { gc => view.website.manageMenu.saveLoadButton.renderRecord(gc.getRecord(format)) }
      case HandleDialogMessage(dialog, open) => dialog match {
        case PromotionDialog(rawMove, rotate) =>
          view.askPromote(newModel.config.messageLang, newModel.config.pieceFace, rawMove, rotate)
        case GameInfoDialog =>
          newModel.mode.getGameControl.foreach { gc => view.showGameInfoDialog(newModel.config.messageLang, gc.game.gameInfo, gc.isHandicapped) }
        case EditWarningDialog =>
          view.showEditWarningDialog(newModel.config.messageLang)
        case EditAlertDialog(message) =>
          view.showEditAlertDialog(message, newModel.config.messageLang)
        case CommentDialog =>
          val comment = newModel.mode.getGameControl.flatMap(gc => gc.game.getComment(gc.gamePosition)).getOrElse("")
          view.showCommentDialog(newModel.config.messageLang, comment)
        case AskDeleteBranchDialog =>
          newModel.mode.getGameControl.foreach { gc => view.askDeleteBranch(newModel.config.messageLang, gc.displayBranchNo) }
      }
    }

    (this.copy(model = adapter(newModel, newModel.copy(newFlashedCursor = None, newMessageBox = None))), None)
  }

  override def initialize(): Unit = {
    renderImpl(model, renderAll = true)
  }
}