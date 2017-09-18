package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.view.board.effect._

/**
  * Visual effects
  */
trait SVGBoardEffector {
  self: SVGBoard =>

  object effect {

    lazy val cursorEffector = CursorEffector(self)
    lazy val selectedEffector = SelectedEffector(self)
    lazy val lastMoveEffector = LastMoveEffector(self)
    lazy val flashEffector = FlashEffector(self)
    lazy val moveEffector = MoveEffector(self)
    lazy val selectingEffector = SelectingEffector(self)
    lazy val legalMoveEffector = LegalMoveEffector(self)

  }

}
