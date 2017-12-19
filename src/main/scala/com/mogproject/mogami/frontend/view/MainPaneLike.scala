package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.model.{GameControl, ModeType}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.board.{SVGArea, SVGAreaLayout}
import com.mogproject.mogami.frontend.view.control.{CommentArea, ControlBar, ControlBarType}
import com.mogproject.mogami.frontend.view.sidebar.{SideBarLeft, SideBarLike, SideBarRight}
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLElement

import scala.collection.mutable
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
trait MainPaneLike extends WebComponent with Observer[SideBarLike] {

  def isMobile: Boolean

  def isLandscape: Boolean

  //
  // Utility
  //

  private[this] def getSideBarWidth: Int = sidebars.map(_.currentWidth).sum + 3

  def widenMainPane(): Unit = mainContent.style.width = 100.pct

  def recenterMainPane(): Unit = mainContent.style.width = s"calc(100% - ${getSideBarWidth}px)"

  override def handleUpdate(subject: SideBarLike): Unit = recenterMainPane()


  private[this] val svgAreas: mutable.ListBuffer[SVGArea] = mutable.ListBuffer.empty

  private[this] var controlBar: Option[ControlBar] = None

  private[this] val commentArea: CommentArea = CommentArea(isDisplayOnly = isMobile, isModal = false)

  private[this] val mainArea = div().render

  private[this] val mainContent = div(
    cls := "main-area-wrapper",
    mainArea
  ).render

  private[this] val sideBarRight: Option[SideBarRight] = (!isMobile).option(new SideBarRight)

  private[this] val sideBarLeft: Option[SideBarLeft] = (!isMobile).option(new SideBarLeft)

  private[this] val sidebars: Seq[SideBarLike] = sideBarRight.toSeq ++ sideBarLeft

  private[this] lazy val clickSound = audio(source(src := "assets/mp3/click.mp3", tpe := "audio/wav")).render

  override lazy val element: Element = div(
    if (isMobile) {
      mainContent
    } else {
      div(cls := "row no-margin no-overflow",
        sidebars.map(_.element),
        mainContent
      )
    },
    clickSound
  ).render

  /**
    * Initialize or reload main boards
    *
    * `controlSection` should be updated.
    *
    * @param layout area layout
    */
  def renderSVGAreas(numAreas: Int, pieceWidth: Int, layout: SVGAreaLayout): Unit = {
    svgAreas.foreach(_.terminate())
    svgAreas.clear()
    svgAreas ++= (0 until numAreas).map(n => SVGArea(n, layout))

    val areaWidth = layout.areaWidth(pieceWidth)

    val node = (isMobile, isLandscape) match {
      case (true, true) => createMobileLandscapeMain(areaWidth)
      case (true, false) => createMobilePortraitMain(areaWidth)
      case (_, _) => createPCPortraitMain(areaWidth)
    }

    mainArea.appendChild(node.render)
    resizeSVGAreas(pieceWidth, layout)
  }

  private[this] def createPCPortraitMain(areaWidth: Int): TypedTag[Div] = {
    controlBar.foreach(_.terminate())
    controlBar = Some(ControlBar(ControlBarType.Normal))

    div(cls := "container-fluid",
      div(
        id := "main-area",
        cls := "main-area-pc",
        if (svgAreas.size == 2) {
          div(cls := "row", svgAreas.map(e => div(cls := "col-xs-6", e.element)))
        } else {
          div(svgAreas.head.element)
        }
      ),
      controlBar.get.element,
      commentArea.element
    )
  }

  private[this] def createMobilePortraitMain(areaWidth: Int): TypedTag[Div] = {
    controlBar.foreach(_.terminate())
    controlBar = Some(ControlBar(ControlBarType.Small))

    div(
      div(cls := "container-fluid",
        div(
          id := "main-area",
          cls := "main-area-mobile-portrait",
          svgAreas.head.element
        ),
        controlBar.get.element,
        commentArea.element
      )
    )
  }

  private[this] def createMobileLandscapeMain(areaWidth: Int): TypedTag[Div] = {
    controlBar.foreach(_.terminate())
    controlBar = (svgAreas.size == 1).option(ControlBar(ControlBarType.Small))

    div(cls := "container-fluid",
      div(
        id := "main-area",
        div(cls := "row",
          div(cls := "col-xs-6", svgAreas.head.element),
          div(cls := "col-xs-6", (svgAreas.size > 1).fold(svgAreas(1).element, controlBar.map(_.element).toSeq :+ commentArea.element))
        )
      )
    )
  }

  def resizeSVGAreas(pieceWidth: Int, layout: SVGAreaLayout): Unit = {
    val areaWidth = layout.areaWidth(pieceWidth)

    val w = (isMobile, isLandscape) match {
      case (false, _) => (areaWidth + 70) * svgAreas.size - 50 // +20 for 1 board, +90 for 2 boards
      case (true, false) => areaWidth
      case (true, true) => areaWidth * 2 + 60
    }

    dom.document.getElementById("main-area") match {
      case e: HTMLElement => e.style.width = w.px
      case _ =>
    }
  }

  def updateSVGArea(f: SVGArea => Unit): Unit = svgAreas.foreach(f)

  def updateSVGArea(areaId: Int, f: SVGArea => Unit): Unit = f(svgAreas(areaId))

  def updateControlBars(f: ControlBar => Unit): Unit = {
    controlBar.foreach(f)
    sideBarLeft.foreach(sb => f(sb.controlBar))
  }

  def updateComment(modeType: ModeType, comment: String): Unit = commentArea.refresh(modeType, comment)

  def updateBranchArea(gameControl: Option[GameControl], recordLang: Language, modeType: ModeType, newBranchMode: Boolean): Unit = {
    sideBarLeft.foreach(_.branchArea.refresh(gameControl, recordLang, modeType, newBranchMode))
  }

  def playClickSound(): Unit = {
    clickSound.pause()
    clickSound.currentTime = 0
    clickSound.play()
  }

  def initialize(): Unit = {
    sidebars.foreach(_.addObserver(this))
    if (isMobile) widenMainPane() else recenterMainPane()
  }

  initialize()
}
