package com.mogproject.mogami.frontend.view.menu.setting

import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, BasePlaygroundModel}
import com.mogproject.mogami.frontend.view.button.DropdownMenu
import com.mogproject.mogami.frontend.view.{English, Language, WebComponent}
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
case class DropdownSelector[A](labelString: String,
                               items: Vector[(A, String)],
                               f: A => BasePlaygroundConfiguration => BasePlaygroundConfiguration,
                               separatorIndexes: Seq[Int] = Seq.empty) extends WebComponent {
  private[this] val button = DropdownMenu[A, BasePlaygroundModel](
    items.map(_._1),
    items.map { case (k, v) => k -> Map[Language, String](English -> v) }.toMap,
    k => UpdateConfigurationAction(f(k)),
    menuClass = "left",
    separatorIndexes = separatorIndexes
  )

  override def element: Element = div(
    marginBottom := 15.px,
    div(
      cls := "pull-right",
      marginTop := (-8).px,
      button.element
    ),
    label(labelString)
  ).render

  def select(item: A): Unit = button.select(item)
}
