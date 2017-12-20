package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.model.analyze.AnalyzeResult
import com.mogproject.mogami.frontend.model.board.cursor.Cursor
import com.mogproject.mogami.frontend.sam.SAMModel

/**
  *
  */
case class TestModel(override val mode: Mode,
                     override val config: BasePlaygroundConfiguration = BasePlaygroundConfiguration(),
                     override val activeCursor: Option[(Int, Cursor)] = None,
                     override val selectedCursor: Option[(Int, Cursor)] = None,
                     override val analyzeResult: Option[AnalyzeResult] = None,
                     override val renderRequests: Seq[RenderRequest] = Seq.empty
                    ) extends BasePlaygroundModel(mode, config, activeCursor, selectedCursor, analyzeResult, renderRequests) {

}

object TestModel {
  def adapter(m: TestModel, bm: BasePlaygroundModel): TestModel = {
    TestModel(bm.mode, bm.config, bm.activeCursor, bm.selectedCursor, bm.analyzeResult, bm.renderRequests)
  }
}