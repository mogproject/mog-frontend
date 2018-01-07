package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class GameHelpMenu extends AccordionMenu {
  override lazy val ident: String = "Help"

  override def getTitle(messages: Messages): String = messages.HELP

  override lazy val icon: String = "question-sign"
  override lazy val content: JsDom.TypedTag[Div] = div(
    ul(
      li("Click on a player name to set game information."),
      li("In Play Mode, you can move pieces by a flick."),
      li("In View Mode, click (or hold) on any squares on the right-hand side of the board to move to the next position, and click (or hold) the left-hand side to the previous position."),
      li("If you click and hold 'forward' or 'backward' button, the position changes continuously.")
    )
  )
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)
}
