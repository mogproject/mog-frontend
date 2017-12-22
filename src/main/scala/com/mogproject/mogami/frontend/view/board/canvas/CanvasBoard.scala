package com.mogproject.mogami.frontend.view.board.canvas

import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, GameControl}
import com.mogproject.mogami.{Move, Player, State}
import com.mogproject.mogami.frontend.{Language, PieceFace}
import com.mogproject.mogami.frontend.view.board.SVGAreaLayout

/**
  * For PNG image creation
  */
case class CanvasBoard(config: BasePlaygroundConfiguration, gameControl: GameControl) {

//  pieceWidth: Int,
//  layout: SVGAreaLayout,
//  messageLang: Language, // for default player names
//  recordLang: Language, // for indexes
//  pieceFace: PieceFace,
//  isFlipped: Boolean,
//  playerNames: Map[Player, String],
//  state: State,
//  lastMove: Option[Move]

  def toBase64: String = {

    ""
  }
}
