package com.mogproject.mogami.frontend.view.menu

import scalatags.JsDom.all._

/**
  *
  */
object InfoMenu extends MenuSection {
  override lazy val accordions: Seq[AccordionMenu] = Seq(AccordionMenu(
    "About",
    "About This Site",
    "info-sign",
    isExpanded = false,
    isVisible = true,
    div(
      p(i(""""Run anywhere. Needs NO installation."""")),
      p("Shogi Playground is a platform for all shogi --Japanese chess-- fans in the world." +
        " This mobile-friendly website enables you to manage, analyze, and share shogi games as well as mate problems."),
      p("If you have any questions, trouble, or suggestion, please tell the ",
        a(href := "https://twitter.com/mogproject", target := "_blank", "author"),
        ". Your voice matters.")
    )))
}
