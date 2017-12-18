package com.mogproject.mogami.frontend.view.observer

import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, ModeType}

/**
  *
  */
trait ModeTypeObserver extends ModelObserver[ModeType] {
  override def extractor(model: BasePlaygroundModel): ModeType = model.mode.modeType
}
