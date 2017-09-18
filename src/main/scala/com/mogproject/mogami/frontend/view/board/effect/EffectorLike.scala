package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.api.AnimateElementExtended
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag


/**
  * Generic effector trait
  */
trait EffectorLike[A] {
  private[this] var currentValue: Option[A] = None

  private[this] var currentElements: Seq[SVGElement] = Seq.empty

  def generateElements(x: A): Seq[TypedTag[SVGElement]]

  def generateAnimateElems(): Seq[TypedTag[SVGElement]] = Seq.empty

  def materialize(elems: Seq[SVGElement]): Unit

  /**
    * Self-destruction after this time (ms)
    */
  def autoDestruct: Option[Int] = None

  protected lazy val animateElems: Seq[Seq[AnimateElementExtended]] = Seq.empty

  def start(x: A): Unit = {
    stop()

    // render SVG tags
    val svgElems = generateElements(x).map { elem =>
      val x = elem.render
      val y = generateAnimateElems().map(_.render.asInstanceOf[AnimateElementExtended])
      y.foreach(x.appendChild) // add animate elements to the parent SVG tag
      (x, y)
    }.toList // stabilize elements (to avoid being a stream)

    // update local variables
    currentElements = svgElems.map(_._1)
    currentValue = Some(x)

    // materialize
    materialize(currentElements)

    // start animation
    svgElems.foreach(_._2.foreach(_.beginElement()))

    // set self-destruction
    autoDestruct.foreach(n => dom.window.setTimeout(() => stop(), n))
  }

  def stop(): Unit = {
    currentElements.foreach(WebComponent.removeElement)
    currentElements = Seq.empty
    currentValue = None
  }

  /**
    * Re-evaluate the 'start' function with a current value
    */
  def restart(): Unit = currentValue.foreach(start)

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
