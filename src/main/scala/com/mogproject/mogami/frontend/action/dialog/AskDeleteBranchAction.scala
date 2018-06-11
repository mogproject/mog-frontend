package com.mogproject.mogami.frontend.action.dialog

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{AskDeleteBranchDialog, PlaygroundModel, HandleDialogMessage}


object AskDeleteBranchAction  extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = Some(model.copy(messageBox = Some(HandleDialogMessage(AskDeleteBranchDialog))))
}
