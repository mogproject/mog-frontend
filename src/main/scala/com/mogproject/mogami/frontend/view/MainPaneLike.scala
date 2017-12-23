package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.model.DeviceType.DeviceType
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipDisabled, FlipEnabled}
import com.mogproject.mogami.frontend.util.PlayerUtil
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.board.{SVGArea, SVGAreaLayout}
import com.mogproject.mogami.frontend.view.control.{CommentArea, ControlBar, ControlBarType}
import com.mogproject.mogami.frontend.view.menu.MenuPane
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
trait MainPaneLike extends WebComponent with Observer[SideBarLike] with SAMObserver[BasePlaygroundModel] {
  def isMobile: Boolean

  def getSite: () => PlaygroundSite

  //
  // Utility
  //

  private[this] def widenMainPane(): Unit = mainContent.style.width = 100.pct

  private[this] def recenterMainPane(): Unit = {
    val s = sidebars.reverse.map(_.isCollapsed.fold("0", "1")).mkString("")
    if (s.nonEmpty) replaceClass(mainContent, "main-area-w-pc-", "main-area-w-pc-" + s)
  }

  override def handleUpdate(subject: SideBarLike): Unit = recenterMainPane()


  private[this] val svgAreas: mutable.ListBuffer[SVGArea] = mutable.ListBuffer.empty

  private[this] var controlBar: Option[ControlBar] = None

  private[this] val commentArea: CommentArea = CommentArea(isDisplayOnly = isMobile, isModal = false)

  private[this] val mainArea = div().render

  private[this] val mainContent = div(
    cls := "main-area-wrapper",
    mainArea
  ).render

  private[this] val sideBarRight: Option[SideBarRight] = (!isMobile).option(new SideBarRight {
    override def getMenuPane: MenuPane = getSite().menuPane
  })

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
  def renderSVGAreas(deviceType: DeviceType, numAreas: Int, pieceWidth: Option[Int], layout: SVGAreaLayout): Unit = {
    svgAreas.foreach(_.terminate())
    svgAreas.clear()
    svgAreas ++= (0 until numAreas).map(n => SVGArea(n, layout))

    val node = deviceType match {
      case DeviceType.MobileLandscape => createMobileLandscapeMain
      case DeviceType.MobilePortrait => createMobilePortraitMain
      case DeviceType.PC => createPCPortraitMain
    }

    WebComponent.removeAllChildElements(mainArea)
    mainArea.appendChild(node.render)
    resizeSVGAreas(deviceType, pieceWidth, layout)
  }

  private[this] def createPCPortraitMain: TypedTag[Div] = {
    controlBar.foreach(_.terminate())
    controlBar = Some(ControlBar(ControlBarType.Normal))

    div(cls := "container-fluid",
      div(
        id := "main-area",
        cls := "main-area-pc",
        if (svgAreas.size == 2) {
          div(cls := "row",
            div(cls := "col-xs-6", svgAreas.head.element),
            div(cls := "col-xs-6 main-second", svgAreas(1).element)
          )
        } else {
          div(svgAreas.head.element)
        }
      ),
      controlBar.get.element,
      commentArea.element
    )
  }

  private[this] def createMobilePortraitMain: TypedTag[Div] = {
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

  private[this] def createMobileLandscapeMain: TypedTag[Div] = {
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

  def resizeSVGAreas(deviceType: DeviceType, pieceWidth: Option[Int], layout: SVGAreaLayout): Unit = {
    dom.document.getElementById("main-area") match {
      case e: HTMLElement =>
        pieceWidth match {
          case Some(pw) =>
            val w = BasePlaygroundConfiguration.getSVGAreaSize(deviceType, pw, layout, svgAreas.size)
            e.style.maxWidth = w.px
            e.style.minWidth = BasePlaygroundConfiguration.getSVGAreaSize(deviceType, BasePlaygroundConfiguration.MIN_PIECE_WIDTH, layout, svgAreas.size).px
            e.style.width = w.px
          case None =>
            e.style.maxWidth = BasePlaygroundConfiguration.getMaxSVGAreaSize(deviceType, BasePlaygroundConfiguration.MAX_PIECE_WIDTH, layout, svgAreas.size).px
            e.style.minWidth = BasePlaygroundConfiguration.getSVGAreaSize(deviceType, BasePlaygroundConfiguration.MIN_PIECE_WIDTH, layout, svgAreas.size).px
            e.style.width = 100.pct
        }
      case _ =>
    }
  }

  def updateSVGArea(f: SVGArea => Unit): Unit = svgAreas.foreach(f)

  def updateSVGArea(areaId: Int, f: SVGArea => Unit): Unit = f(svgAreas(areaId))

  def getFirstSVGArea: SVGArea = svgAreas.head

  def updateControlBars(f: ControlBar => Unit): Unit = {
    controlBar.foreach(f)
    sideBarLeft.foreach(sb => f(sb.controlBar))
  }

  def updateComment(modeType: ModeType, comment: String): Unit = commentArea.refresh(modeType, comment)

  def updateBranchArea(gameControl: Option[GameControl], recordLang: Language, modeType: ModeType, newBranchMode: Boolean): Unit = {
    sideBarLeft.foreach(_.branchArea.refresh(gameControl, recordLang, modeType, newBranchMode))
  }

  def updateModeType(modeType: ModeType): Unit = {
    sideBarLeft.foreach(_.refresh(modeType))
  }

  def collapseSideBarRight(): Unit = sideBarRight.foreach(_.collapseSideBar())

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

  //
  // Observer
  //
  override val samObserveMask: Int = {
    import ObserveFlag._
    val modes = MODE_FROM_EDIT | MODE_TO_EDIT | GAME_BRANCH | GAME_INFO | GAME_POSITION | GAME_HANDICAP
    val confs = CONF_LAYOUT | CONF_NUM_AREAS | CONF_FLIP_TYPE | CONF_PIECE_WIDTH | CONF_PIECE_FACE | CONF_MSG_LANG | CONF_RCD_LANG
    val cursors = CURSOR_ACTIVE | CURSOR_SELECT | CURSOR_FLASH
    modes | confs | cursors
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    import ObserveFlag._

    lazy val gc = model.mode.getGameControl
    lazy val config = model.config
    val areaUpdated = (flag & (CONF_LAYOUT | CONF_NUM_AREAS)) != 0

    def check(mask: Int) = areaUpdated || (flag & mask) != 0

    // 1. Area
    if (areaUpdated) renderSVGAreas(config.deviceType, config.flipType.numAreas, config.pieceWidth, config.layout)

    // 2. Piece Width
    if (check(CONF_PIECE_WIDTH)) resizeSVGAreas(config.deviceType, config.pieceWidth, config.layout)

    // 3. Flip
    if (check(CONF_FLIP_TYPE)) {
      config.flipType match {
        case FlipDisabled => updateSVGArea(_.setFlip(false))
        case FlipEnabled => updateSVGArea(_.setFlip(true))
        case DoubleBoard => Seq(0, 1).foreach { n => updateSVGArea(n, _.setFlip(n == 1)) }
      }
    }

    // 4. Indexes
    if (check(CONF_FLIP_TYPE | CONF_RCD_LANG)) updateSVGArea(_.board.drawIndexes(config.recordLang == Japanese))

    // 5. Player Names
    if (check(GAME_INFO | CONF_MSG_LANG | GAME_HANDICAP)) {
      val names = PlayerUtil.getCompletePlayerNames(model.mode.getGameInfo, config.messageLang, model.mode.isHandicapped)
      updateSVGArea(_.player.drawNames(names))
    }
  }
}
