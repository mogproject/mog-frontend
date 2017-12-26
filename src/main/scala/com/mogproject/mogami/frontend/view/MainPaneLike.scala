package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.core.Ptype
import com.mogproject.mogami.frontend.model.DeviceType.DeviceType
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.api.WebAudioAPISound
import com.mogproject.mogami.frontend.model.board.cursor.{BoardCursor, Cursor}
import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipDisabled, FlipEnabled}
import com.mogproject.mogami.frontend.util.PlayerUtil
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.board.{SVGArea, SVGAreaLayout}
import com.mogproject.mogami.frontend.view.control.{CommentArea, ControlBar, ControlBarType}
import com.mogproject.mogami.frontend.view.menu.MenuPane
import com.mogproject.mogami.frontend.view.modal.AlertDialog
import com.mogproject.mogami.frontend.view.sidebar.{SideBarLeft, SideBarLike, SideBarRight}
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLElement

import scala.collection.mutable
import scala.util.{Failure, Success, Try}
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
trait MainPaneLike extends WebComponent with Observer[SideBarLike] with SAMObserver[BasePlaygroundModel] {
  def isMobile: Boolean

  def getSite: () => PlaygroundSite

  lazy val imageCache: ImageCache = new ImageCache

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

  private[this] val controlBar: ControlBar = ControlBar(isMobile.fold(ControlBarType.Small, ControlBarType.Normal))

  private[this] val commentArea: CommentArea = CommentArea(isDisplayOnly = isMobile)

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

  private[this] val clickSound = {
    val sound = new WebAudioAPISound("assets/mp3/click")
    sound.setVolume(100)
    sound
  }

  override lazy val element: Element = div(
    if (isMobile) {
      mainContent
    } else {
      div(cls := "row no-margin no-overflow",
        sidebars.map(_.element),
        mainContent
      )
    }
  ).render

