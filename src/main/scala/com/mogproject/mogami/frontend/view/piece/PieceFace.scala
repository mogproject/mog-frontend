package com.mogproject.mogami.frontend.view.piece

import com.mogproject.mogami.Ptype


/**
  *
  */
sealed abstract class PieceFace(faceId: String) {
  val basePath: String = "assets/img/p/"

  def getImagePath(ptype: Ptype): String = s"${basePath}${faceId}/${ptype.toCsaString}.svg"
}

case object JapaneseOneCharFace extends PieceFace("jp1")