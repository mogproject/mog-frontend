package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.analyze.{CheckmateButton, PointCountButton}
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class AnalyzeMenu(isMobile: Boolean) extends AccordionMenu {

  override lazy val ident: String = "Analyze"
  override lazy val titleLabel = DynamicComponent(_.ANALYZE)
  override lazy val icon: String = "education"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)

  lazy val checkmateButton = new CheckmateButton(isMobile)

  lazy val pointCountButton = new PointCountButton(isMobile)

  override lazy val content: JsDom.TypedTag[Div] = div(
    label(DynamicComponent(_.ANALYZE_FOR_CHECKMATE).element),
    checkmateButton.element,
    br(),
    label(DynamicComponent(_.COUNT_POINT_LABEL).element),
    pointCountButton.element
  )
}
