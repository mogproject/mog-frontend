package com.mogproject.mogami.frontend.view.menu.setting

import com.mogproject.mogami.frontend.action.{PlaygroundAction, UpdateConfigurationAction}
import com.mogproject.mogami.frontend.view.{English, Language, WebComponent}
import com.mogproject.mogami.frontend.view.button.DropdownMenu
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
class BoardSizeButton extends WebComponent {

  /**
    * definitions of board sizes
    */
  private[this] val sizeSettings = Seq(
    None -> "Automatic",
    Some(15) -> "15 - Extra Small",
    Some(30) -> "30 - Small",
    Some(40) -> "40 - Medium",
    Some(50) -> "50 - Large",
    Some(60) -> "60 - Extra Large"
  )

  private[this] def actionBuilder(pieceWidth: Option[Int]): PlaygroundAction = UpdateConfigurationAction(_.copy(pieceWidth = pieceWidth))

  private[this] lazy val dropdownButton = DropdownMenu(
    sizeSettings.map(_._1).toVector,
    sizeSettings.map { case (k, v) => k -> Map[Language, String](English -> v) }.toMap,
    actionBuilder,
    menuClass = "left",
    separatorIndexes = Seq(1)
  )

  override lazy val element: Div = div(
    cls := "btn-group pull-right",
    role := "group",
    marginTop := (-8).px,
    dropdownButton.element
  ).render

  def updateValue(pieceWidth: Option[Int]): Unit = {
    if (sizeSettings.map(_._1).toSet.contains(pieceWidth)) {
      dropdownButton.select(pieceWidth)
    } else {
      dropdownButton.select(None)
    }

  }
}
