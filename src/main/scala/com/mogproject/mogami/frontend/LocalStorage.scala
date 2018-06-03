package com.mogproject.mogami.frontend

import com.mogproject.mogami.frontend.api.LZString
import com.mogproject.mogami.frontend.model.board.BoardIndexType
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}
import org.scalajs.dom

import scala.util.Try

/**
  * Manages browser's Local Storage
  */
case class LocalStorage(pieceWidth: Option[Option[Int]] = None,
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
                        recordLang: Option[Language] = None) {
  def save(): Unit = {
    pieceWidth.foreach {
      case Some(n) => setItem("sz", n)
      case None => clearItem("sz")
    }
    layout match {
      case Some(SVGStandardLayout) => setItem("layout", "s")
      case Some(SVGCompactLayout) => setItem("layout", "c")
      case Some(SVGWideLayout) => setItem("layout", "w")
      case None => // do nothing
    }
    pieceFace.foreach(x => setItem("p", x.faceId))
    colorBackground.foreach(x => setItem("colorbg", x))
    colorCursor.foreach(x => setItem("colorcs", x))
    colorLastMove.foreach(x => setItem("colorlm", x))
    boardIndexType.foreach(x => setItem("bi", x.id))
    doubleBoardMode.foreach(x => setItem("double", x))
    visualEffect.foreach(x => setItem("ve", x))
    soundEffect.foreach(x => setItem("se", x))
    messageLang.foreach(x => setItem("mlang", x))
    recordLang.foreach(x => setItem("rlang", x))
  }

  private[this] def setItem(key: String, item: Any): Unit = try {
    dom.window.localStorage.setItem(key, item.toString)
  } catch {
    case _: RuntimeException => // do nothing
  }

  private[this] def clearItem(key: String): Unit = dom.window.localStorage.removeItem(key)
}

object LocalStorage {
  def load(): LocalStorage = {
    val keys: Seq[(String, String => LocalStorage => LocalStorage)] = Seq(
      ("sz", s => ls => ls.copy(pieceWidth = Some(Try(s.toInt).toOption))),
      ("layout", s => ls => ls.copy(layout = parseLayout(s))),
      ("p", s => ls => ls.copy(pieceFace = PieceFace.parseString(s))),
      ("colorbg", s => ls => ls.copy(colorBackground = parseColor(s))),
      ("colorcs", s => ls => ls.copy(colorCursor = parseColor(s))),
      ("colorlm", s => ls => ls.copy(colorLastMove = parseColor(s))),
      ("bi", s => ls => ls.copy(boardIndexType = BoardIndexType.parseString(s))),
      ("double", s => ls => ls.copy(doubleBoardMode = parseBooleanString(s))),
      ("ve", s => ls => ls.copy(visualEffect = parseBooleanString(s))),
      ("se", s => ls => ls.copy(soundEffect = parseBooleanString(s))),
      ("mlang", s => ls => ls.copy(messageLang = Language.parseString(s))),
      ("rlang", s => ls => ls.copy(recordLang = Language.parseString(s)))
    )
    keys.foldLeft[LocalStorage](LocalStorage()) { case (ls, (k, f)) => f(dom.window.localStorage.getItem(k))(ls) }
  }

  private[this] def parseBooleanString(s: String): Option[Boolean] = s match {
    case "true" => Some(true)
    case "false" => Some(false)
    case _ => None
  }

  private[this] def parseLayout(s: String): Option[SVGAreaLayout] = s match {
    case "s" => Some(SVGStandardLayout)
    case "c" => Some(SVGCompactLayout)
    case "w" => Some(SVGWideLayout)
    case _ => None
  }

  private[this] def parseColor(s: String): Option[String] = {
    val pattern = """^[0-9A-Fa-f]{6}$""".r
    s match {
      case pattern() => Some("#" + s.toLowerCase())
      case _ => None
    }
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
