package com.mogproject.mogami.frontend

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}


case class PlaygroundArgumentsBuilderEmbed(gameControl: GameControl) extends PlaygroundArgumentsBuilderLike {
  override val config: PlaygroundConfiguration = PlaygroundConfiguration()


  def toEmbedCode(isSnapshot: Boolean,
                  pieceWidth: Int,
                  layout: SVGAreaLayout,
                  pieceFace: PieceFace,
                  boardIndexType: BoardIndexType,
                  isFlipped: Boolean,
                  visualEffectEnabled: Boolean,
                  soundEffectEnabled: Boolean,
                  messageLang: Option[Language],
                  recordLang: Option[Language]
                 ): String = {
    val params = isSnapshot.fold(snapshotParams, recordParams(createFullUrl = false)) ++ Seq(
      "embed" -> "true",
      "sz" -> pieceWidth.toString,
      "layout" -> (layout match {
        case SVGStandardLayout => "s"
        case SVGCompactLayout => "c"
        case SVGWideLayout => "w"
      }),
      "p" -> pieceFace.faceId,
      "bi" -> boardIndexType.id,
      "ve" -> visualEffectEnabled.toString,
      "se" -> soundEffectEnabled.toString
    ) ++ messageLang.map("mlang" -> _.toString) ++ recordLang.map("rlang" -> _.toString) ++ isFlipped.option("flip" -> "true")

    val url = createUrl(params)
    val w = layout.areaWidth(pieceWidth) + 38
    val h = layout.areaHeight(pieceWidth) + 170 + (layout == SVGStandardLayout).fold(10, 0)

    s"""<iframe src="${url}" width="${w}" height="${h}"></iframe>"""
  }
}
