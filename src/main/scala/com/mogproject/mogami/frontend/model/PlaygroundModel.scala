package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.model.board.cursor.Cursor
import com.mogproject.mogami.frontend.sam.SAMModel

/**
  *
  */
case class PlaygroundModel(mode: Mode,
                           config: PlaygroundConfiguration = PlaygroundConfiguration(),
                           activeCursor: Option[(Int, Cursor)] = None,
                           selectedCursor: Option[(Int, Cursor)] = None,
                           flashedCursor: Option[Cursor] = None,
                           menuDialogOpen: Boolean = false,
                           messageBox: Option[Message] = None
                          ) extends SAMModel {
}
