package com.mogproject.mogami.frontend.view.sidebar

import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
class EditHelpArea extends WebComponent {
  override lazy val element: Div = div(
    ul(
      li("Click on a player name to set the turn to move."),
      li("Double-click on a piece on the board to change its attributes:",
        ul(
          li("Black Unpromoted ->"),
          li("Black Promoted ->"),
          li("White Unpromoted ->"),
          li("White Promoted ->"),
          li("Black Unpromoted")
        )
      )
    )
  ).render
}
