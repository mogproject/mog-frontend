package com.mogproject.mogami.frontend.action.dialog

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, MenuDialogRequest}

/**
  *
  */
case class MenuDialogAction(open: Boolean) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    Some(model.addRenderRequest(MenuDialogRequest(open)))
  }
}
