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
case class DropdownSelector[A](labelFunc: Messages => String,
                               items: Vector[A],
                               itemLabelFunc: Messages => Map[A, String],
                               f: A => BasePlaygroundConfiguration => BasePlaygroundConfiguration,
                               separatorIndexes: Seq[Int] = Seq.empty) extends WebComponent {
  private[this] val labelElem = WebComponent.dynamicLabel(labelFunc)

  private[this] val button = DropdownMenu[A](
    items,
    itemLabelFunc,
    (k: A) => doAction(UpdateConfigurationAction(f(k))),
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
    labelElem.element
  ).render

  def select(item: A): Unit = button.select(item)
}
