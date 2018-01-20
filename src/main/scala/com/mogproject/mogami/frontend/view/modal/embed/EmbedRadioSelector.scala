package com.mogproject.mogami.frontend.view.modal.embed

import com.mogproject.mogami.frontend.view.button.RadioButton
import com.mogproject.mogami.frontend.view.i18n.Messages
import org.scalajs.dom.Element

abstract class EmbedRadioSelector[A](items: Seq[A],
                                     messageFunc: Messages => Map[A, String],
                                     clickAction: () => Unit
                                    ) extends EmbedSettingRow[A] {

  private[this] val button: RadioButton[A] = RadioButton(items, messageFunc,
    (v: A) => {
      button.select(v)
      clickAction()
    }
  )

  override protected def buttonElem: Element = button.element

  def select(item: A): Unit = button.select(item)

  def getValue: A = button.getValue
}
