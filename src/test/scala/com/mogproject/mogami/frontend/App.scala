package com.mogproject.mogami.frontend

import com.mogproject.mogami._
import com.mogproject.mogami.core.state.StateCache.Implicits._
import com.mogproject.mogami.frontend.model.{GameControl, PlayMode, TestModel}
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.state.TestState
import com.mogproject.mogami.frontend.view.TestView
import org.scalajs.dom

import scala.scalajs.js.JSApp

/**
  * Entry point for testing
  */
object App extends JSApp {
  override def main(): Unit = {
    PlaygroundSAM.initialize(TestModel.adapter)
    SAM.initialize(TestState(TestModel(PlayMode(GameControl(Game()))), TestView(dom.document.getElementById("app"))))
  }
}

