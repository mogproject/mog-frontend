package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.i18n.DynamicLabel
import com.mogproject.mogami.frontend.view.reset.EditResetButton
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class ResetMenu extends AccordionMenu {

  override lazy val ident: String = "Reset"
  override lazy val titleLabel = DynamicLabel(_.RESET)
  override lazy val icon: String = "erase"
  override lazy val visibleMode = Set(EditModeType)

  private[this] lazy val editResetButton = new EditResetButton

  override lazy val content: JsDom.TypedTag[Div] = div(
    editResetButton.element
  )
}

