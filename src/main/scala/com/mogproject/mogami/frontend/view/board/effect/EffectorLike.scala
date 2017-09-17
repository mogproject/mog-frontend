package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.api.AnimateElementExtended
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.raw.SVGElement

/**
  * Generic effector trait
  */
trait EffectorLike[A] {
  private[this] var currentValue: Option[A] = None

  private[this] var currentElements: Set[SVGElement] = Set.empty

  private[this] def removeElement(elem: Element): Unit = elem.parentNode.removeChild(elem)

  def generateElements(x: A): Set[SVGElement]

  def materialize(elem: SVGElement): Unit

  /**
    * Self-destruction after this time (ms)
    */
  def autoDestruct: Option[Int] = None

  def animateElems: Seq[AnimateElementExtended] = Seq.empty

  def start(x: A): Unit = {
    stop()

    currentElements = generateElements(x)
    currentValue = Some(x)

    // materialize
    currentElements.foreach(materialize)

    // start animation
    animateElems.foreach(_.beginElement())

    // set self-destruction
    autoDestruct.foreach(n => dom.window.setTimeout(() => stop(), n))
  }

  def stop(): Unit = {
    currentElements.foreach(removeElement)
    currentElements = Set.empty
  }

  /**
    * Re-evaluate the 'start' function with a current value
    */
  def restart(): Unit = currentValue.foreach(start)

}
