package com.mogproject.mogami.frontend.api.bootstrap

import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.jquery.{JQuery, jQuery}

import scala.scalajs.js

/**
  *
  */
object Tooltip {
  def enableHoverToolTip(): Unit = enableHoverToolTip(jQuery("""[data-toggle="tooltip"]"""))

  def enableHoverToolTip(elem: Element): Unit = enableHoverToolTip(jQuery(elem))

  def enableHoverToolTip(elems: Seq[Element]): Unit = elems.foreach(enableHoverToolTip)

  def enableHoverToolTip(jQuery: JQuery): Unit = {
    jQuery.asInstanceOf[BootstrapJQuery].tooltip {
      val r = js.Dynamic.literal()
      r.trigger = "hover"
      r.asInstanceOf[TooltipOptions]
    }
  }

  def hideToolTip(jQuery: JQuery): Unit = jQuery.asInstanceOf[BootstrapJQuery].tooltip("hide")

  def hideToolTip(elem: Element): Unit = hideToolTip(jQuery(elem))

  def hideToolTip(elems: Seq[Element]): Unit = elems.foreach(hideToolTip)

  def hideAllToolTip(): Unit = {
    jQuery("""[data-toggle="tooltip"]""").asInstanceOf[BootstrapJQuery].tooltip("hide")
  }

  def display(elem: Element, message: String, displayTime: Int = 1000): Unit = {
    jQuery(elem).attr("data-original-title", message).asInstanceOf[BootstrapJQuery].tooltip("show")
    val f = () => jQuery(elem).asInstanceOf[BootstrapJQuery].tooltip("hide").attr("data-original-title", "")
    dom.window.setTimeout(f, displayTime)
  }
}
