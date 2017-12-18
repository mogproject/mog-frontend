package com.mogproject.mogami.frontend.view.observer

import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, ModeType}
import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.frontend.state.BasePlaygroundState
import com.mogproject.mogami.frontend.view.{BasePlaygroundView, Observer}

/**
  *
  */
trait ModeTypeObserver extends Observer[ModeType] {
  private[this] def initialize(): Unit = {
    SAM.addModelObserver((m: BasePlaygroundModel) => m.mode.modeType, this.asInstanceOf[Observer[Any]])
  }

  initialize()
}
