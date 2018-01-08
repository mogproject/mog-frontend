package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class AboutMenu extends AccordionMenu {
  override lazy val ident: String = "About"

  override def getTitle(messages: Messages): String = messages.ABOUT_THIS_SITE

  override lazy val icon: String = "info-sign"
  override lazy val content: JsDom.TypedTag[Div] = div(
    WebComponent.dynamicDivElements(_.ABOUT_CONTENT).element
  )
  override lazy val visibleMode = Set(PlayModeType, ViewModeType, EditModeType)
}
