package com.mogproject.mogami.frontend.view.setting

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.BasePlaygroundConfiguration
import com.mogproject.mogami.frontend.view.button.DropdownMenu
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
case class DropdownSelector[A](labelString: String,
                               items: Vector[(A, String)],
                               f: A => BasePlaygroundConfiguration => BasePlaygroundConfiguration,
                               separatorIndexes: Seq[Int] = Seq.empty) extends WebComponent {
  private[this] val button = DropdownMenu[A](
    items.map(_._1),
    items.map { case (k, v) => k -> Map[Language, String](English -> v) }.toMap,
    DropdownMenu.buildClickAction(k => UpdateConfigurationAction(f(k))),
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
