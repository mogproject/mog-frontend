package com.mogproject.mogami.frontend.state

import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.view.TestView

/**
  *
  */
case class TestState(model: TestModel, view: TestView) extends BasePlaygroundState[TestModel, TestView] {
  override def adapter(m: TestModel, b: BasePlaygroundModel): TestModel = TestModel.adapter(m, b)

  override def copy(model: TestModel = model, view: TestView = view): BasePlaygroundState[TestModel, TestView] = TestState(model, view)
}