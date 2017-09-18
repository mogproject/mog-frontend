package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.sam.SAMView
import org.scalajs.dom.Element

/**
  *
  */
case class TestView(rootElem: Element) extends SAMView {

  lazy val boardTest = new BoardTestView

  override def initialize(): Unit = {

    boardTest.materialize(rootElem)
    boardTest.board.resize(400)

  }
}
