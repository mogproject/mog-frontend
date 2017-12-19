package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, CommentDialogRequest}

/**
  *
  */
object OpenCommentDialogAction  extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = Some(model.addRenderRequest(CommentDialogRequest))
}
