package com.mogproject.mogami.frontend.view.observer

import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.frontend.view.Observer

/**
  *
  */
trait ModelObserver[A] extends Observer[A] {
  def extractor(model: BasePlaygroundModel): A

  private[this] def initialize(): Unit = {
    val f = (x: Any) => x match {
      case m: BasePlaygroundModel => extractor(m)
      case _ =>
    }
    SAM.addModelObserver(f, this.asInstanceOf[Observer[Any]])
  }

  initialize()
}
