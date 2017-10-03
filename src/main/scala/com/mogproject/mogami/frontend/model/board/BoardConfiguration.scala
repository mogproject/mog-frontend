package com.mogproject.mogami.frontend.model.board

import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGStandardLayout}

/**
  * Board configuration
  */
case class BoardConfiguration(layout: SVGAreaLayout = SVGStandardLayout, boardWidth: Int = 400) {

}
