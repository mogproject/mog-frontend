package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.board.{SVGArea, SVGAreaLayout}
import com.mogproject.mogami.frontend.view.control.{ControlBar, ControlBarType}
import com.mogproject.mogami.frontend.view.sidebar.{SideBarLeft, SideBarLike, SideBarRight}
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div

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
  }

  private[this] def createPCPortraitMain(areaWidth: Int): TypedTag[Div] = {
    controlBar.foreach(_.terminate())
    controlBar = Some(ControlBar(ControlBarType.Normal))
    val numAreas = svgAreas.size
    div(
      div(cls := "container-fluid",
        div(cls := "main-area main-area-pc",
          width := ((areaWidth + 70) * numAreas - 50).px, // +20 for 1 board, +90 for 2 boards
          if (numAreas == 2) {
            div(cls := "row",
              div(cls := "col-xs-6", svgAreas.head.element), div(cls := "col-xs-6", svgAreas(1).element)
            )
          } else {
            div(
              svgAreas.head.element
            )
          },
          controlBar.get.element
        )
      )
      // todo: other gadgets
    )
  }

  private[this] def createMobilePortraitMain(areaWidth: Int): TypedTag[Div] = {
    controlBar.foreach(_.terminate())
    controlBar = Some(ControlBar(ControlBarType.Small))
    div(
      div(cls := "container-fluid",
        div(cls := "main-area main-area-mobile-portrait",
          width := areaWidth.px,
          svgAreas.head.element
        ),
        controlBar.get.element
      )
    )
  }

  private[this] def createMobileLandscapeMain(areaWidth: Int): TypedTag[Div] = {
    controlBar.foreach(_.terminate())
    if (svgAreas.size == 1) controlBar = Some(ControlBar(ControlBarType.Small))
    div(cls := "container-fluid",
      div(cls := "main-area",
        width := (areaWidth * 2 + 60).px,
        div(cls := "row",
          div(cls := "col-xs-6", svgAreas.head.element),
          div(cls := "col-xs-6", (svgAreas.size > 1).fold(svgAreas(1).element, controlBar.map(_.element).toSeq))
        )
      )
    )
  }

  def updateSVGArea(f: SVGArea => Unit): Unit = svgAreas.foreach(f)

  def updateSVGArea(areaId: Int, f: SVGArea => Unit): Unit = f(svgAreas(areaId))

  def updateControlBars(f: ControlBar => Unit): Unit = {
    controlBar.foreach(f)
    sideBarLeft.foreach(sb => f(sb.controlBar))
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
