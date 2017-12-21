package com.mogproject.mogami.frontend

import org.scalajs.dom

import scala.util.Try

/**
  * Manages browser's Local Storage
  */
case class LocalStorage(pieceWidth: Option[Int] = None,
                        doubleBoardMode: Option[Boolean] = None,
                        messageLang: Option[Language] = None,
                        recordLang: Option[Language] = None,
                        pieceFace: Option[PieceFace] = None,
                        visualEffect: Option[Boolean] = None,
                        soundEffect: Option[Boolean] = None)

object LocalStorage {
  def load(): LocalStorage = {
    val keys: Seq[(String, String => LocalStorage => LocalStorage)] = Seq(
      ("sz", s => ls => ls.copy(pieceWidth = Try(s.toInt).toOption)),
      ("double", s => ls => ls.copy(doubleBoardMode = parseBooleanString(s))),
      ("mlang", s => ls => ls.copy(messageLang = Language.parseString(s))),
      ("rlang", s => ls => ls.copy(recordLang = Language.parseString(s))),
      ("p", s => ls => ls.copy(pieceFace = PieceFace.parseString(s))),
      ("ve", s => ls => ls.copy(visualEffect = parseBooleanString(s))),
      ("se", s => ls => ls.copy(soundEffect = parseBooleanString(s)))
    )
    keys.foldLeft[LocalStorage](LocalStorage()) { case (ls, (k, f)) => f(dom.window.localStorage.getItem(k))(ls) }
  }

  def saveSize(size: Int): Unit = setItem("sz", size)

  def clearSize(): Unit = dom.window.localStorage.removeItem("sz")

  def saveDoubleBoardMode(enabled: Boolean): Unit = setItem("double", enabled)

  def saveMessageLang(lang: Language): Unit = setItem("mlang", lang)

  def saveRecordLang(lang: Language): Unit = setItem("rlang", lang)

  def savePieceFace(pieceFace: PieceFace): Unit = setItem("p", pieceFace)

  def saveVisualEffect(enabled: Boolean): Unit = setItem("ve", enabled)

  def saveSoundEffect(enabled: Boolean): Unit = setItem("se", enabled)

  private[this] def parseBooleanString(s: String): Option[Boolean] = s match {
    case "true" => Some(true)
    case "false" => Some(false)
    case _ => None
  }

  private[this] def setItem(key: String, item: Any): Unit = dom.window.localStorage.setItem(key, item.toString)
}
