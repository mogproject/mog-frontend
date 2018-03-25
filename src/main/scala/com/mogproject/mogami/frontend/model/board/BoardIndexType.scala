package com.mogproject.mogami.frontend.model.board

/**
  * Board Index
  */
sealed abstract class BoardIndexType(val id: String, val displayName: String)

case object BoardIndexJapanese extends BoardIndexType("ja", "Japanese")

case object BoardIndexEnglish extends BoardIndexType("en", "Alphabet")

case object BoardIndexNumber extends BoardIndexType("n", "Numbers")

object BoardIndexType {
  def parseString(s: String): Option[BoardIndexType] = all.find(_.id == s)

  val all = Seq(BoardIndexJapanese, BoardIndexEnglish, BoardIndexNumber)
}