package com.mogproject.mogami.frontend

import com.mogproject.mogami.frontend.model.TestModel
import com.mogproject.mogami.frontend.state.TestState
import com.mogproject.mogami.frontend.view.TestView
import org.scalajs.dom.Element

/**
  * Entry point for testing
  */
object App extends PlaygroundAppLike[TestModel, TestView, TestState] {

  TestSettings

  override def createModel(mode: Mode, config: BasePlaygroundConfiguration): TestModel = TestModel(mode, config)

  override def createView(config: BasePlaygroundConfiguration, rootElem: Element): TestView = {
    TestView(config.deviceType.isMobile, config.freeMode, config.embeddedMode, config.isDev, config.isDebug, rootElem)
  }

  override def createState(model: TestModel, view: TestView): TestState = TestState(model, view)

  override def samAdapter: (TestModel, BasePlaygroundModel) => TestModel = TestModel.adapter

}

