package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.core.{Player, Ptype}
import com.mogproject.mogami.frontend.model.DeviceType.DeviceType
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.api.WebAudioAPISound
import com.mogproject.mogami.frontend.model.{EditMode, PlayMode}
import com.mogproject.mogami.frontend.model.board.cursor.{BoardCursor, Cursor, PlayerCursor}
import com.mogproject.mogami.frontend.model.board.{BoardIndexJapanese, DoubleBoard, FlipDisabled, FlipEnabled}
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

  def embeddedMode: Boolean

  def getSite: () => PlaygroundSiteLike

  lazy implicit val imageCache: SVGImageCache = new SVGImageCache

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

  val controlBar: ControlBar = ControlBar((isMobile || embeddedMode).fold(ControlBarType.Small, ControlBarType.Normal))

  val commentArea: CommentArea = CommentArea(isDisplayOnly = isMobile || embeddedMode)

  private[this] val mainArea = div().render

  private[this] val mainContent = div(
    cls := "main-area-wrapper",
    mainArea
  ).render

  val sideBarRight: Option[SideBarRight] = (!isMobile && !embeddedMode).option(new SideBarRight {
    override def getMenuPane: MenuPane = getSite().menuPane
  })

  val sideBarLeft: Option[SideBarLeft] = (!isMobile && !embeddedMode).option(new SideBarLeft)

  private[this] val sidebars: Seq[SideBarLike] = sideBarRight.toSeq ++ sideBarLeft

  override lazy val element: Element = div(
    if (isMobile || embeddedMode) {
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

    val maxNumAreas = (deviceType == DeviceType.MobilePortrait || embeddedMode).fold(1, 2)
    svgAreas ++= (0 until math.min(maxNumAreas, numAreas)).map(n => SVGArea(n, layout))

    val node = (embeddedMode, deviceType) match {
      case (true, _) => createMobilePortraitMain
      case (false, DeviceType.MobilePortrait) => createMobilePortraitMain
      case (false, DeviceType.MobileLandscape) => createMobileLandscapeMain
      case (false, DeviceType.PC) => createPCPortraitMain
    }

    WebComponent.replaceChildElement(mainArea, node.render)
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

  private[this] def loadSound(name: String): Option[WebAudioAPISound] = BrowserInfo.isSoundSupported.option {
    val sound = new WebAudioAPISound(s"assets/mp3/${name}")
    sound.setVolume(100)
    sound
  }

  private[this] lazy val clickSound: Option[WebAudioAPISound] = loadSound("click")
  private[this] lazy val selectSound: Option[WebAudioAPISound] = loadSound("select")
  private[this] lazy val cancelSound: Option[WebAudioAPISound] = loadSound("cancel")
  private[this] lazy val switchSound: Option[WebAudioAPISound] = loadSound("switch")
  private[this] lazy val successSound: Option[WebAudioAPISound] = loadSound("success")
  private[this] lazy val beepSound: Option[WebAudioAPISound] = loadSound("beep")

  def prepareSound(): Unit = {
    clickSound
    selectSound
    cancelSound
    switchSound
    successSound
    beepSound
  }

  def playSound(sound: Option[WebAudioAPISound], retry: Boolean = true): Unit = {
    Try {
      sound.foreach { s =>
        val ret = s.play()
        if (!ret && retry) dom.window.setTimeout(() => playSound(Some(s), retry = false), 300)
      }
    } match {
      case Success(_) =>
      case Failure(e) => println(e)
    }
  }

  def copyAllMoves(text: String): Unit = sideBarLeft.foreach(_.copyAllMoves(text))

  //
  // Image cache
  //
  def downloadImages(urls: Seq[String], callback: () => Unit): Unit = {
    imageCache.download(urls, callback, failedUrls => {
      AlertDialog(p(Messages.get.IMAGE_DOWNLOAD_FAILURE, br(), pre(failedUrls.map(u => Seq(StringFrag(u), br()))))).show()
    })
  }

  def initialize(): Unit = {
    sidebars.foreach(_.addObserver(this))
    if (isMobile || embeddedMode) widenMainPane() else recenterMainPane()
  }

  initialize()

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.MODE_ALL | ObserveFlag.CONF_ALL | ObserveFlag.CURSOR_ALL

  override def refresh(model: BasePlaygroundModel, flag: Long): Unit = {
    refreshPhase1(model, flag)
  }

  private[this] def refreshPhase1(model: BasePlaygroundModel, flag: Long): Unit = {
    import ObserveFlag._

    lazy val config = model.config
    val areaUpdated = (flag & (CONF_DEVICE | CONF_LAYOUT | CONF_NUM_AREAS)) != 0

    def check(mask: Long) = areaUpdated || (flag & mask) != 0

    // 0. Load Sound
    if (isFlagUpdated(flag, CONF_SOUND) && config.soundEffectEnabled) prepareSound()

    if (flag != -1L && isFlagUpdated(flag, CONF_SOUND) && config.soundEffectEnabled) playSound(successSound)

    // 0. Mode Change
    if (flag != -1L && isFlagUpdated(flag, MODE_TYPE) && config.soundEffectEnabled) playSound(switchSound)

    // 1. Area
    if (areaUpdated) renderSVGAreas(config.deviceType, config.flipType.numAreas, config.pieceWidth, config.layout)

    // 2. Piece Width
    if (check(CONF_PIECE_WIDTH)) resizeSVGAreas(config.deviceType, config.pieceWidth, config.layout)

    // 2. Board Colors
    if (check(CONF_COLOR_BACKGROUND)) updateSVGArea(_.drawBackgroundColor(config.colorBackground))

    // 3. Flip
    if (check(CONF_FLIP_TYPE)) {
      downloadImages(Player.constructor.map(p => config.layout.player.getSymbolImagePath(p)), () => {
        (config.flipType, config.deviceType, config.embeddedMode) match {
          case (FlipDisabled, _, _) => updateSVGArea(_.setFlip(false))
          case (FlipEnabled, _, _) => updateSVGArea(_.setFlip(true))
          case (DoubleBoard, DeviceType.MobilePortrait, _) => updateSVGArea(_.setFlip(false))
          case (DoubleBoard, _, true) => updateSVGArea(_.setFlip(false))
          case (DoubleBoard, _, false) => Seq(0, 1).foreach { n => updateSVGArea(n, _.setFlip(n == 1)) }
        }

        // do not play SE when initializing, changing board layout, or changing double board setting
        if (!isFlagUpdated(flag, CONF_LAYOUT) && !isFlagUpdated(flag, CONF_NUM_AREAS) && config.soundEffectEnabled) playSound(switchSound)

        refreshPhase2(model, flag, areaUpdated)
      })
    } else {
      refreshPhase2(model, flag, areaUpdated)
    }
  }

  private[this] def refreshPhase2(model: BasePlaygroundModel, flag: Long, areaUpdated: Boolean): Unit = {
    import ObserveFlag._

    lazy val mode = model.mode
    lazy val config = model.config

    def check(mask: Long) = areaUpdated || (flag & mask) != 0

    // 4. Indexes
    if (check(CONF_IDX_TYPE)) {
      config.boardIndexType match {
        case BoardIndexJapanese =>
          downloadImages((1 to 9).map(n => config.layout.board.getJapaneseRankIndexImagePath(n)), () =>
            updateSVGArea(_.board.drawIndexes(BoardIndexJapanese))
          )
        case x =>
          updateSVGArea(_.board.drawIndexes(x))
      }
    }

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
    if (check(GAME_BRANCH | GAME_POSITION | CONF_PIECE_FACE | MODE_EDIT)) {
      // use image cache
      downloadImages(config.pieceFace.allImagePaths, () => {
        updateSVGArea { area =>
          area.board.drawPieces(mode.getBoardPieces, config.pieceFace, keepLastMove = true)
          area.hand.drawPieces(mode.getHandPieces, config.pieceFace, keepLastMove = true)
          if (mode.isEditMode) area.box.drawPieces(mode.getBoxPieces, config.pieceFace)
        }
        refreshPhase3(model, flag, areaUpdated)
      })
    } else {
      refreshPhase3(model, flag, areaUpdated)
    }
  }


  private[this] def refreshPhase3(model: BasePlaygroundModel, flag: Long, areaUpdated: Boolean): Unit = {
    import ObserveFlag._

    lazy val mode = model.mode
    lazy val config = model.config

    def check(mask: Long) = areaUpdated || (flag & mask) != 0

    // 9. Last Move
    if (check(GAME_BRANCH | GAME_POSITION | MODE_EDIT | CONF_FLIP_TYPE | CONF_COLOR_LAST_MOVE)) updateSVGArea(_.drawLastMove(mode.getLastMove, config.colorLastMove))

    // 10. Active Cursor
    if ((flag & CURSOR_ACTIVE) != 0) {
      // clear current active cursor
      updateSVGArea(_.clearActiveCursor())

      // draw new cursor
      model.activeCursor match {
        case Some((n, c)) => updateSVGArea(n, _.drawCursor(c, config.colorCursor))
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
      model.selectedCursor.map(_._2).foreach { c => updateSVGArea(_.select(c, config.visualEffectEnabled, legalMoves, config.colorCursor)) }
    }

    // 12. Flash Cursor
    if (flag != -1 && isFlagUpdated(flag, CURSOR_FLASH)) {
      model.flashedCursor.foreach { c => updateSVGArea(_.flashCursor(c)) }
    }

    // 13. Move Sound Effect
    if (flag != 1L && config.soundEffectEnabled) {
      if (model.flashedCursor.exists(_.isPlayer)) {
        playSound(switchSound)
      } else if (isFlagUpdated(flag, GAME_BRANCH) || isFlagUpdated(flag, GAME_JUST_MOVED)) {
        mode match {
          case (PlayMode(_)) =>
          case (EditMode(_, _, _, _)) => playSound(clickSound)
          case _ =>
        }
      } else {
        if (mode.modeType == PlayModeType || mode.modeType == EditModeType) {
          if (isFlagUpdated(flag, PROMOTION_DIALOG)) {
            playSound(switchSound)
          } else if (isFlagUpdated(flag, CURSOR_SELECT) && !isFlagUpdated(flag, MODE_TYPE)) {
            // Cursor selected or canceled
            playSound(model.selectedCursor.isDefined.fold(selectSound, cancelSound))
          } else if (isFlagUpdated(flag, CURSOR_FLASH)) {
            playSound(beepSound)
          }
        }
      }
    }

    // 13. Move Effect
    if (flag != -1 && isFlagUpdated(flag, GAME_JUST_MOVED)) {
      mode.getLastMove.foreach { move =>
        if (!model.flashedCursor.flatMap(_.board).contains(move.to)) updateSVGArea(_.flashCursor(BoardCursor(move.to)))

        if (config.soundEffectEnabled) playSound(clickSound)
        if (config.visualEffectEnabled) {
          //          updateSVGArea(a => a.board.effect.moveEffector.start(a.board.getRect(move.to)))
          if (move.promote) updateSVGArea(_.board.startPromotionEffect(move.to, move.oldPiece, config.pieceFace))
        }
      }
    }

    // 14. Move Forward/Backward Effect
    if (flag != -1 && isFlagUpdated(flag, GAME_NEXT_POS | GAME_PREV_POS) && model.selectedCursor.isDefined) {
      updateSVGArea(_.board.effect.forwardEffector.start((flag & GAME_NEXT_POS) != 0))
    }
  }
}
