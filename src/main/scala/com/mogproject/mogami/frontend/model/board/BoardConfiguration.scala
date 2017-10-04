package com.mogproject.mogami.frontend.model.board

import com.mogproject.mogami.frontend.view.{English, Language}
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGStandardLayout}

/**
  * Board configuration
  */
case class BoardConfiguration(layout: SVGAreaLayout = SVGStandardLayout,
                              boardWidth: Int = 400,
                              flipType: FlipType = FlipDisabled,
                              pieceFace: String = "jp1",
                              recordLang: Language = English
                             ) {

}
