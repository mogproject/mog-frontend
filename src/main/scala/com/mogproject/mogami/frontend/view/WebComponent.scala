package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement.TooltipPlacement
import org.scalajs.dom
import org.scalajs.dom.html.Button
import org.scalajs.dom.raw.{HTMLElement, SVGElement}
import org.scalajs.dom.{Element, Node, Text}
import org.scalajs.jquery.jQuery

import scalatags.JsDom.TypedTag
import scalatags.JsDom.Modifier
import scalatags.JsDom.all.{button => btn, _}

/**
  *
  */
trait WebComponent {
  def element: Element

  def terminate(): Unit = {
    WebComponent.removeAllChildElements(element)
    WebComponent.removeElement(element)
  }

  def replaceClass(elem: Element, replaceKeyword: String, newClassName: String*): Unit = {
    val removeCandidates = for {
      i <- 0 until elem.classList.length
      cls = elem.classList(i)
      if cls.contains(replaceKeyword)
    } yield {
      cls
    }
    removeCandidates.foreach(elem.classList.remove)
    newClassName.foreach(elem.classList.add)
  }


  def disableElement(): Unit = setDisabled(true)

  def enableElement(): Unit = setDisabled(false)

  def setDisabled(disabled: Boolean): Unit = element match {
    case e: HTMLElement => e.disabled = disabled
    case _ =>
  }

  def isDisabled: Boolean = element match {
    case e: HTMLElement => e.disabled.contains(true)
    case _ => false
  }

  def hide(): Unit = WebComponent.hideElement(element)

  def show(): Unit = WebComponent.showElement(element)

  def doAction(action: PlaygroundAction, delayMS: Int = 0): Unit = if (delayMS <= 0) {
    PlaygroundSAM.doAction(action)
  } else {
    dom.window.setTimeout(() => PlaygroundSAM.doAction(action), delayMS)
  }

  def defaultButtonWithManualTooltip(tooltipPlacement: TooltipPlacement, modifier: Modifier*): TypedTag[Button] = btn(
    cls := "btn btn-default",
    tpe := "button",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := tooltipPlacement.toString,
    modifier
  )

  //
  // Class name shortcuts
  //
  def classButtonDefault: String = "btn-default"

  def classButtonDefaultBlock: String = "btn-default btn-block"

  def classButtonPrimaryBlock: String = "btn-primary btn-block"

  //
  // AttrPair shortcuts
  //
  def dismissModalNew: Modifier = data("dismiss") := "modal"
}

object WebComponent {
  def removeElement(elem: Node): Unit = jQuery(elem).remove()

  def removeElements(elems: Iterable[Node]): Unit = elems.foreach(removeElement)

  def removeAllChildElements(elem: Node): Unit = jQuery(elem).empty()

  def replaceChildElement(parent: Node, elem: Node): Unit = {
    removeAllChildElements(parent)
    parent.appendChild(elem)
    // debug:
    //    if (parent == null) {
    //      println(s"Error: parent not initialized in replaceChildElement elem=${elem}")
    //    } else {
    //
    //    }
  }

  def replaceChildElements(parent: Node, elems: Iterable[Node]): Unit = {
    removeAllChildElements(parent)
    elems.foreach(parent.appendChild)
    // debug:
    //    if (parent == null) {
    //      val msg = elems match {
    //        case (x: Text) :: Nil => "[" + x.wholeText + "]"
    //        case _ => elems.toString
    //      }
    //
    //      println(s"Error: parent not initialized in replaceChildElements elems=${msg}")
    //    } else {
    //
    //    }
  }

  def showElement(elem: Element): Unit = elem match {
    case e: HTMLElement => e.style.display = display.block.v
    case e: SVGElement => e.setAttribute("visibility", "visible")
    case _ =>
  }

  def hideElement(elem: Element): Unit = elem match {
    case e: HTMLElement => e.style.display = display.none.v
    case e: SVGElement => e.setAttribute("visibility", "hidden")
    case _ =>
  }

  def clearClass(elem: Element): Unit = {
    while (elem.classList.length > 0) elem.classList.remove(elem.classList(0))
  }

  def setClass(elem: Element, className: String): Unit = {
    setClass(elem, Seq(className))
  }

  def setClass(elem: Element, classNames: Seq[String]): Unit = {
    clearClass(elem)
    classNames.foreach(elem.classList.add)
  }
}