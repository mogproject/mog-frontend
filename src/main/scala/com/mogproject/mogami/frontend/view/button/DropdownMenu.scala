package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.i18n.{DynamicComponentLike, Messages}
import org.scalajs.dom.Element

import scalatags.JsDom.Frag
import scalatags.JsDom.all._

/**
  * Dropdown menu with dynamic labels
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
