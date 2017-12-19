package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, BasePlaygroundModel}

/**
  *
  */
case class UpdateConfigurationAction(f: BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = Some(model.copy(newConfig = f(model.config)))
}
