package com.mogproject.mogami.frontend.model.board

import com.mogproject.mogami.Piece
import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.sam.SAMModel

/**
  *
  */
case class BoardModel(isViewMode: Boolean = false,
                      isEditMode: Boolean = false,
                      cursorEnabled: Boolean = true,
                      activeCursor: Option[Square] = None,
                      isFlipped: Boolean = false,
                      pieces: Map[Square, Piece] = Map.empty
                     ) extends SAMModel {

}
