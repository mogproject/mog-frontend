package com.mogproject.mogami.frontend.model.board

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.sam.SAMModel

/**
  *
  */
case class BoardModel(activeCursor: Option[Square]) extends SAMModel {

}
