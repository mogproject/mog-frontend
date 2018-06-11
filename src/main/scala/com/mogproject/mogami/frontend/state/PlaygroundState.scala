package com.mogproject.mogami.frontend.state

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.BasePlaygroundView
import com.mogproject.mogami.frontend.view.i18n.Messages

/**
  *
  */
case class PlaygroundState(model: PlaygroundModel, view: BasePlaygroundView) extends SAMState[PlaygroundModel] {

  override def getObserveFlag(newModel: PlaygroundModel): Long = ObserveFlag.getObserveFlag(model, newModel)

  override def render(newModel: PlaygroundModel): (SAMState[PlaygroundModel], Option[SAMAction[PlaygroundModel]]) = renderImpl(newModel)

  private[this] def renderImpl(newModel: PlaygroundModel): (PlaygroundState, Option[SAMAction[PlaygroundModel]]) = {
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
      case CopyAllMovesMessage(text) =>
        newModel.mode.getGameControl.foreach { gc => view.website.mainPane.copyAllMoves(text) }
      case HandleDialogMessage(dialog, open) => dialog match {
        case PromotionDialog(rawMove, rotate) =>
          view.askPromote(newModel.config.pieceFace, rawMove, rotate)
        case GameInfoDialog =>
          newModel.mode.getGameControl.foreach { gc => view.showGameInfoDialog(gc.game.gameInfo, newModel.mode.isHandicapped) }
        case EditWarningDialog =>
          view.showEditWarningDialog()
        case EditAlertDialog(message) =>
          view.showEditAlertDialog(message)
        case CommentDialog =>
          val comment = newModel.mode.getGameControl.flatMap(gc => gc.game.getComment(gc.gamePosition)).getOrElse("")
          view.showCommentDialog(comment)
        case AskDeleteBranchDialog =>
          newModel.mode.getGameControl.foreach { gc => view.askDeleteBranch(gc.displayBranchNo) }
        case EmbedDialog =>
          newModel.mode.getGameControl.foreach { gc => view.showEmbedDialog(gc, newModel.config) }
      }
    }

    (this.copy(model = newModel.copy(flashedCursor = None, messageBox = None)), None)
  }

}