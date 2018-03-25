package com.mogproject.mogami.frontend.model.board

/**
  * Board Index
  */
sealed abstract class BoardIndexType(val id: String)

case object BoardIndexJapanese extends BoardIndexType("ja")

case object BoardIndexEnglish extends BoardIndexType("en")

case object BoardIndexNumber extends BoardIndexType("n")

object BoardIndexType {
  def parseString(s: String): Option[BoardIndexType] = all.find(_.id == s)

  val all = Seq(BoardIndexJapanese, BoardIndexEnglish, BoardIndexNumber)
}