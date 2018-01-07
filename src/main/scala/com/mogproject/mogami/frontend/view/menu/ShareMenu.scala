package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend.view.share._
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class ShareMenu(isMobile: Boolean) extends AccordionMenu with SAMObserver[BasePlaygroundModel] {

  override lazy val ident: String = "Share"

  override def getTitle(messages: Messages): String = messages.SHARE

  override lazy val icon: String = "share"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)

  lazy val warningLabel = new WarningLabel
  lazy val recordCopyButton = new RecordCopyButton
  lazy val snapshotCopyButton = new SnapshotCopyButton
  lazy val imageLinkButton = new ImageLinkButton
  lazy val sfenStringCopyButton = new SfenStringCopyButton
  lazy val notesViewButton = new NotesViewButton

  override lazy val content: JsDom.TypedTag[Div] = div(
    warningLabel.element,
    WebComponent.dynamicLabel(_.RECORD_URL).element,
    recordCopyButton.element,
    br(),
    WebComponent.dynamicLabel(_.SNAPSHOT_URL).element,
    snapshotCopyButton.element,
    br(),
    WebComponent.dynamicLabel(_.SNAPSHOT_IMAGE).element,
    imageLinkButton.element,
    br(),
    WebComponent.dynamicLabel(_.SNAPSHOT_SFEN_STRING).element,
    sfenStringCopyButton.element,
    br(),
    WebComponent.dynamicLabel(_.NOTES_VIEW).element,
    notesViewButton.element
  )

  //
  // Observer
  //
  override val samObserveMask: Int = super.samObserveMask | {
    import ObserveFlag._
    GAME_BRANCH | GAME_POSITION | GAME_COMMENT | CONF_FLIP_TYPE | MENU_DIALOG
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {

    /** Do not update if the menu dialog is hidden. */
    if (!model.config.deviceType.isMobile || model.menuDialogOpen) {
      super.refresh(model, flag)

      model.mode.getGameControl.foreach { gc =>
        val builder = ArgumentsBuilder(gc, model.config)
        recordCopyButton.updateValue(builder.toRecordUrl)
        if (builder.commentOmitted) warningLabel.show() else warningLabel.hide()
        snapshotCopyButton.updateValue(builder.toSnapshotUrl)
        imageLinkButton.updateValue(builder.toImageLinkUrl)
        sfenStringCopyButton.updateValue(gc.getDisplayingState.toSfenString)
        notesViewButton.updateValue(builder.toNotesViewUrl)
      }
    }
  }

}