  /**
    * Initialize or reload main boards
    *
    * `controlSection` should be updated.
    *
    * @param layout area layout
    */
  private[this] def renderSVGAreas(deviceType: DeviceType, numAreas: Int, pieceWidth: Option[Int], layout: SVGAreaLayout): Unit = {
    svgAreas.foreach(_.terminate())
    svgAreas.clear()

    val maxNumAreas = (deviceType == DeviceType.MobilePortrait).fold(1, 2)
    svgAreas ++= (0 until math.min(maxNumAreas, numAreas)).map(n => SVGArea(n, layout))

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
      controlBar.element,
      commentArea.element
    )
  }

  private[this] def createMobilePortraitMain: TypedTag[Div] = {
    div(
      div(cls := "container-fluid",
        div(
          id := "main-area",
          cls := "main-area-mobile-portrait",
          svgAreas.head.element
        ),
        controlBar.element,
        commentArea.element
      )
    )
  }

  private[this] def createMobileLandscapeMain: TypedTag[Div] = {
    div(cls := "container-fluid",
      div(
        id := "main-area",
        div(cls := "row",
          div(cls := "col-xs-6", svgAreas.head.element),
          div(cls := "col-xs-6 main-second", (svgAreas.size > 1).fold(svgAreas(1).element, commentArea.element))
        ),
        controlBar.element
      )
    )
  }

  private[this] def resizeSVGAreas(deviceType: DeviceType, pieceWidth: Option[Int], layout: SVGAreaLayout): Unit = {
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

  private[this] def updateSVGArea(f: SVGArea => Unit): Unit = svgAreas.foreach(f)

  private[this] def updateSVGArea(areaId: Int, f: SVGArea => Unit): Unit = f(svgAreas(areaId))

  def flashCursor(cursor: Cursor): Unit = {
    updateSVGArea(_.flashCursor(cursor))
  }

  def playClickSound(): Unit = {
    Try {
      clickSound.play()
    } match {
      case Success(_) =>
      case Failure(e) => println(e)
    }
  }

  //
  // Image cache
  //
  def downloadImages(urls: Seq[String], callback: () => Unit): Unit = {
    imageCache.download(urls, callback, failedUrls => {
      AlertDialog(English, p("Failed to download image(s):", br(), pre(failedUrls.map(u => Seq(StringFrag(u), br()))))).show()
    })
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
    val modes = MODE_EDIT | GAME_BRANCH | GAME_INFO | GAME_POSITION | GAME_HANDICAP | GAME_INDICATOR | GAME_JUST_MOVED | GAME_NEXT_POS | GAME_PREV_POS
    val confs = CONF_DEVICE | CONF_LAYOUT | CONF_NUM_AREAS | CONF_FLIP_TYPE | CONF_PIECE_WIDTH | CONF_PIECE_FACE | CONF_MSG_LANG | CONF_RCD_LANG
    val cursors = CURSOR_ACTIVE | CURSOR_SELECT | CURSOR_FLASH
    modes | confs | cursors
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    import ObserveFlag._

    lazy val mode = model.mode
    lazy val config = model.config
    val areaUpdated = (flag & (CONF_DEVICE | CONF_LAYOUT | CONF_NUM_AREAS)) != 0

    def check(mask: Int) = areaUpdated || (flag & mask) != 0

    // 1. Area
    if (areaUpdated) renderSVGAreas(config.deviceType, config.flipType.numAreas, config.pieceWidth, config.layout)

    // 2. Piece Width
    if (check(CONF_PIECE_WIDTH)) resizeSVGAreas(config.deviceType, config.pieceWidth, config.layout)

    // 3. Flip
    if (check(CONF_FLIP_TYPE)) {
      (config.flipType, config.deviceType) match {
        case (FlipDisabled, _) => updateSVGArea(_.setFlip(false))
        case (FlipEnabled, _) => updateSVGArea(_.setFlip(true))
        case (DoubleBoard, DeviceType.MobilePortrait) => updateSVGArea(_.setFlip(false))
        case (DoubleBoard, _) => Seq(0, 1).foreach { n => updateSVGArea(n, _.setFlip(n == 1)) }
      }
    }

    // 4. Indexes
    if (check(CONF_RCD_LANG)) updateSVGArea(_.board.drawIndexes(config.recordLang == Japanese))

    // 5. Player Names
    if (check(GAME_INFO | CONF_MSG_LANG | GAME_HANDICAP)) {
      val names = PlayerUtil.getCompletePlayerNames(mode.getGameInfo, config.messageLang, mode.isHandicapped)
      updateSVGArea(_.player.drawNames(names))
    }

    // 6. Indicators
    if (check(GAME_INDICATOR)) updateSVGArea(_.player.drawIndicators(mode.getIndicators))

    // 7. Box
    if (check(MODE_EDIT)) updateSVGArea(mode.boxAvailable.fold(_.showBox(), _.hideBox()))

    // 8. Board/Hand/Box Pieces
    if (check(GAME_BRANCH | GAME_POSITION | CONF_PIECE_FACE)) {
      // use image cache
      downloadImages(Ptype.constructor.map(model.config.pieceFace.getImagePath), () =>
        updateSVGArea { area =>
          area.board.drawPieces(mode.getBoardPieces, config.pieceFace, keepLastMove = true)
          area.hand.drawPieces(mode.getHandPieces, config.pieceFace, keepLastMove = true)
          if (mode.isEditMode) area.box.drawPieces(mode.getBoxPieces, config.pieceFace)
        })
    }

    // 9. Last Move
    if (check(GAME_BRANCH | GAME_POSITION | MODE_EDIT | CONF_FLIP_TYPE)) updateSVGArea(_.drawLastMove(mode.getLastMove))

    // 10. Active Cursor
    if ((flag & CURSOR_ACTIVE) != 0) {
      // clear current active cursor
      updateSVGArea(_.clearActiveCursor())

      // draw new cursor
      model.activeCursor match {
        case Some((n, c)) => updateSVGArea(n, _.drawCursor(c))
        case None => // do nothing
      }
    }

    // 11. Selected Cursor
    if ((!mode.isViewMode || model.selectedCursor.isEmpty) && (flag & CURSOR_SELECT) != 0) {
      val legalMoves = for {
        (_, c) <- model.selectedCursor.toSet if config.visualEffectEnabled && !c.isBox
        from = c.moveFrom
        lm <- mode.getLegalMoves(from)
      } yield lm

      // clear current selected cursor
      updateSVGArea(_.unselect())

      // draw new cursor
      model.selectedCursor.map(_._2).foreach { c => updateSVGArea(_.select(c, config.visualEffectEnabled, legalMoves)) }
    }

    // 12. Flash Cursor
    if (flag != -1 && isFlagUpdated(flag, CURSOR_FLASH)) model.flashedCursor.foreach { c => updateSVGArea(_.flashCursor(c)) }


    // 13. Move Effect
    if (flag != -1 && isFlagUpdated(flag, GAME_JUST_MOVED)) {
      mode.getLastMove.foreach { move =>
        if (!model.flashedCursor.flatMap(_.board).contains(move.to)) updateSVGArea(_.flashCursor(BoardCursor(move.to)))

        if (config.soundEffectEnabled) playClickSound()
        if (config.visualEffectEnabled) {
          //          updateSVGArea(a => a.board.effect.moveEffector.start(a.board.getRect(move.to)))
          if (move.promote) updateSVGArea(_.board.startPromotionEffect(move.to, move.oldPiece, config.pieceFace))
        }
      }
    }

    // 14. Move Forward/Backward Effect
    if (flag != -1 && isFlagUpdated(flag, GAME_NEXT_POS | GAME_PREV_POS) && model.selectedCursor.isDefined) updateSVGArea(_.board.effect.forwardEffector.start((flag & GAME_NEXT_POS) != 0))
  }
}
