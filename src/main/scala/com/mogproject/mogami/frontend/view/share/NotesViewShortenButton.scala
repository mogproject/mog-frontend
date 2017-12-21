package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.ShortenButtonLike


/**
  *
  */
class NotesViewShortenButton extends ShortenButtonLike {
  override protected val ident = "notes-view-short"

  override def onClick(): Unit = ??? // Controller.shortenNotesViewUrl()
}
