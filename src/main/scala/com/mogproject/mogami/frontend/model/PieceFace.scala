package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.Ptype


/**
  *
  */
sealed abstract class PieceFace(val faceId: String, val displayName: String) {
  val basePath: String = "assets/img/p/"

  def getImagePath(ptype: Ptype): String = s"${basePath}${faceId}/${ptype.toCsaString}.svg"
}

case object JapaneseOneCharFace extends PieceFace("jp1", "Japanese 1")

case object JapaneseOneCharGraphicalFace extends PieceFace("jp2", "Japanese 2")

case object JapaneseTwoCharGraphicalFace extends PieceFace("jp3", "Japanese 3")

case object WesternOneCharFace extends PieceFace("en1", "Western 1")

object PieceFace {
  def parseString(s: String): Option[PieceFace] = all.find(_.faceId == s)

  val all = Seq(JapaneseOneCharFace, JapaneseOneCharGraphicalFace, JapaneseTwoCharGraphicalFace, WesternOneCharFace)
}