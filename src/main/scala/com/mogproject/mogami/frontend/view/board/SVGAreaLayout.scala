package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.view.board.board.SVGBoardLayout

/**
  *
  */
sealed trait SVGAreaLayout {
  def board: SVGBoardLayout

  def viewBoxBottomRight: Coord
}

// fixme: offsets
case object SVGStandardLayout extends SVGAreaLayout {
  override val board: SVGBoardLayout = SVGBoardLayout(Coord(0, 300))

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH, board.VIEW_BOX_HEIGHT)
}

case object CompactLayout extends SVGAreaLayout {
  override val board: SVGBoardLayout = SVGBoardLayout(Coord(200, 0))

  override def viewBoxBottomRight: Coord = Coord(2048, 2048)
}

case object WideLayout extends SVGAreaLayout {
  override val board: SVGBoardLayout = SVGBoardLayout(Coord(400, 0))

  override def viewBoxBottomRight: Coord = Coord(2048, 2048)
}
