package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.sam.{SAM, SAMAction, SAMModel}
import com.mogproject.mogami.frontend.view.{English, Language, WebComponent}
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
case class DropdownMenu[A, M <: SAMModel](items: Vector[A], labels: Map[A, Map[Language, String]], actionBuilder: A => SAMAction[M]) extends WebComponent {
  require(items.nonEmpty, "items must not be empty.")

  //
  // Variables
  //
  private[this] var currentLanguage: Language = English

  //
  // HTML Elements
  //
  private[this] lazy val labelButton = button(
    cls := "btn btn-default dropdown-toggle",
    tpe := "button",
    data("toggle") := "dropdown",
    aria.haspopup := true,
    aria.expanded := false
  ).render

  private[this] lazy val menuItems = ul(cls := "dropdown-menu").render

  private[this] lazy val dropdownElem = div(
    cls := "dropdown",
    labelButton,
    menuItems
  ).render

  override def element: Element = dropdownElem

  //
  // Operations
  //
  def renderItems(language: Language): Unit = {
    WebComponent.removeAllChildElements(menuItems)
    items.map { item =>
      val elem = li(a(onclick := { () => select(item); SAM.doAction(actionBuilder(item)) }, lookupLabel(item, language))).render
      menuItems.appendChild(elem)
    }
    currentLanguage = language
  }

  def select(item: A): Unit = {
    labelButton.innerHTML = lookupLabel(item, currentLanguage) + " " + span(cls := "caret")
  }

  //
  // Utilities
  //
  private[this] def lookupLabel(item: A, language: Language): String = labels.get(item).flatMap(_.get(language)).getOrElse("")

  //
  // Initialization
  //
  renderItems(currentLanguage)
  select(items.head)
}
