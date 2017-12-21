package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.ShortenButtonLike

/**
  *
  */
class RecordShortenButton extends ShortenButtonLike {
  override protected val ident = "record-short"

  override def onClick(): Unit = ??? //Controller.shortenRecordUrl()
}
