package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipDisabled, FlipEnabled, FlipType}
import com.mogproject.mogami.frontend.view.{English, Japanese, Language}
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGStandardLayout}

/**
  * Base configuration for Playground framework
  */
case class BasePlaygroundConfiguration(layout: SVGAreaLayout = SVGStandardLayout,
                                       pieceWidth: Option[Int] = None,
                                       flipType: FlipType = FlipDisabled,
                                       pieceFace: PieceFace = JapaneseOneCharFace,
                                       newBranchMode: Boolean = false,
                                       messageLang: Language = English,
                                       recordLang: Language = Japanese,
                                       visualEffectEnabled: Boolean = true,
                                       soundEffectEnabled: Boolean = false
                                      ) {

  def isAreaFlipped(areaId: Int): Boolean = flipType match {
    case FlipDisabled => false
    case FlipEnabled => true
    case DoubleBoard => areaId == 1
  }
}
