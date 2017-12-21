package com.mogproject.mogami.frontend.action
import com.mogproject.mogami.frontend.model.BasePlaygroundModel

/**
  *
  */
object RefreshScreenAction extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    UpdateConfigurationAction(_.updateScreenSize()).execute(model)
  }
}
