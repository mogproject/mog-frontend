package com.mogproject.mogami.frontend.view

import org.scalajs.dom.{Element, Node}

/**
  *
  */
trait WebComponent {
  def element: Element
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