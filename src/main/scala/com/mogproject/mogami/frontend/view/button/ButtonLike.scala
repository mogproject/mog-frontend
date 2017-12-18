package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.view.event.EventManageable
import com.mogproject.mogami.frontend.view.{English, Language, WebComponent}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLElement

/**
  * todo: merge with RadioButton
  * deprecated
  */
trait ButtonLike[Key, Input <: HTMLElement, Output <: Element] extends WebComponent with EventManageable {
  protected def keys: Seq[Key]

  protected def labels: Map[Language, Seq[String]] = Map.empty

  protected def generateInput(key: Key): Input

  protected def invoke(key: Key): Unit

  protected def defaultValue: Key = keys.head

  protected def defaultLanguage: Language = English

  private[this] lazy val labelMap: Map[Language, Map[Key, String]] = labels.map { case (l, s) => l -> keys.zip(s).toMap }

  protected lazy val inputMap: Map[Key, Input] = keys.map(k => k -> generateInput(k)).toMap

  protected def inputs: Seq[Input] = keys.map(inputMap)

  protected def updateLabelColor(elem: HTMLElement, isActive: Boolean): Unit = {
    replaceClass(elem, "ctive", isActive.fold("active", "notActive"))
  }

  def initialize(): Unit = {
    inputMap.foreach { case (k, e) => setClickEvent(e, { () => updateValue(k); invoke(k) }) }
    updateLabel(defaultLanguage)
    updateValue(defaultValue)
  }

  def updateLabel(lang: Language): Unit = if (labels.nonEmpty) inputMap.foreach { case (k, e) => e.innerHTML = labelMap(lang)(k) }

  def updateValue(newValue: Key): Unit = {
    inputMap.foreach { case (k, e) => updateLabelColor(e, k == newValue) }
  }

  def getValue: Key = inputMap.find(_._2.classList.contains("active")).map(_._1).getOrElse {
    throw new RuntimeException("Could not find the selected value")
  }


  initialize()
}
