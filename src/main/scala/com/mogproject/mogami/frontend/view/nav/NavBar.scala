package com.mogproject.mogami.frontend.view.nav

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
case class NavBar(buttons: Seq[WebComponent],
                  isMobile: Boolean = false,
                  brandName: String = "Shogi Playground",
                  brandUrl: Option[String] = None,
                  classNames: String = "navbar navbar-default navbar-fixed-top"
                 ) extends WebComponent {

  override lazy val element: Div = div(
    div(cls := "container", padding := 0,
      div(cls := "navbar-header",
        ul(cls := "nav navbar-nav",
          li(cls := "hidden-xs", a(cls := "navbar-brand", brandUrl.toSeq.flatMap(u => Seq(href := u, target := "_blank")), brandName)),
          buttons.map(b => li(b.element))

          //          li(modeLabel),
          //          li(flipButton.dom),
          //          li(resignButton.dom),
          //          isMobile.fold(li(paddingLeft := "10px", div(menuButton.dom)), "")
        )
      )
    )
  ).render
}
