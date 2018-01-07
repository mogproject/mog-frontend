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
  override lazy val titleLabel = DynamicComponent(_.BRANCH)
  override lazy val icon: String = "share-alt"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType)

  lazy val branchArea = BranchArea(true)

  override lazy val content: JsDom.TypedTag[Div] = div(
    branchArea.element
  )
}

