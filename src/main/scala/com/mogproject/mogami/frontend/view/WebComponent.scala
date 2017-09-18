package com.mogproject.mogami.frontend.view

import org.scalajs.dom.{Element, Node}

/**
  *
  */
trait WebComponent {
  def element: Element

  def materialize(parent: Element): Unit = {
    parent.appendChild(element)
  }
}

object WebComponent {
  def removeElement(elem: Node): Unit = elem.parentNode.removeChild(elem)

  def removeElements(elems: Iterable[Node]): Unit = elems.foreach(removeElement)
}