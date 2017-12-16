package com.mogproject.mogami.frontend.view

import org.scalajs.dom.Element

/**
  *
  */
case class TestView(rootElem: Element) extends BasePlaygroundView {

  val mainPane = TestMainPane()

}
