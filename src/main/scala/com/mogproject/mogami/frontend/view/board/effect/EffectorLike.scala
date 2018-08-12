package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.api.AnimateElementExtended
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.system.BrowserInfo
import org.scalajs.dom
import org.scalajs.dom.raw.SVGElement

import scala.util.{Failure, Success, Try}
import scalatags.JsDom.TypedTag


/**
  * Generic effector trait
  */
trait EffectorLike[A, T <: EffectorTarget] {

  // local variables
  private[this] var currentValue: Option[A] = None

  private[this] var currentElements: Seq[SVGElement] = Seq.empty

  private[this] var currentCallback: () => Unit = () => {}

  // public methods
  def target: T

  def generateElements(x: A): Seq[TypedTag[SVGElement]]

  def materialize(elems: Seq[SVGElement]): Unit

  /**
    * Self-destruction after this time (ms)
    */
  def autoDestruct: Option[Int] = None

  def start(x: A, callback: () => Unit = { () => }): Unit = {
    stop()

    // set callback
    currentCallback = callback

    // render SVG tags
    val svgElems = generateElements(x).map(_.render).toList // stabilize elements (to avoid being a stream)

    // update local variables
    currentElements = svgElems
    currentValue = Some(x)

    // materialize
    materialize(currentElements)

    // start animation
    if (BrowserInfo.isAnimationSupported) {
      Try(svgElems.foreach(startAnimation)) match {
        case Success(_) => // ok
        case Failure(e) =>
          println(e.getMessage)
          BrowserInfo.isAnimationSupported = false

          // set visual effect disabled
          dom.window.setTimeout(() => PlaygroundSAM.doAction(UpdateConfigurationAction(_.copy(visualEffectEnabled = false))), 100)
      }
    }

    // set self-destruction
    autoDestruct.foreach(n => dom.window.setTimeout({ () => stop() }, n))
  }

  def stop(): Unit = {
    currentCallback()
    currentCallback = () => {}
    currentElements.foreach(WebComponent.removeElement)
    currentElements = Seq.empty
    currentValue = None
  }

  /**
    * Re-evaluate the 'start' function with a current value
    */
  def restart(): Unit = currentValue.foreach(start(_))

  private[this] def startAnimation(parentElem: SVGElement): Unit = {
    for {
      i <- 0 until parentElem.childElementCount
    } {
      val node = parentElem.childNodes(i)
      if (node.nodeName == "animate" || node.nodeName == "animateTransform") {
        node.asInstanceOf[AnimateElementExtended].beginElement()
      }
    }
  }


  protected final def createAnimateElem(attribute: String, toOrValues: Any, duration: String = ""): TypedTag[SVGElement] = {
    import scalatags.JsDom.all._
    import scalatags.JsDom.svgAttrs._
    import scalatags.JsDom.svgTags.animate

    val fromAutoDestruct: Seq[Modifier] = autoDestruct.map { n =>
      Seq(dur := f"${n / 1000.0}%.2fs", fill := "freeze", repeatCount := 1)
    }.getOrElse(
      Seq(dur := duration, repeatCount := "indefinite")
    )

    val vals: Modifier = toOrValues match {
      case xs: Seq[Any] if xs.length > 1 => values := xs.mkString(";")
      case Seq(x) => to := x.toString
      case _ => to := toOrValues.toString
    }

    val props = Seq(attributeName := attribute, begin := "indefinite") ++ fromAutoDestruct :+ vals
    animate(props: _*)
  }
}
