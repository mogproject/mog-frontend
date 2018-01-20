package com.mogproject.mogami.frontend.view.modal.embed

import com.mogproject.mogami.frontend.Messages
import com.mogproject.mogami.frontend.view.button.DropdownMenu
import org.scalajs.dom.Element


abstract class EmbedDropdownSelector[A](items: Vector[A],
                                        itemLabelFunc: Messages => Map[A, String],
                                        clickAction: () => Unit
                                       ) extends EmbedSettingRow[A] {

  private[this] val button: DropdownMenu[A] = DropdownMenu[A](items, itemLabelFunc, labelClass = "setting-row-dropdown", menuClass = "left", clickAction = v => {
    button.select(v)
    clickAction()
  })

  override protected def buttonElem: Element = button.element

  def select(item: A): Unit = button.select(item)

  def getValue: A = button.getValue
}

