package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.Square
import com.mogproject.mogami.frontend.api.AnimateElementExtended
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.raw.{SVGCircleElement, SVGElement}
import org.scalajs.dom.svg.RectElement

import scalatags.JsDom.all._
import scalatags.JsDom.svgTags.animate

/**
  * Visual effects
  */
trait SVGBoardEffector {
  self: SVGBoard =>

  object effect {

    def insertElement(elem: Element): Unit = svgElement.insertBefore(elem, borderElement)

    def appendElement(elem: Element): Unit = svgElement.appendChild(elem)

    def removeElement(elem: Element): Unit = elem.parentNode.removeChild(elem)

    // todo: refactor

    private[this] var cursorElem: Option[RectElement] = None

    private[this] var selectedElem: Option[RectElement] = None

    private[this] var selectingElem: Option[SVGCircleElement] = None

    private[this] var moveEffectElem: Option[SVGCircleElement] = None

    private[this] var lastMoveElems: Seq[RectElement] = Seq.empty


    def startCursorEffect(square: Square): Unit = {
      val elem = getRect(square).shrink(12).toSVGRect(cls := "board-cursor").render
      stopCursorEffect()
      insertElement(elem)
      cursorElem = Some(elem)
    }

    def stopCursorEffect(): Unit = {
      cursorElem.foreach(removeElement)
      cursorElem = None
    }

    def startSelectedSquareEffect(square: Square): Unit = {
      val elem = getRect(square).shrink(9).toSVGRect(cls := "board-selected").render
      stopSelectedSquareEffect()
      insertElement(elem)
      selectedElem = Some(elem)
    }

    def stopSelectedSquareEffect(): Unit = {
      selectedElem.foreach(removeElement)
      selectedElem = None
    }

    def startFlashCursorEffect(square: Square): Unit = {
      val elem = getRect(square).shrink(12).toSVGRect(cls := "board-flash").render
      insertElement(elem)
      dom.window.setTimeout(() => removeElement(elem), 300)
    }

    def startMoveEffect(moveTo: Square): Unit = {
      import scalatags.JsDom.svgAttrs._

      val sr = SVGBoard.PIECE_WIDTH * 2 / 5

      val props = Seq(
        dur := "0.4s",
        begin := "indefinite",
        fill := "freeze",
        repeatCount := 1
      )

      val animateElems = Seq(
        animate(attributeName := "r", to := SVGBoard.PIECE_WIDTH, props),
        animate(attributeName := "opacity", from := 1, to := 0, props)
      ).map(_.render.asInstanceOf[AnimateElementExtended])

      val elem = getRect(moveTo).center.toSVGCircle(sr, cls := "board-move", animateElems).render
      moveEffectElem = Some(elem)

      appendElement(elem)
      animateElems.foreach(_.beginElement())

      dom.window.setTimeout(() => removeElement(elem), 400)
    }

    def startSelectingEffect(square: Square): Unit = {
      import scalatags.JsDom.svgAttrs._

      val sr = SVGBoard.PIECE_WIDTH * 2 / 5

      val props = Seq(
        dur := "3s",
        begin := "indefinite",
        fill := "freeze",
        repeatCount := "indefinite"
      )

      val animateElems = Seq(
        animate(attributeName := "r", to := SVGBoard.PIECE_WIDTH * 2, props),
        animate(attributeName := "opacity", from := 1, to := -0.5, props)
      ).map(_.render.asInstanceOf[AnimateElementExtended])

      val elem = getRect(square).center.toSVGCircle(sr, cls := "board-move", animateElems).render
      moveEffectElem = Some(elem)

      stopSelectingEffect()
      appendElement(elem)
      animateElems.foreach(_.beginElement())
      selectingElem = Some(elem)
    }

    def stopSelectingEffect(): Unit = {
      selectingElem.foreach(removeElement)
      selectingElem = None
    }

    def startLastMoveEffect(lastMoveSquares: Seq[Square]): Unit = {
      val elems = lastMoveSquares.map(getRect(_).toSVGRect(cls := "board-last").render)
      stopLastMoveEffect()
      elems.foreach(insertElement)
      lastMoveElems = elems
    }

    def stopLastMoveEffect(): Unit = {
      lastMoveElems.foreach(removeElement)
      lastMoveElems = Seq.empty
    }

  }

}
