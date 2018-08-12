package com.mogproject.mogami.frontend.action.dialog

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.PlaygroundModel

/**
  *
  */
case class MenuDialogAction(open: Boolean) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    model.config.deviceType.isMobile.option(model.copy(menuDialogOpen = open))
  }
}
