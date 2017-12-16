package com.mogproject.mogami.frontend.view

import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.{Element, Node}
import scalatags.JsDom.all._

/**
  *
  */
trait WebComponent {
  def element: Element

  def showElement(elem: HTMLElement): Unit = elem.style.display = display.block.v

  def hideElement(elem: HTMLElement): Unit = elem.style.display = display.none.v

  def terminate(): Unit = {
    WebComponent.removeAllChildElements(element)
    WebComponent.removeElement(element)
  }
}

object WebComponent {
  def removeElement(elem: Node): Unit = elem.parentNode.removeChild(elem)

  def removeElements(elems: Iterable[Node]): Unit = elems.foreach(removeElement)

  def removeAllChildElements(elem: Node): Unit = while (elem.hasChildNodes()) elem.removeChild(elem.firstChild)

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