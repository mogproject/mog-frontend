package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.frontend.LocalStorage
import com.mogproject.mogami.frontend.model.board.DoubleBoard
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, BasePlaygroundModel}

/**
  *
  */
case class UpdateConfigurationAction(f: BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    val config = f(model.config)

    // save settings to Local Storage
    LocalStorage(
      config.pieceWidth,
      Some(config.layout),
      Some(config.pieceFace),
      Some(config.flipType == DoubleBoard),
      Some(config.visualEffectEnabled),
      Some(config.soundEffectEnabled),
      Some(config.messageLang),
      Some(config.recordLang)
    ).save()

    Some(model.copy(newConfig = config, newSelectedCursor = None))
  }
}
