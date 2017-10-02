package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.view.board.Cursor
import org.scalajs.dom.Element

/**
  * Target of effectors
  */
trait EffectorTarget {

  protected def thresholdElement: Element

  def materializeBackground[A <: Element](elem: A): A = {
    thresholdElement.parentNode.insertBefore(elem, thresholdElement)
    elem
  }

  def materializeBackground[A <: Element](elems: Seq[A]): Seq[A] = {
    elems.map(materializeBackground[A])
  }

  def materializeForeground[A <: Element](elem: A): A = {
    thresholdElement.parentNode.appendChild(elem)
    elem
  }

  def materializeForeground[A <: Element](elems: Seq[A]): Seq[A] = {
    elems.map(materializeForeground[A])
  }

  def clientPos2Cursor(clientX: Double, clientY: Double): Option[Cursor]
}
