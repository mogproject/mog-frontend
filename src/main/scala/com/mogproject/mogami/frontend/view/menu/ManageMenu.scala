package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.model.{PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.manage.SaveLoadButton
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class ManageMenu(isMobile: Boolean) extends AccordionMenu {
  override lazy val ident: String = "Manage"

  override def getTitle(messages: Messages): String = messages.MANAGE

  override lazy val icon: String = "file"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)

  lazy val saveLoadButton = new SaveLoadButton(isMobile)

  override lazy val content: JsDom.TypedTag[Div] = div(saveLoadButton.element)
}
