package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.branch.BranchArea
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class BranchMenu extends AccordionMenu {

  override lazy val ident: String = "Branch"
  override lazy val title: String = ident
  override lazy val icon: String = "share-alt"
  override lazy val visibleMode = Set(PlayModeType)

  private[this] lazy val branchButton = BranchArea(true)

  override lazy val content: JsDom.TypedTag[Div] = div(
    branchButton.element
  )
}

