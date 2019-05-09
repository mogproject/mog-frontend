package com.mogproject.mogami.frontend.view.footer

import com.mogproject.mogami.frontend.FrontendSettings
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait FooterLike extends WebComponent {
  override lazy val element: Div = div(
    hr(),
    small(p(cls := "footer-text",
      s"Shogi Playground © 2017-${FrontendSettings.currentYear} ",
      a(href := FrontendSettings.url.authorSiteUrl, "mogproject"),
      " - ",
      a(href := FrontendSettings.url.donationUrl, "Donate $5"),
      br,
      "Try ", a(href := FrontendSettings.url.shogiBotUrl, "Shogi Bot"),
      " and ", a(href := FrontendSettings.url.playgroundLiveUrl, "Shogi Playground Live!"),
      " - ",
      a(href := "./privacy/", "Privacy Policy")
    ))
  ).render

}
