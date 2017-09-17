package com.mogproject.mogami.frontend.view

import org.scalajs.dom.Element

/**
  *
  */
trait WebComponent {
  def element: Element

  def materialize(parent: Element): Unit = {
    parent.appendChild(element)
  }
}
