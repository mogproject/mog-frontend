package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, CommentDialog, HandleDialogMessage}

/**
  *
  */
object OpenCommentDialogAction extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = Some(model.copy(newMessageBox = Some(HandleDialogMessage(CommentDialog))))
}
