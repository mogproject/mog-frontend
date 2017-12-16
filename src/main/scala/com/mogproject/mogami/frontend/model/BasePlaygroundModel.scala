package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.sam.SAMModel
import com.mogproject.mogami.frontend.view.board.Cursor

/**
  *
  */
class BasePlaygroundModel(val mode: Mode,
                          val config: BasePlaygroundConfiguration = BasePlaygroundConfiguration(),
                          val activeCursor: Option[(Int, Cursor)] = None,
                          val selectedCursor: Option[Cursor] = None
                         ) extends SAMModel {

}
