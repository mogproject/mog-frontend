package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.ShortenButtonLike

/**
  *
  */
class SnapshotShortenButton extends ShortenButtonLike {
  override protected val ident = "snapshot-short"

  override def onClick(): Unit = ??? // Controller.shortenSnapshotUrl()
}
