package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.model.{PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend.view.menu.analyze.CheckmateButton
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class AnalyzeMenu extends AccordionMenu {

  override lazy val ident: String = "Analyze"
  override lazy val title: String = ident
  override lazy val icon: String = "education"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)
  override lazy val content: JsDom.TypedTag[Div] = div(
    label("Analyze for Checkmate"),
    CheckmateButton.element,
    br(),
    label("Count points")
//    PointCountButton.output
  )
}
