package com.mogproject.mogami.frontend.view.system

import org.scalajs.dom

/**
  * Manages I/O for LocalStorage
  */
trait LocalStorageIOLike[A <: LocalStorageLike] {
  /**
    * Creates a new LocalStorageLike instance.
    *
    * @return LocalStorageLike
    */
  def newData(): A

  /**
    * List of data keys and conversion rules.
    *
    * @return list of entities
    */
  def entities: Seq[LocalStorageEntity[A]]

  /**
    * Saves all data
    *
    * @param data LocalStorageLike
    */
  def save(data: A): Unit = {
    entities.foreach { ent =>
      ent.writer(data) match {
        case None => // do nothing
        case Some("") => clearItem(ent.key)
        case Some(s) => setItem(ent.key, s)
      }
    }
  }

  /**
    * Loads all data
    *
    * @return LocalStorageLike
    */
  def load(): A = {
    entities.foldLeft[A](newData()) { case (ls, ent) => ent.reader(dom.window.localStorage.getItem(ent.key))(ls) }
  }

  //
  // LocalStorage access
  //
  private[this] def setItem(key: String, item: Any): Unit = try {
    dom.window.localStorage.setItem(key, item.toString)
  } catch {
    // possibly not supported or access not allowed
    case _: RuntimeException => // do nothing
  }

  private[this] def clearItem(key: String): Unit = dom.window.localStorage.removeItem(key)

  //
  // Utilities
  //
  protected def parseBooleanString(s: String): Option[Boolean] = s match {
    case "true" => Some(true)
    case "false" => Some(false)
    case _ => None
  }

  protected def parseColor(s: String): Option[String] = {
    val pattern = """^[0-9A-Fa-f]{6}$""".r
    s match {
      case pattern() => Some("#" + s.toLowerCase())
      case _ => None
    }
  }

}
