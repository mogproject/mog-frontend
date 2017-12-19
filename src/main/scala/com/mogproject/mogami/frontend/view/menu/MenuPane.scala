package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.bootstrap.BootstrapJQuery
import org.scalajs.dom.html.Div
import org.scalajs.jquery.jQuery

import scala.scalajs.js
import scalatags.JsDom.all._

/**
  *
  */
case class MenuPane(isMobile: Boolean) extends WebComponent {
//  val linksMenu = new LinksMenu

//  val settingsMenu = new SettingsMenu

  lazy val sections: Seq[MenuSection] = Seq(
    HelpMenu,
    InfoMenu
//    linksMenu,
//    settingsMenu
  )

  override lazy val element: Div = div(
    cls := "panel-group", id := "accordion", role := "tablist", aria.multiselectable := true,
    sections.map(_.outputs)
  ).render

  def collapseMenu(): Unit = {
    jQuery(".panel-collapse").asInstanceOf[BootstrapJQuery].collapse {
      val r = js.Dynamic.literal()
      r.toggle = false
      r.parent = "#accordion" // necessary to keep group settings
      r
    }
    jQuery(".panel-collapse").asInstanceOf[BootstrapJQuery].collapse("hide")

    sections.foreach(_.collapseTitle())
  }

  def expandMenu(): Unit = {
    sections.foreach(_.expandTitle())
  }
}
