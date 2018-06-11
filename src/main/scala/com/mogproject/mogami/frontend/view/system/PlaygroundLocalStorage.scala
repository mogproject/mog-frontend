package com.mogproject.mogami.frontend.view.system

import com.mogproject.mogami.frontend.api.LZString
import com.mogproject.mogami.frontend.model.board.BoardIndexType
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}
import com.mogproject.mogami.frontend.{Language, PieceFace}
import org.scalajs.dom

import scala.util.Try

/**
  * Manages browser's Local Storage
  */
case class PlaygroundLocalStorage(pieceWidth: Option[Option[Int]] = None,
                                  layout: Option[SVGAreaLayout] = None,
                                  pieceFace: Option[PieceFace] = None,
                                  colorBackground: Option[String] = None,
                                  colorCursor: Option[String] = None,
                                  colorLastMove: Option[String] = None,
                                  boardIndexType: Option[BoardIndexType] = None,
                                  doubleBoardMode: Option[Boolean] = None,
                                  visualEffect: Option[Boolean] = None,
                                  soundEffect: Option[Boolean] = None,
                                  messageLang: Option[Language] = None,
                                  recordLang: Option[Language] = None) extends LocalStorageLike {

  private def toLayoutString: Option[String] = layout map {
    case SVGStandardLayout => "s"
    case SVGCompactLayout => "c"
    case SVGWideLayout => "w"
  }

  def save(): Unit = PlaygroundLocalStorage.save(this)
}

object PlaygroundLocalStorage extends LocalStorageIOLike[PlaygroundLocalStorage] {
  override def newData(): PlaygroundLocalStorage = new PlaygroundLocalStorage()

  private[this] val E = LocalStorageEntity[PlaygroundLocalStorage] _

  //
  // Entities
  //
  override val entities: Seq[LocalStorageEntity[PlaygroundLocalStorage]] = Seq(
    E("sz", s => _.copy(pieceWidth = Some(Try(s.toInt).toOption)), _.pieceWidth.map(_.getOrElse(""))),
    E("layout", s => _.copy(layout = parseLayout(s)), _.toLayoutString),
    E("p", s => _.copy(pieceFace = PieceFace.parseString(s)), _.pieceFace.map(_.faceId)),
    E("colorbg", s => _.copy(colorBackground = parseColor(s)), _.colorBackground),
    E("colorcs", s => _.copy(colorCursor = parseColor(s)), _.colorCursor),
    E("colorlm", s => _.copy(colorLastMove = parseColor(s)), _.colorLastMove),
    E("bi", s => _.copy(boardIndexType = BoardIndexType.parseString(s)), _.boardIndexType.map(_.id)),
    E("double", s => _.copy(doubleBoardMode = parseBooleanString(s)), _.doubleBoardMode),
    E("ve", s => _.copy(visualEffect = parseBooleanString(s)), _.visualEffect),
    E("se", s => _.copy(soundEffect = parseBooleanString(s)), _.soundEffect),
    E("mlang", s => _.copy(messageLang = Language.parseString(s)), _.messageLang),
    E("rlang", s => _.copy(recordLang = Language.parseString(s)), _.recordLang)
  )

  private[this] def parseLayout(s: String): Option[SVGAreaLayout] = s match {
    case "s" => Some(SVGStandardLayout)
    case "c" => Some(SVGCompactLayout)
    case "w" => Some(SVGWideLayout)
    case _ => None
  }

  /**
    * Load image from LocalStorage
    *
    * @param url          URL to the image
    * @param imageVersion image version
    * @return encoded image data
    */
  def loadImage(url: String, imageVersion: Int): Option[String] = {
    val key = "i:" + url
    val ret = for {
      compressed <- Try(dom.window.localStorage.getItem(key)).toOption
      data = LZString.decompressFromUTF16(compressed)
      tokens = data.split(";", 2) if tokens.size == 2
      v <- Try(tokens.head.toInt).toOption if imageVersion == v // check version
    } yield {
      tokens(1)
    }

    if (ret.isEmpty) {
      try {
        dom.window.localStorage.removeItem(key)
      } catch {
        case _: RuntimeException => // Failed to remove item.
      }
    }
    ret
  }

  def saveImage(url: String, imageVersion: Int, data: String): Unit = {
    val s = imageVersion + ";" + data
    val compressed = LZString.compressToUTF16(s)
    dom.window.localStorage.setItem("i:" + url, compressed)
  }
}
