package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.sam.SAMView
import org.scalajs.dom.Element

/**
  *
  */
case class TestView(rootElem: Element) extends SAMView {

  lazy val boardTest = new BoardTestView

  override def initialize(): Unit = {

    rootElem.appendChild(boardTest.element)
    boardTest.area.resize(400)

  }
}
