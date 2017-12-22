package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend._
import org.scalajs.dom.Element
import org.scalajs.dom.html.LI

import scalatags.JsDom.all._

/**
  *
  */
case class DropdownMenu[A](items: Vector[A],
                           labels: Map[A, Map[Language, String]],
                           clickAction: A => Unit = { (_: A) => },
                           dropdownClass: String = "dropdown",
                           labelClass: String = "",
                           menuClass: String = "",
                           dropdownHeader: Option[String] = None,
                           separatorIndexes: Seq[Int] = Seq.empty
                          ) extends WebComponent {
  require(items.nonEmpty, "items must not be empty.")

  //
  // Variables
  //
  private[this] var currentLanguage: Language = English

  private[this] var value: A = items.head

  //
  // HTML Elements
  //
  private[this] val labelButton = button(
    cls := "btn btn-default dropdown-toggle " + labelClass,
    tpe := "button",
    data("toggle") := "dropdown"
  ).render

  private[this] val menuItems = ul(
    cls := "dropdown-menu " + menuClass
  ).render

  private[this] val dropdownElem = div(
    cls := dropdownClass,
    labelButton,
    menuItems
  ).render

  override val element: Element = {
    renderItems(currentLanguage)
    select(items.head)
    dropdownElem
  }

  //
  // Operations
  //
  def renderItems(language: Language): Unit = {
    // reset
    WebComponent.removeAllChildElements(menuItems)

    // header
    dropdownHeader.foreach(hd => menuItems.appendChild(h6(cls := "dropdown-header", hd).render))

    // items and separators
    items.zipWithIndex.map { case (item, ind) =>
      if (separatorIndexes.contains(ind)) menuItems.appendChild(separator)
      menuItems.appendChild(li(a(onclick := { () => select(item); clickAction(item) }, lookupLabel(item, language).get)).render)
    }

    currentLanguage = language
  }

  def separator: LI = li(role := "separator", cls := "divider").render

  def select(item: A): Unit = {
    value = item
    lookupLabel(item, currentLanguage) match {
      case Some(s) => labelButton.innerHTML = s + " " + span(cls := "caret")
      case None => // do nothing
    }
  }

  def getValue: A = value

  //
  // Utilities
  //
  private[this] def lookupLabel(item: A, language: Language): Option[String] = labels.get(item).flatMap(_.get(language))

}

object DropdownMenu {
  def buildLabels[A](items: Vector[A]): Map[A, Map[Language, String]] = {
    items.map { k => k -> Map[Language, String](English -> k.toString) }.toMap
  }

  def buildClickAction[A](actionBuilder: A => PlaygroundAction)(item: A): Unit = {
    PlaygroundSAM.doAction(actionBuilder(item))
  }
}