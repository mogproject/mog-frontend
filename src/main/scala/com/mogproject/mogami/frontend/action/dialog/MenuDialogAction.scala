package com.mogproject.mogami.frontend.action.dialog

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, MenuDialogRequest}

/**
  *
  */
case class MenuDialogAction(open: Boolean) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    model.config.deviceType.isMobile.option(model.addRenderRequest(MenuDialogRequest(open)))
  }
}
