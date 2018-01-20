package com.mogproject.mogami.frontend.action.dialog

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, EmbedDialog, HandleDialogMessage}


object EmbedDialogAction extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = Some(model.copy(newMessageBox = Some(HandleDialogMessage(EmbedDialog))))
}
