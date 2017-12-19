package com.mogproject.mogami.frontend.action
import com.mogproject.mogami.frontend.model.BasePlaygroundModel

/**
  *
  */
object RefreshScreenAction extends PlaygroundAction {
  // todo: impl
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = Some(model)
}
