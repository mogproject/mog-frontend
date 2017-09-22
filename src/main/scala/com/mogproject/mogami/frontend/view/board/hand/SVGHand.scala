package com.mogproject.mogami.frontend.view.board.hand

import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.Element

/**
  *
  */

case class SVGHandLayout(whiteOffset: Coord, blackOffset: Coord, pieceWidth: Int, numRows: Int, numColumns: Int) {

}

case class SVGHand(layout: SVGHandLayout) extends WebComponent {
  override def element: Element = ???
}
