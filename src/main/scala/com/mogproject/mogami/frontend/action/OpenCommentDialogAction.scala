package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.frontend.model.{PlaygroundModel, CommentDialog, HandleDialogMessage}

/**
  *
  */
object OpenCommentDialogAction extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = Some(model.copy(messageBox = Some(HandleDialogMessage(CommentDialog))))
}
