package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.api.bootstrap.Tooltip
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.event.PointerHoldSensor
import com.mogproject.mogami.frontend.view.i18n.{DynamicComponentLike, DynamicHoverTooltipLike, DynamicPlaceholderLike, Messages}
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement.TooltipPlacement
import org.scalajs.dom
import org.scalajs.dom.html.{Button, Input, Span}
import org.scalajs.dom.raw.{HTMLElement, SVGElement}
import org.scalajs.dom.{Element, Node, Text}
import org.scalajs.jquery.jQuery

import scalatags.JsDom.TypedTag
import scalatags.JsDom.Modifier
import scalatags.JsDom.all.{button => btn, _}
import scalatags.generic.StylePair

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
    /** @note classList is not supported by IE9 and lower. */
    val removeCandidates = elem.getAttribute("class").split(" ").filter(_.contains(replaceKeyword))
    val jq = jQuery(elem)
    removeCandidates.foreach(jq.removeClass(_))
    jq.addClass(newClassName.mkString(" "))
  }

  def disableElement(): Unit = setDisabled(true)

  def enableElement(): Unit = setDisabled(false)

  def setDisabled(disabled: Boolean): Unit = element match {
    case e: HTMLElement =>
      Tooltip.hideToolTip(e)
      e.disabled = disabled
    case _ =>
  }

  def isDisabled: Boolean = element match {
    case e: HTMLElement => e.disabled.contains(true)
    case _ => false
  }

  def isVisible: Boolean = WebComponent.isVisibleElement(element)

  def hide(): Unit = WebComponent.hideElement(element)

  def show(displayStyle: StylePair[Element, String] = display.block): Unit = WebComponent.showElement(element, displayStyle)

  def doAction(action: PlaygroundAction, delayMS: Int = 0): Unit = if (delayMS <= 0) {
    PlaygroundSAM.doAction(action)
  } else {
    dom.window.setTimeout(() => PlaygroundSAM.doAction(action), delayMS)
  }

  //
  // Utility
  //
  private[this] def getGlyphiconFrags(text: String, glyphicon: String): Seq[Frag] = {
    text.nonEmpty.option(text).map(s => StringFrag(s + " ")).toSeq :+ WebComponent.glyph(glyphicon)
  }

  //
  // Class name shortcuts
  //
  def classButtonDefault: String = "btn-default"

  def classButtonBlock: String = "btn-block"

  def classButtonDefaultBlock: String = classButtonDefault + " " + classButtonBlock

  def classButtonPrimaryBlock: String = "btn-primary " + classButtonBlock

  //
  // AttrPair shortcuts
  //
  def dismissModal: Modifier = data("dismiss") := "modal"

  def clipboardTarget(id: String): Modifier = data("clipboard-target") := "#" + id

  //
  // Mutator
  //
  def withManualTooltip(placement: TooltipPlacement = TooltipPlacement.Bottom): WebComponent = {
    val newElem = element
    newElem.setAttribute("data-toggle", "tooltip")
    newElem.setAttribute("data-trigger", "manual")
    newElem.setAttribute("data-placement", placement.toString)
    WebComponent(newElem)
  }

  def withTextContent(text: String): WebComponent = {
    val newElem = element
    WebComponent.replaceChildElement(newElem, text.render)
    WebComponent(newElem)
  }

  def withTextContent(text: String, glyphicon: String): WebComponent = {
    val newElem = element
    WebComponent.replaceChildElements(newElem, getGlyphiconFrags(text, glyphicon).map(_.render))
    WebComponent(newElem)
  }

  def withHoldAction(pointerDown: () => Unit, pointerHold: () => Boolean): WebComponent = {
    element match {
      case thisElem: HTMLElement =>
        new WebComponent with PointerHoldSensor {
          override def element: Element = thisElem

          override def eventTarget: HTMLElement = thisElem

          override def pointerDownAction(clientX: Double, clientY: Double): Unit = pointerDown()

          override def pointerUpAction(clientX: Double, clientY: Double): Unit = {}

          override def pointerHoldAction(): Boolean = pointerHold()
        }
      case _ => this
    }
  }

  //
  // Dynamic components
  //
  def withDynamicInnerElements(f: Messages => Seq[Frag]): WebComponent = {
    val thisElem = element
    new WebComponent with DynamicComponentLike {
      override lazy val element: Element = thisElem

      override def getDynamicElements(messages: Messages): Seq[Frag] = f(messages)
    }
  }

  def withDynamicInnerElement(f: Messages => Frag): WebComponent = withDynamicInnerElements(f.andThen(Seq(_)))

  def withDynamicTextContent(f: Messages => String): WebComponent = withDynamicInnerElement(f.andThen(StringFrag))

  def withDynamicTextContent(f: Messages => String, glyphicon: String): WebComponent = withDynamicInnerElements { messages: Messages => getGlyphiconFrags(f(messages), glyphicon) }


  def withDynamicHoverTooltip(f: Messages => String, tooltipPlacement: TooltipPlacement = TooltipPlacement.Bottom): WebComponent = {
    val thisElem = element
    new WebComponent with DynamicHoverTooltipLike {
      override lazy val element: Element = thisElem

      override lazy val placement: TooltipPlacement = tooltipPlacement

      override def getTooltipMessage(messages: Messages): String = f(messages)
    }
  }

  def withDynamicPlaceholder(f: Messages => String): WebComponent = {
    element match {
      case thisElem: Input =>
        new WebComponent with DynamicPlaceholderLike {
          override lazy val element: Input = thisElem

          override def getPlaceholder(messages: Messages): String = f(messages)
        }
      case _ => this
    }
  }

}

