package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.model.{PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend.view.analyze.{CheckmateButton, PointCountButton}
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class AnalyzeMenu(isMobile: Boolean) extends AccordionMenu {

  override lazy val ident: String = "Analyze"
  override lazy val title: String = ident
  override lazy val icon: String = "education"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)

  lazy val checkmateButton = new CheckmateButton(isMobile)

  lazy val pointCountButton = new PointCountButton(isMobile)

  override lazy val content: JsDom.TypedTag[Div] = div(
    label("Analyze for Checkmate"),
    checkmateButton.element,
    br(),
    label("Count points"),
    pointCountButton.element
  )
}
