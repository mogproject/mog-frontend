package com.mogproject.mogami.frontend.action.dialog

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{AskDeleteBranchRequest, BasePlaygroundModel}


object AskDeleteBranchAction  extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = Some(model.addRenderRequest(AskDeleteBranchRequest))
}
