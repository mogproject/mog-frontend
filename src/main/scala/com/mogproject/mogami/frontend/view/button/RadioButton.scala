package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.view.Language
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._

/**
  *
  */
case class RadioButton[Key](keys: Seq[Key],
                            override val labels: Map[Language, Seq[String]],
                            onClick: Key => Unit = { _: Key => {} },
                            buttonClasses: Seq[String] = Seq("btn-sm"),
                            buttonGroupClasses: Seq[String] = Seq("btn-group-justified"),
                            tooltip: Option[String] = None
                           ) extends ButtonLike[Key, Anchor, Div] {

  override protected def generateInput(key: Key): Anchor = a(
    cls := ("btn" :: "btn-primary" :: buttonClasses.toList).mkString(" ")
  ).render

  override protected def invoke(key: Key): Unit = onClick(key)

  override val element: Div = div(
    cls := "input-group",
    tooltip.map { s => Seq(data("toggle") := "tooltip", data("placement") := "bottom", data("original-title") := s) },
    div(cls := ("btn-group" :: buttonGroupClasses.toList).mkString(" "),
      inputs
    )
  ).render

}
