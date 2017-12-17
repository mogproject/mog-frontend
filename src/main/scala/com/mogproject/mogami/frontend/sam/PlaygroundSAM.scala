package com.mogproject.mogami.frontend.sam

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel

/**
  *
  */
trait PlaygroundSAMLike {
  def doAction(action: PlaygroundAction): Unit = {}
}

class PlaygroundSAM[M <: BasePlaygroundModel](adapter: (M, BasePlaygroundModel) => M) extends PlaygroundSAMLike {

  override def doAction(action: PlaygroundAction): Unit = {
    SAM.doAction(new SAMAction[M] {
      override def execute(model: M): Option[M] = action.execute(model).map(adapter(model, _))
    })
  }
}

object PlaygroundSAM {
  private[this] var samImpl: PlaygroundSAMLike = new PlaygroundSAMLike {}

  def initialize[M <: BasePlaygroundModel](adapter: (M, BasePlaygroundModel) => M): Unit = {
    samImpl = new PlaygroundSAM(adapter)
  }

  /**
    * Do action with an adapter
    *
    * @param action action
    */
  def doAction[M <: SAMModel](action: SAMAction[M]): Unit = action match {
    case a: PlaygroundAction => samImpl.doAction(a)
    case a => SAM.doAction(a)
  }

}
