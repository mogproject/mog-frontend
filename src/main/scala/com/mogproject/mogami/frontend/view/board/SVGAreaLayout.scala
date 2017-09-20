package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.Coord

/**
  *
  */
sealed trait SVGAreaLayout {
  def boardOffset: Coord
}

// fixme: offsets
case object SVGStandardLayout extends SVGAreaLayout {
  override val boardOffset = Coord(0, 300)
}

case object CompactLayout extends SVGAreaLayout {
  override val boardOffset = Coord(200, 0)
}

case object WideLayout extends SVGAreaLayout {
  override val boardOffset = Coord(400, 0)
}
