package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.ArgumentsBuilder
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend.view.share._
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class ShareMenu extends AccordionMenu {
  override lazy val ident: String = "Share"
  override lazy val title: String = ident
  override lazy val icon: String = "share"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)

  lazy val recordCopyButton = new RecordCopyButton
  lazy val recordShortenButton = new RecordShortenButton
  lazy val snapshotCopyButton = new SnapshotCopyButton
  lazy val snapshotShortenButton = new SnapshotShortenButton
  lazy val imageLinkButton = new ImageLinkButton
  lazy val sfenStringCopyButton = new SfenStringCopyButton
  lazy val notesViewButton = new NotesViewButton
  lazy val notesViewShortenButton = new NotesViewShortenButton

  override lazy val content: JsDom.TypedTag[Div] = div(
    recordCopyButton.element,
    recordShortenButton.element,
    br(),
    snapshotCopyButton.element,
    snapshotShortenButton.element,
    br(),
    imageLinkButton.element,
    br(),
    sfenStringCopyButton.element,
    br(),
    notesViewButton.element,
    notesViewShortenButton.element
  )

  def refresh(model: BasePlaygroundModel): Unit = {
    model.mode.getGameControl.foreach { gc =>
      val builder = ArgumentsBuilder(gc.game, gc.gamePosition, model.config)
      recordCopyButton.updateValue(builder.toRecordUrl)
      snapshotCopyButton.updateValue(builder.toSnapshotUrl)
      imageLinkButton.updateValue(builder.toImageLinkUrl)
      sfenStringCopyButton.updateValue(gc.game.toSfenString)
      notesViewButton.updateValue(builder.toNotesViewUrl)
    }
  }
}
