package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.sam.SAMView
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGStandardLayout}
import org.scalajs.dom.Element

/**
  *
  */
case class TestView(rootElem: Element) extends SAMView {

  val boardTest: BoardTestView = new BoardTestView

  override def initialize(): Unit = {
    rootElem.appendChild(boardTest.element)
  }

  def setAreaLayout(layout: SVGAreaLayout): Unit = {
    boardTest.drawBoardArea(layout)
  }

}
