package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.board.{SVGArea, SVGAreaLayout}
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div

import scala.collection.mutable
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
trait MainPaneLike extends WebComponent {

  def isMobile: Boolean

  def isLandscape: Boolean

  private[this] val svgAreas: mutable.ListBuffer[SVGArea] = mutable.ListBuffer.empty

  private[this] val mainArea = div().render

  private[this] val mainContent = div(
    cls := "main-area-wrapper",
    mainArea
  ).render

  private[this] lazy val clickSound = audio(source(src := "assets/mp3/click.mp3", tpe := "audio/wav")).render

  override lazy val element: Element = div(
    if (isMobile) {
      mainContent
    } else {
      div(cls := "row no-margin no-overflow",
        // todo: add sidebars
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
  def renderSVGAreas(numAreas: Int, layout: SVGAreaLayout): Unit = {
    svgAreas.foreach(_.terminate())
    svgAreas.clear()

    svgAreas ++= (0 until numAreas).map(n => SVGArea(n, layout))

    val node = (isMobile, isLandscape) match {
      case (true, true) => createMobileLandscapeMain()
      case (true, false) => createMobilePortraitMain()
      case (_, _) => createPCPortraitMain()
    }

    mainArea.appendChild(node.render)
  }

  private[this] def createPCPortraitMain(): TypedTag[Div] = {
    val numAreas = svgAreas.size
    div(
      div(cls := "container-fluid",
        div(cls := "main-area main-area-pc",
          // todo
          //          width := (layout.viewBoxBottomRight.x +70) * numAreas - 50, // +20 for 1 board, +90 for 2 boards
          if (numAreas == 2) {
            div(cls := "row",
              div(cls := "col-xs-6", svgAreas.head.element), div(cls := "col-xs-6", svgAreas(1).element)
            )
          } else {
            div(
              svgAreas.head.element
            )
          }
        )
        // todo: control bar
      )
      // todo: other gadgets
    )
  }

  private[this] def createMobilePortraitMain(): TypedTag[Div] = div(
    div(cls := "container-fluid",
      div(cls := "main-area main-area-mobile-portrait",
        //        width := canvasWidth,
        svgAreas.head.element
      )
    )
  )


  private[this] def createMobileLandscapeMain(): TypedTag[Div] = div(cls := "container-fluid",
    div(cls := "main-area",
      //      width := canvasWidth * 2 + 60,
      div(cls := "row",
        div(cls := "col-xs-6", svgAreas.head.element),
        div(cls := "col-xs-6", (svgAreas.size > 1).fold(svgAreas(1).element, "")) //, Seq(controlBarArea, chatArea.dom)))
      )
    )
  )


  def updateSVGArea(f: SVGArea => Unit): Unit = svgAreas.foreach(f)

  def updateSVGArea(areaId: Int, f: SVGArea => Unit): Unit = f(svgAreas(areaId))

  def playClickSound(): Unit = {
    clickSound.pause()
    clickSound.currentTime = 0
    clickSound.play()
  }
}
