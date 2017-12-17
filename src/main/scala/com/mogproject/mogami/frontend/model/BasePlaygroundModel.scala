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
                          val renderRequests: Seq[RenderRequest] = Seq.empty
                         ) extends SAMModel {
  def copy(newMode: Mode = mode,
           newConfig: BasePlaygroundConfiguration = config,
           newActiveCursor: Option[(Int, Cursor)] = activeCursor,
           newSelectedCursor: Option[(Int, Cursor)] = selectedCursor,
           newRenderRequests: Seq[RenderRequest] = renderRequests): BasePlaygroundModel = {
    new BasePlaygroundModel(newMode, newConfig, newActiveCursor, newSelectedCursor, newRenderRequests)
  }

  def addRenderRequest(renderRequest: RenderRequest): BasePlaygroundModel = {
    copy(newRenderRequests = renderRequests :+ renderRequest)
  }

  def addRenderRequests(requests: Seq[RenderRequest]): BasePlaygroundModel = {
    copy(newRenderRequests = renderRequests ++ requests)
  }
}
