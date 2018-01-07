package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class AboutMenu extends AccordionMenu {
  override lazy val ident: String = "About"
  override lazy val titleLabel = DynamicComponent(_.ABOUT_THIS_SITE)
  override lazy val icon: String = "info-sign"
  override lazy val content: JsDom.TypedTag[Div] = div(
    p(i(""""Run anywhere. Needs NO installation."""")),
    p("Shogi Playground is a platform for all shogi --Japanese chess-- fans in the world." +
      " This mobile-friendly website enables you to manage, analyze, and share shogi games as well as mate problems."),
    p("If you have any questions, trouble, or suggestion, please tell the ",
      a(href := FrontendSettings.url.authorContactUrl, target := "_blank", "author"),
      ". Your voice matters."),
    br(),
    label("Special Thanks"),
    p(
      "Piece Graphics - ",
      a(href := FrontendSettings.url.credit.shineleckomaUrl, target := "_blank", "shineleckoma")
    ),
    p(
      "Piece Fotns - ",
      a(href := FrontendSettings.url.credit.loraFontsUrl, target := "_blank", "Lora Fonts")
    )
  )
  override lazy val visibleMode = Set(PlayModeType, ViewModeType, EditModeType)
}
