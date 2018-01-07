package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.i18n.{DynamicComponentLike, Messages}
import org.scalajs.dom.Element
import org.scalajs.dom.html.LI

import scalatags.JsDom.Frag
import scalatags.JsDom.all._

/**
  *
  */
case class DropdownMenu[A](items: Vector[A],
                           labelFunc: Messages => Map[A, String],
                           clickAction: A => Unit = { (_: A) => },
                           dropdownClass: String = "dropdown",
                           labelClass: String = "",
                           menuClass: String = "",
                           dropdownHeader: Option[Messages => String] = None,
                           separatorIndexes: Seq[Int] = Seq.empty
                          ) extends WebComponent with DynamicComponentLike {
  require(items.nonEmpty, "items must not be empty.")

  override def getDynamicElements(messages: Messages): Seq[Frag] = {
    // header
    val hdr = dropdownHeader.map(hd => h6(cls := "dropdown-header", hd(messages)))

    // items and separators
    val its = items.zipWithIndex.flatMap { case (item, ind) =>
      val sep = separatorIndexes.contains(ind).option(li(role := "separator", cls := "divider"))
      val it = li(a(onclick := { () => select(item); clickAction(item) }, StringFrag(labelFunc(messages).getOrElse(item, ""))))
      sep.toSeq       :+ it
    }

    Seq(createLabelButton(messages), ul(cls := "dropdown-menu " + menuClass, hdr.toSeq ++ its))
  }

  private[this] def createLabelButton(messages: Messages) = button(
    cls := "btn btn-default dropdown-toggle " + labelClass,
    tpe := "button",
    data("toggle") := "dropdown",
    StringFrag(labelFunc(Messages.get).getOrElse(value, "")),
    " ",
    span(cls := "caret")
  )

  //
  // Variables
  //
  private[this] var value: A = items.head

  override lazy val element: Element = div(cls := dropdownClass).render

  //
  // Operations
  //
  def select(item: A): Unit = {
    value = item
    refresh()
  }

  def getValue: A = value
}



case class DropdownMenuOld[A](items: Vector[A],
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

object DropdownMenuOld {
  def buildLabels[A](items: Vector[A]): Map[A, Map[Language, String]] = {
    items.map { k => k -> Map[Language, String](English -> k.toString) }.toMap
  }

  def buildClickAction[A](actionBuilder: A => PlaygroundAction)(item: A): Unit = {
    PlaygroundSAM.doAction(actionBuilder(item))
  }
}