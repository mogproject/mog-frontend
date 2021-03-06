package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.dialog.EmbedDialogAction
import com.mogproject.mogami.frontend.model.{PlaygroundModel, PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend.view.button.CommandButton
import com.mogproject.mogami.frontend.view.common.WarningLabel
import com.mogproject.mogami.frontend.view.share._
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._
import org.scalajs.dom

/**
  *
  */
class ShareMenu(isMobile: Boolean) extends AccordionMenu with PlaygroundSAMObserver {

  override lazy val ident: String = "Share"

  override def getTitle(messages: Messages): String = messages.SHARE

  override lazy val icon: String = "share"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)

  lazy val warningLabel = WarningLabel(_.SHARE_WARNING)
  lazy val recordCopyButton = new RecordCopyButton
  lazy val snapshotCopyButton = new SnapshotCopyButton
  lazy val imageLinkButton = new ImageLinkButton
  lazy val sfenStringCopyButton = new SfenStringCopyButton
  lazy val notesViewButton = new NotesViewButton
  lazy val embedButton: WebComponent = CommandButton(classButtonDefault, dismissModal, onclick := { () => doAction(EmbedDialogAction) }).withDynamicTextContent(_.EMBED_BUTTON)

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
    notesViewButton.element,
    br(),
    WebComponent.dynamicLabel(_.EMBED_LABEL).element,
    br(),
    embedButton.element
  )

  //
  // Observer
  //
  override val samObserveMask: Long = super.samObserveMask | {
    import ObserveFlag._
    GAME_BRANCH | GAME_INFO | GAME_POSITION | GAME_COMMENT | CONF_FLIP_TYPE | MENU_DIALOG
  }

  override def refresh(model: PlaygroundModel, flag: Long): Unit = {
    super.refresh(model, flag)
    setTimer(model, flag)
  }

  private[this] def refreshImpl(model: PlaygroundModel, flag: Long): Unit = {

    /** Do not update if the menu dialog is hidden. */
    if (!model.config.deviceType.isMobile || model.menuDialogOpen) {
      model.mode.getGameControl.foreach { gc =>
        val builder = PlaygroundArgumentsBuilder(gc, model.config)
        recordCopyButton.updateValue(builder.toRecordUrl)
        if (builder.commentOmitted) warningLabel.show() else warningLabel.hide()
        snapshotCopyButton.updateValue(builder.toSnapshotUrl)
        imageLinkButton.updateValue(builder.toImageLinkUrl)
        sfenStringCopyButton.updateValue(gc.getDisplayingState.toSfenString)
        notesViewButton.updateValue(builder.toNotesViewUrl)
      }
    }
  }

  //
  // Lazy refreshment
  //
  private[this] var currentTimer: Option[Int] = None

  private[this] def setTimer(model: PlaygroundModel, flag: Long): Unit = {
    currentTimer.foreach { t =>
      // cancel previous timer
      dom.window.clearTimeout(t)
    }
    currentTimer = Some(dom.window.setTimeout(() => refreshImpl(model, flag), 300))
  }

}
