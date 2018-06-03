package com.mogproject.mogami.frontend.view.nav

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.dialog.MenuDialogAction
import com.mogproject.mogami.frontend.view.action.ResignButton
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.button.CommandButton
import com.mogproject.mogami.frontend.view.i18n.MessagesEnglish
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
trait NavBarLike extends WebComponent {

  def isMobile: Boolean

  def embeddedMode: Boolean

  def brandName: String = "Shogi Playground"

  def brandUrl: Option[String] = None

  lazy val flipButton: FlipButton = new FlipButton

  lazy val resignButton: ResignButton = ResignButton(isSmall = true, confirm = false)

  lazy val menuButton: WebComponent = CommandButton(
    classButtonDefault + " " + classButtonThin + " menu-btn",
    onclick := { () => doAction(MenuDialogAction(true)) }
  ).withTextContent(MessagesEnglish.MENU, "menu-hamburger")

  lazy val linkButton = new PlaygroundLinkButton

  def buttons: Seq[WebComponent] = Seq(flipButton) ++
    (!isMobile && !embeddedMode).option(resignButton) ++
    (isMobile && !embeddedMode).option(menuButton) ++
    embeddedMode.option(linkButton)

  def classNames: String = "navbar navbar-default navbar-fixed-top"

  def brandElem: Element = brandUrl.map { u =>
    li(cls := "hidden-xs", a(cls := "navbar-brand", href := u, brandName))
  }.getOrElse {
    li(cls := "hidden-xs navbar-brand", brandName)
  }.render

  lazy val navElem: Element = tag("nav")(
    cls := classNames,
    div(cls := "container", padding := 0,
      div(cls := "navbar-header",
        ul(cls := "nav navbar-nav",
          (!embeddedMode).option(brandElem),
          buttons.map(b => li(b.element))
        )
      )
    )
  ).render

  override lazy val element = navElem
}
