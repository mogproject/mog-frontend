package com.mogproject.mogami.frontend.view.footer

import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait FooterLike extends WebComponent {
  def isDevMode: Boolean

  override lazy val element: Div = div(
    hr(),
    isDevMode.fold("[Dev Mode]", ""),
    small(p(cls := "footer-text",
      "Shogi Playground © 2017 ",
      a(href := "https://mogproject.com", target := "_blank", "mogproject"),
      " - ",
      a(href := "https://www.paypal.me/mogproject", target := "_blank", "Donate me"),
      " - ",
      "Try ", a(href := "https://www.facebook.com/shogibot/", target := "_blank", "Shogi Bot"),
      " and ", a(href := "https://live.mogproject.com/", target := "_blank", "Shogi Playground Live!")
    ))
  ).render

}