object WebComponent {
  // Constructors
  def apply(elem: Element): WebComponent = new WebComponent {
    override def element: Element = elem
  }

  def apply[T <: Element](tag: TypedTag[T]): WebComponent = apply(tag.render)

  def dynamicLabel(f: Messages => String, modifier: Modifier*): WebComponent = {
    apply(label(modifier: _*)).withDynamicTextContent(f)
  }

  def dynamicSpan(f: Messages => String, modifier: Modifier*): WebComponent = {
    apply(span(modifier: _*)).withDynamicTextContent(f)
  }

  def dynamicDiv(f: Messages => String, modifier: Modifier*): WebComponent = {
    apply(div(modifier: _*)).withDynamicTextContent(f)
  }

  def dynamicDivElement(f: Messages => Frag, modifier: Modifier*): WebComponent = {
    apply(div(modifier: _*)).withDynamicInnerElement(f)
  }

  def dynamicDivElements(f: Messages => Seq[Frag], modifier: Modifier*): WebComponent = {
    apply(div(modifier: _*)).withDynamicInnerElements(f)
  }

  //
  // Utility Functions
  //
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

  def showElement(elem: Element, displayStyle: StylePair[Element, String]): Unit = elem match {
    case e: HTMLElement => e.style.display = displayStyle.v
    case e: SVGElement => e.setAttribute("visibility", "visible")
    case _ =>
  }

  def showElement(elem: Element): Unit = showElement(elem, display.block)

  def hideElement(elem: Element): Unit = elem match {
    case e: HTMLElement => e.style.display = display.none.v
    case e: SVGElement => e.setAttribute("visibility", "hidden")
    case _ =>
  }

  def isVisibleElement(elem: Element): Boolean = elem match {
    case e: HTMLElement => e.style.display != display.none.v
    case e: SVGElement => e.getAttribute("visibility") != "hidden"
    case _ => false
  }

  def clearClass(elem: Element): Unit = {
    jQuery(elem).removeClass()
  }

  def setClass(elem: Element, className: String): Unit = {
    setClass(elem, Seq(className))
  }

  def setClass(elem: Element, classNames: Seq[String]): Unit = {
    clearClass(elem)
    jQuery(elem).addClass(classNames.mkString(" "))
  }

  def glyph(glyphicon: String): TypedTag[Span] = span(cls := s"glyphicon glyphicon-${glyphicon}", aria.hidden := true)

}