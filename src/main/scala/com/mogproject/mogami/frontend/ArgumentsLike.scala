package com.mogproject.mogami.frontend

import com.mogproject.mogami.core.game.Game.GamePosition
import com.mogproject.mogami.frontend.model.ConfigurationLike

import scala.util.Try

trait ArgumentsLike[Config <: ConfigurationLike[Config], T <: ArgumentsLike[Config, T]] {
  self: T =>

  def config: Config

  protected def updateConfig(f: Config => Config): T

  /**
    * Loads data from Local Storage and returns ArgumentsLike with updated configuration.
    *
    * @return new ArgumentsLike instance
    */
  def loadLocalStorage(): T = updateConfig(_.loadLocalStorage())

  def parseQueryString(query: String): T

  //
  // Utilities
  //
  protected def parseGamePosition(s: String): Option[GamePosition] = {
    val pattern = raw"(?:([\d])+[.])?([\d]+)".r

    s match {
      case pattern(null, y) => for {yy <- Try(y.toInt).toOption} yield GamePosition(0, yy)
      case pattern(x, y) => for {xx <- Try(x.toInt).toOption; yy <- Try(y.toInt).toOption} yield GamePosition(xx, yy)
      case _ => None
    }
  }

  protected def parseBoolean(s: String): Option[Boolean] = {
    s.toLowerCase match {
      case "true" => Some(true)
      case "false" => Some(false)
      case _ => None
    }
  }

  protected def parseColor(s: String): Option[String] = {
    val pattern = """^[0-9A-Fa-f]{6}$""".r
    s match {
      case pattern() => Some("#" + s.toLowerCase())
      case _ => None
    }
  }
}
