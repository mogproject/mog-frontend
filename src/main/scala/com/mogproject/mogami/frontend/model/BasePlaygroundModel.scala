package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.view.board.Cursor

/**
  *
  */
case class BasePlaygroundModel(mode: Mode,
                               config: BasePlaygroundConfiguration = BasePlaygroundConfiguration(),
                               activeCursor: Option[(Int, Cursor)] = None,
                               selectedCursor: Option[Cursor] = None
                              ) {

}
