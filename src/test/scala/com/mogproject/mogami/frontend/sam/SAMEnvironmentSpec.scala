package com.mogproject.mogami.frontend.sam

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class SAMEnvironmentSpec extends AnyFlatSpec with Matchers {

  private case class DummyModel(value: Int) extends SAMModel

  private object DummyView extends SAMView {
    override def initialize(): Unit = {}
  }

  private case class DummyState(model: DummyModel, env: SAMEnvironment[DummyModel]) extends SAMState[DummyModel] {
    override def view: SAMView = DummyView

    override def getObserveFlag(newModel: DummyModel): Long = -1L

    override def render(newModel: DummyModel): (SAMState[DummyModel], Option[SAMAction[DummyModel]]) = {
      (copy(model = newModel), None)
    }
  }

  "SAMEnvironment#initialize" must "not throw when observers are removed during notification" in {
    val env = new SAMEnvironment[DummyModel]()

    val removingObserver = new SAMObserver[DummyModel] {
      override def samObserveMask(): Long = -1L

      override def refresh(model: DummyModel, flag: Long): Unit = {
        env.removeObserver(this)
      }
    }

    val passiveObserver = new SAMObserver[DummyModel] {
      override def samObserveMask(): Long = -1L

      override def refresh(model: DummyModel, flag: Long): Unit = {}
    }

    env.addObserver(removingObserver)
    env.addObserver(passiveObserver)

    noException must be thrownBy env.initialize(DummyState(DummyModel(0), env))
  }
}
