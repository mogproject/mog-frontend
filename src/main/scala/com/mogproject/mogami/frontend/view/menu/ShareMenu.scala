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
class ShareMenu extends AccordionMenu with SAMObserver[BasePlaygroundModel] {
  override lazy val ident: String = "Share"
  override lazy val title: String = ident
  override lazy val icon: String = "share"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)

  lazy val recordCopyButton = new RecordCopyButton
  lazy val snapshotCopyButton = new SnapshotCopyButton
  lazy val imageLinkButton = new ImageLinkButton
  lazy val sfenStringCopyButton = new SfenStringCopyButton
  lazy val notesViewButton = new NotesViewButton

  override lazy val content: JsDom.TypedTag[Div] = div(
    recordCopyButton.element,
    br(),
    snapshotCopyButton.element,
    br(),
    imageLinkButton.element,
    br(),
    sfenStringCopyButton.element,
    br(),
    notesViewButton.element
  )

  //
  // Observer
  //
  override val samObserveMask: Int = super.samObserveMask | {
    import ObserveFlag._
    GAME_BRANCH | GAME_POSITION | CONF_FLIP_TYPE | MENU_DIALOG
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {

    /** Do not update if the menu dialog is hidden. */
    if (!model.config.deviceType.isMobile || model.menuDialogOpen) {
      super.refresh(model, flag)

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

}
