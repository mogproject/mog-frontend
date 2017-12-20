package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.sam.{PlaygroundSAM, SAMAction, SAMModel}
import com.mogproject.mogami.frontend.view.{English, Language, WebComponent}
import org.scalajs.dom.Element
import org.scalajs.dom.html.LI

import scalatags.JsDom.all._

/**
  *
  */
case class DropdownMenu[A, M <: BasePlaygroundModel](items: Vector[A],
                                                     labels: Map[A, Map[Language, String]],
                                                     actionBuilder: A => SAMAction[M],
                                                     menuClass: String = "",
                                                     separatorIndexes: Seq[Int] = Seq.empty
                                                    ) extends WebComponent {
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
    data("toggle") := "dropdown"
  ).render

  private[this] lazy val menuItems = ul(cls := "dropdown-menu " + menuClass).render

  private[this] lazy val dropdownElem = div(
    cls := "dropdown",
    labelButton,
    menuItems
  ).render

  override lazy val element: Element = {
    renderItems(currentLanguage)
    select(items.head)
    dropdownElem
  }

  //
  // Operations
  //
  def renderItems(language: Language): Unit = {
    WebComponent.removeAllChildElements(menuItems)
    items.zipWithIndex.map { case (item, ind) =>
      if (separatorIndexes.contains(ind)) menuItems.appendChild(separator)
      val elem = li(a(href := "#", onclick := { () => select(item); PlaygroundSAM.doAction(actionBuilder(item)) }, lookupLabel(item, language))).render
      menuItems.appendChild(elem)
    }
    currentLanguage = language
  }

  def separator: LI = li(role := "separator", cls := "divider").render

  def select(item: A): Unit = {
    labelButton.innerHTML = lookupLabel(item, currentLanguage) + " " + span(cls := "caret")
  }

  //
  // Utilities
  //
  private[this] def lookupLabel(item: A, language: Language): String = labels.get(item).flatMap(_.get(language)).getOrElse("")

}
