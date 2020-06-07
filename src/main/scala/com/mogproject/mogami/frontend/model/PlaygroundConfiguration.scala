package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.FrontendSettings
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.DeviceType.DeviceType
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGStandardLayout}
import com.mogproject.mogami.frontend.view.sidebar.{SideBarLeft, SideBarRight}
import com.mogproject.mogami.frontend.view.system.{BrowserInfo, PlaygroundLocalStorage}
import org.scalajs.dom

import scala.scalajs.js.UndefOr

/**
  * Base configuration for Playground framework
  */
case class PlaygroundConfiguration(layout: SVGAreaLayout = SVGStandardLayout,
                                   pieceWidth: Option[Int] = None,
                                   flipType: FlipType = FlipDisabled,
                                   pieceFace: PieceFace = PieceFace.JapaneseOneCharFace,
                                   colorBackground: String = FrontendSettings.color.defaultBackground,
                                   colorCursor: String = FrontendSettings.color.defaultCursor,
                                   colorLastMove: String = FrontendSettings.color.defaultLastMove,
                                   boardIndexType: BoardIndexType = PlaygroundConfiguration.defaultBoardIndexType,
                                   newBranchMode: Boolean = false,
                                   messageLang: Language = PlaygroundConfiguration.browserLanguage,
                                   recordLang: Language = PlaygroundConfiguration.browserLanguage,
                                   visualEffectEnabled: Boolean = true,
                                   soundEffectEnabled: Boolean = false,
                                   baseUrl: String = PlaygroundConfiguration.defaultBaseUrl,
                                   deviceType: DeviceType = PlaygroundConfiguration.defaultDeviceType,
                                   freeMode: Boolean = false, // the turn never changes when this parameter is true
                                   embeddedMode: Boolean = false,
                                   isDev: Boolean = false,
                                   isDebug: Boolean = false
                                  ) extends ConfigurationLike[PlaygroundConfiguration] {

  def isAreaFlipped(areaId: Int): Boolean = flipType match {
    case FlipDisabled => false
    case FlipEnabled => true
    case DoubleBoard => areaId == 1
  }

  def toQueryParameters: List[String] = {
    type Parser = List[String] => List[String]

    val parseFlip: Parser = xs => flipType match {
      case FlipEnabled => "flip=true" :: xs
      case _ => xs
    }

    parseFlip(List.empty)
  }

  def updateScreenOrientation(): PlaygroundConfiguration = {
    this.copy(deviceType = DeviceType(deviceType.isMobile, BrowserInfo.isLandscape))
  }

  def collapseByDefault: Boolean = {
    val pw = pieceWidth.getOrElse(PlaygroundConfiguration.MIN_PIECE_WIDTH)
    val aw = PlaygroundConfiguration.getSVGAreaSize(deviceType, pw, layout, flipType.numAreas)
    !deviceType.isMobile && BrowserInfo.getClientWidth < aw + SideBarLeft.EXPANDED_WIDTH + SideBarRight.EXPANDED_WIDTH
  }

  override def loadLocalStorage(): PlaygroundConfiguration = {
    val ls = PlaygroundLocalStorage.load()

    this.copy(
      pieceWidth = ls.pieceWidth.getOrElse(pieceWidth),
      layout = ls.layout.getOrElse(layout),
      pieceFace = ls.pieceFace.getOrElse(pieceFace),
      colorBackground = ls.colorBackground.getOrElse(colorBackground),
      colorCursor = ls.colorCursor.getOrElse(colorCursor),
      colorLastMove = ls.colorLastMove.getOrElse(colorLastMove),
      flipType = ls.doubleBoardMode.contains(true).fold(DoubleBoard, flipType),
      boardIndexType = ls.boardIndexType.getOrElse(boardIndexType),
      visualEffectEnabled = ls.visualEffect.getOrElse(visualEffectEnabled),
      soundEffectEnabled = ls.soundEffect.getOrElse(soundEffectEnabled),
      messageLang = ls.messageLang.getOrElse(messageLang),
      recordLang = ls.recordLang.getOrElse(recordLang)
    )
  }

}

object PlaygroundConfiguration {

  /** used for limiting automatic board scaling */
  final val MIN_PIECE_WIDTH: Int = 15
  final val MAX_PIECE_WIDTH: Int = 40

  lazy val browserLanguage: Language = {
    def f(n: UndefOr[String]): Option[String] = n.toOption.flatMap(Option.apply)

    /* (nodejs compatible)
    n.toOption.flatMap(Option.apply)

    val nav = dom.window.navigator.asInstanceOf[UndefOr[com.mogproject.mogami.frontend.api.Navigator]].toOption
    val lang: Option[String] = nav.flatMap(n => (n.languages.toOption.flatMap(_.headOption) ++
      f(n.language) ++ f(n.userLanguage) ++ f(n.browserLanguage)).headOption)
     */
    val nav = dom.window.navigator.asInstanceOf[com.mogproject.mogami.frontend.api.Navigator]
    val firstLang = nav.languages.toOption.flatMap(_.headOption)
    val lang: Option[String] = (firstLang ++ f(nav.language) ++ f(nav.userLanguage) ++ f(nav.browserLanguage)).headOption

    lang.map(_.slice(0, 2).toLowerCase) match {
      case Some("ja") => Japanese
      case _ => English
    }
  }

  /* (nodejs compatible)
  import org.scalajs.dom.raw.Location
  lazy val defaultBaseUrl = dom.window.location.asInstanceOf[UndefOr[Location]].map(loc => s"${loc.protocol}//${loc.host}${loc.pathname}").getOrElse("")
  */
  lazy val defaultBaseUrl = s"${dom.window.location.protocol}//${dom.window.location.host}${dom.window.location.pathname}"

  lazy val defaultDeviceType: DeviceType = DeviceType(BrowserInfo.isMobile, BrowserInfo.isLandscape)

  lazy val defaultBoardIndexType: BoardIndexType = (browserLanguage == Japanese).fold(BoardIndexJapanese, BoardIndexEnglish)

  def getSVGAreaSize(deviceType: DeviceType, pieceWidth: Int, layout: SVGAreaLayout, numAreas: Int): Int = {
    val areaWidth = layout.areaWidth(pieceWidth)
    deviceType match {
      case DeviceType.PC => (areaWidth + 70) * numAreas - 50 // +20 for 1 board, +90 for 2 boards
      case DeviceType.MobilePortrait => areaWidth
      case DeviceType.MobileLandscape => areaWidth * 2 + 60
    }
  }

  def getMaxSVGAreaSize(deviceType: DeviceType, pieceWidth: Int, layout: SVGAreaLayout, numAreas: Int): Int = {
    val aw = getSVGAreaSize(deviceType, pieceWidth, layout, numAreas)

    if (deviceType.isLandscape) {
      val effectiveHeight = math.min(BrowserInfo.getClientHeight, BrowserInfo.getClientWidth) - 76
      val w = effectiveHeight * layout.viewBoxBottomRight.x / layout.viewBoxBottomRight.y

      math.min(aw, (w * 2).toInt)
    } else if (deviceType.isMobile) {
      val w = BrowserInfo.getClientWidth
      val h = BrowserInfo.getClientHeight
      val effectiveWidth = math.min(w, h) - 10
      val effectiveHeight = math.max(w, h) - 60 - 100 // menu/comment margin: 100px
      val ww = effectiveHeight * layout.viewBoxBottomRight.x / layout.viewBoxBottomRight.y

      math.min(aw, math.min(effectiveWidth, ww).toInt)
    } else {
      aw
    }
  }
}