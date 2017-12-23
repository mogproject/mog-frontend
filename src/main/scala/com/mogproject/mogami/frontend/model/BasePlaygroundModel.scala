package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.model.board.cursor.Cursor
import com.mogproject.mogami.frontend.sam.SAMModel

/**
  *
  */
class BasePlaygroundModel(val mode: Mode,
                          val config: BasePlaygroundConfiguration = BasePlaygroundConfiguration(),
                          val activeCursor: Option[(Int, Cursor)] = None,
                          val selectedCursor: Option[(Int, Cursor)] = None,
                          val flashedCursor: Option[Cursor] = None,
                          val messageBox: Option[Message]
                         ) extends SAMModel {
  def copy(newMode: Mode = mode,
           newConfig: BasePlaygroundConfiguration = config,
           newActiveCursor: Option[(Int, Cursor)] = activeCursor,
           newSelectedCursor: Option[(Int, Cursor)] = selectedCursor,
           newFlashedCursor: Option[Cursor] = flashedCursor,
           newMessageBox: Option[Message] = messageBox): BasePlaygroundModel = {
    new BasePlaygroundModel(newMode, newConfig, newActiveCursor, newSelectedCursor, newFlashedCursor, newMessageBox)
  }
}
