package com.mogproject.mogami.frontend

import com.mogproject.mogami.frontend.view.TestView
import org.scalajs.dom.Element

import scala.scalajs.js.annotation.JSExportTopLevel

/**
  * Entry point for testing
  */
@JSExportTopLevel("App")
object App extends PlaygroundAppLike {

  TestSettings

  override def createView(config: PlaygroundConfiguration, rootElem: Element): TestView = {
    TestView(config.deviceType.isMobile, config.freeMode, config.embeddedMode, config.isDev, config.isDebug, rootElem)
  }

}

