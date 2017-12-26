package com.mogproject.mogami.frontend.view.footer

import com.mogproject.mogami.frontend.Settings
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
      "Shogi Playground © 2017 ",
      a(href := Settings.url.authorSiteUrl, target := "_blank", "mogproject"),
      " - ",
      a(href := Settings.url.donationUrl, target := "_blank", "Donate me"),
      br,
      "Try ", a(href := Settings.url.shogiBotUrl, target := "_blank", "Shogi Bot"),
      " and ", a(href := Settings.url.playgroundLiveUrl, target := "_blank", "Shogi Playground Live!")
    ))
  ).render

}
