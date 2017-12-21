package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.model.{PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend.view.manage.SaveLoadButton
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class ManageMenu extends AccordionMenu {
  override lazy val ident: String = "Manage"
  override lazy val title: String = ident
  override lazy val icon: String = "file"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)

  lazy val saveLoadButton = new SaveLoadButton

  override lazy val content: JsDom.TypedTag[Div] = div(saveLoadButton.element)
}
