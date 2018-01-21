package com.mogproject.mogami.frontend.view.nav

import com.mogproject.mogami.frontend._
import org.scalajs.dom.Element
import org.scalajs.dom.html.Anchor

import scalatags.JsDom.all._

/**
  *
  */
class PlaygroundLinkButton extends WebComponent with SAMObserver[BasePlaygroundModel] {
  private[this] lazy val linkButton: WebComponent = WebComponent(a(
    cls := "btn btn-default pg-link-btn",
    tpe := "button",
    target := "_blank",
    img(src := "assets/ico/mediumtile.png", height := 100.pct)
  )).withDynamicHoverTooltip(_.PLAYGROUND_LINK_TOOLTIP)

  override lazy val element: Element = linkButton.element

  //
  // Observer
  //
  override val samObserveMask: Long = {
    import ObserveFlag._
    GAME_BRANCH | GAME_POSITION | GAME_COMMENT | CONF_FLIP_TYPE | MENU_DIALOG
  }

  override def refresh(model: BasePlaygroundModel, flag: Long): Unit = {
    model.mode.getGameControl.foreach { gc =>
      val builder = ArgumentsBuilder(gc, model.config)
      linkButton.element.asInstanceOf[Anchor].href = builder.toRecordUrl
    }
  }
}
