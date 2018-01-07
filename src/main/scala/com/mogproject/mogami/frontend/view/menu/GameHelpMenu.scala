package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class GameHelpMenu extends AccordionMenu {
  override lazy val ident: String = "Help"

  override def getTitle(messages: Messages): String = messages.HELP

  override lazy val icon: String = "question-sign"

  override lazy val content: JsDom.TypedTag[Div] = div(WebComponent(ul()).withDynamicInnerElements(_.HELP_CONTENT).element)
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)
}
