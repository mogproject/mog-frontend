package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.model.board.cursor.Cursor

/**
  *
  */
case class TestModel(override val mode: Mode,
                     override val config: BasePlaygroundConfiguration = BasePlaygroundConfiguration(),
                     override val activeCursor: Option[(Int, Cursor)] = None,
                     override val selectedCursor: Option[(Int, Cursor)] = None,
                     override val flashedCursor: Option[Cursor] = None,
                     override val messageBox: Option[Message] = None
                    ) extends BasePlaygroundModel(mode, config, activeCursor, selectedCursor, flashedCursor, messageBox) {

}

// todo: TestModel(baseModel: BasePlaygroundModel)
object TestModel {
  def adapter(m: TestModel, bm: BasePlaygroundModel): TestModel = {
    TestModel(bm.mode, bm.config, bm.activeCursor, bm.selectedCursor, bm.flashedCursor, bm.messageBox)
  }
}