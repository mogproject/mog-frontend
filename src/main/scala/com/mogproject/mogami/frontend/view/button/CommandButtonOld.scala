package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend._
import org.scalajs.dom.Element

import scalatags.JsDom.all._


/**
  * Command button
  */
case class CommandButton(className: String,  modifier: Modifier*) extends WebComponent {
  override def element: Element = button(
    cls := "btn " + className,
    tpe := "button",
    modifier
  ).render
}

//object CommandButton {
//  def apply(className: String, labelFunc: Messages => String, modifier: Modifier*): CommandButton = {
//    new CommandButton(className, DynamicComponent(labelFunc).element, modifier)
//  }
//
//}

case class CommandButtonOld(labelElem: Element,
                            clickAction: () => Unit,
                            buttonClass: Seq[String] = Seq("btn-default"),
                            isBlock: Boolean = true,
                            isDismiss: Boolean = false) extends CommandButtonLike(labelElem, clickAction, buttonClass, isBlock, isDismiss) {

}
//
