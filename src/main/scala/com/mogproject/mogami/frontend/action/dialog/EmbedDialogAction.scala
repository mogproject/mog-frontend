package com.mogproject.mogami.frontend.action.dialog

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{PlaygroundModel, EmbedDialog, HandleDialogMessage}


object EmbedDialogAction extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = Some(model.copy(messageBox = Some(HandleDialogMessage(EmbedDialog))))
}
