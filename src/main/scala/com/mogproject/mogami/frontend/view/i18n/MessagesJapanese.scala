package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.model.io.RecordFormat
import com.mogproject.mogami.{BranchNo, State}
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.model.board.{BoardIndexEnglish, BoardIndexJapanese, BoardIndexNumber, BoardIndexType}
import com.mogproject.mogami.frontend.{FrontendSettings, PieceFace}
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}
import com.mogproject.mogami.frontend.view.share.ImageSize
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.LI

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Japanese message definitions
  */
case object MessagesJapanese extends Messages {

  override val FLIP: String = "反転"

  override val COMMENT: String = "コメント"
  override val COMMENT_CLEAR: String = "削除"
  override val COMMENT_CLEARED: String = "削除しました"
  override val COMMENT_CLEAR_TOOLTIP: String = "このコメントを削除"
  override val COMMENT_UPDATE: String = "更新"
  override val COMMENT_UPDATED: String = "更新しました"
  override val COMMENT_UPDATE_TOOLTIP: String = "このコメントを更新"

  override val MENU: String = "メニュー"
  override val PLAYGROUND_LINK_TOOLTIP: String = "Shogi Playground を開く"

  override val EDIT: String = "編集モード"
  override val ATTRIBUTES: String = "駒の属性"
  override val SELECT_PIECE_ON_BOARD: String = "盤上の駒を選択してください。"
  override val EDIT_HELP: Seq[TypedTag[LI]] = Seq(
    li("対局者名をクリックすると手番を設定できます。"),
    li("盤上の駒をダブルクリックすると、駒の属性を変更できます。駒は以下の順番で変化します。",
      ul(
        li("先手 不成駒 ->"),
        li("先手 成駒 ->"),
        li("後手 不成駒->"),
        li("後手 成駒 ->"),
        li("先手 不成駒")
      )
    )
  )

  override val RECORD: String = "棋譜"
  override val SNAPSHOT: String = "局面"

  override val SHARE: String = "シェア"
  override val COPY: String = "コピー"
  override val RECORD_URL: String = "棋譜 URL"
  override val SNAPSHOT_URL: String = "局面 URL"
  override val SHORTEN_URL: String = "短縮 URL"
  override val SHORTEN_URL_TOOLTIP: String = "kifu.co を利用して短縮 URL を生成"
  override val SHORTEN_URL_CREATING: String = "作成中"
  override val SNAPSHOT_IMAGE: String = "局面画像生成"
  override val IMAGE_SIZE: String = "画像サイズ"
  override val SMALL: String = "小"
  override val MEDIUM: String = "中"
  override val LARGE: String = "大"
  override val VIEW: String = "開く"
  override val SNAPSHOT_SFEN_STRING: String = "局面 SFEN 文字列"
  override val NOTES_VIEW: String = "Notes ビュー"
  override val COPY_SUCCESS: String = "コピーしました"
  override val COPY_FAILURE: String = "エラー"

  override val WARNING: String = "注意"
  override val SHARE_WARNING: String = "URL 文字数制限のため、この棋譜内のコメントはシェアされません"

  override val EMBED_LABEL: String = "サイト埋め込み"
  override val EMBED_BUTTON: String = "埋め込みメニューを開く"
  override val EMBED_CODE: String = "埋め込みコード"
  override val EMBED_OPTIONS: String = "埋め込み設定"
  override val EMBED_CONTENT: String = "コンテンツ"
  override val EMBED_REFERENCE: String = "クエリ・パラメータ詳細"
  override val AUTO_DETECT: String = "自動検出"

  override val MANAGE: String = "ファイル管理"
  override val LOAD: String = "読み込み"
  override val SAVE: String = "保存"
  override val LOAD_FROM_FILE: String = "棋譜ファイル読み込み"
  override val LOAD_FROM_TEXT: String = "テキスト読み込み"
  override val LOAD_FROM_TEXT_PLACEHOLDER: String = "ここに棋譜を貼り付けてください"
  override val LOAD_FROM_TEXT_TOOLTIP: String = "テキストボックスの内容を棋譜として読み込み"
  override val TEXT_CLEAR: String = "消去"
  override val TEXT_CLEAR_TOOLTIP: String = "テキストボックスの内容を消去"
  override val TEXT_CLEARED: String = "消去しました"
  override val SAVE_TO_FILE_CLIPBOARD: String = "棋譜ファイル / クリップボードへ書き出し"
  override val SAVE_TO_FILE_TOOLTIP: String = "ファイルとして棋譜を保存"
  override val FILE_NAME: String = "ファイル名"
  override val BROWSE: String = "選択"
  override val LOADING: String = "読み込み中"
  override val LOAD_FROM_URL: String = "URL 読み込み"
  override val LOAD_FROM_URL_PLACEHOLDER: String = "ここに URL を貼り付けてください"
  override val LOAD_FROM_URL_TOOLTIP: String = "URL の内容を棋譜として読み込み"
  override val DOWNLOADING: String = "ダウンロード中"
  override val URL_CLEAR_TOOLTIP: String = "URL の入力を消去"

  override def LOADING_TEXT(format: RecordFormat): String = s"${format} 形式として読み込み中..."

  override def FILE_TOO_LARGE(maxFileSizeKB: Int): String = s"ファイルサイズが大きすぎます (最大 ${maxFileSizeKB}KB)"

  override val ERROR_OPEN_FILE: String = "ファイルを開けませんでした"
  override val ERROR_SELECT_FILE: String = "ファイルの選択に失敗しました"
  override val ERROR: String = "エラー"
  override val UNKNOWN_TYPE: String = "不明な棋譜形式です"
  override val LOAD_SUCCESS: String = "読み込み完了"
  override val LOAD_INFO_MOVES: String = "手"
  override val LOAD_INFO_BRANCHES: String = "変化"
  override val LOAD_FAILURE: String = ERROR
  override val INVALID_URL: String = "不正な URL フォーマットです"
  override val INVALID_URL_SUFFIX: String = "URL の末尾は .csa, .kif, .ki2 のいずれかである必要があります"

  override val ACTION: String = "特殊な指し手"
  override val RESIGN: String = "投了"

  override val ANALYZE: String = "解析"
  override val ANALYZE_FOR_CHECKMATE: String = "詰み解析"
  override val ANALYZE_CHECKMATE_TOOLTIP: String = "この局面の詰み手順を解析"

  override val ADD_CHECKMATE_MOVES: String = "手順を棋譜に追記"
  override val ADD_CHECKMATE_MOVES_TOOLTIP: String = "この手順を現在の棋譜に反映"

  override val CHECKMATE_MOVES_ADDED: String = "詰み手順が反映されました。"
  override val TIMEOUT: String = "時間制限"
  override val SEC: String = "秒"
  override val ANALYZING: String = "解析中"
  override val CHECKMATE_ANALYZE_TIMEOUT: String = "制限時間内に解析できませんでした。"
  override val NO_CHECKMATES: String = "詰みはありません。"
  override val CHECKMATE_FOUND: String = "詰み手順を発見しました"
  override val BRANCH: String = "変化"
  override val DELETE: String = "削除"
  override val DELETE_BRANCH: String = "この変化を削除"
  override val DELETE_BRANCH_TOOLTIP: String = "この変化を削除"
  override val NEW_BRANCH_TOOLTIP: String = "新しい指し手に対して変化を作成"
  override val NEW_BRANCH_MODE: String = "分岐作成モード"
  override val NEW_BRANCH_HELP: String = "新しい指し手に対して常に変化を作成します。"
  override val CHANGE_BRANCH: String = "変化を選択"
  override val FORKS: String = "分岐"
  override val NO_FORKS: String = "この局面に分岐はありません。"

  override val COUNT_POINT: String = "点数計算"
  override val COUNT_POINT_LABEL: String = "点数計算 (持将棋判定用)"
  override val COUNT_POINT_TOOLTIP: String = "局面の点数を計算 (持将棋判定用)"

  override def COUNT_POINT_RESULT(point: Int, isKingInPromotionZone: Boolean, numPiecesInPromotionZone: Int): String = {
    Seq(
      s"点数: ${point}点",
      "敵陣3段目以内: " + isKingInPromotionZone.fold("玉 + ", "") + s"${numPiecesInPromotionZone}枚"
    ).mkString("\n")
  }

  override val RESET: String = "初期局面"
  override val INITIAL_STATE: Map[State, String] = Map(
    State.HIRATE -> "平手",
    State.MATING_BLACK -> "詰将棋 (先手)",
    State.MATING_WHITE -> "詰将棋 (後手)",
    State.HANDICAP_LANCE -> "香落ち",
    State.HANDICAP_BISHOP -> "角落ち",
    State.HANDICAP_ROOK -> "飛車落ち",
    State.HANDICAP_ROOK_LANCE -> "飛香落ち",
    State.HANDICAP_2_PIECE -> "二枚落ち",
    State.HANDICAP_3_PIECE -> "三枚落ち",
    State.HANDICAP_4_PIECE -> "四枚落ち",
    State.HANDICAP_5_PIECE -> "五枚落ち",
    State.HANDICAP_6_PIECE -> "六枚落ち",
    State.HANDICAP_8_PIECE -> "八枚落ち",
    State.HANDICAP_10_PIECE -> "十枚落ち",
    State.HANDICAP_THREE_PAWNS -> "歩三兵",
    State.HANDICAP_NAKED_KING -> "裸玉",
    State.empty -> "全て駒箱へ"
  )

  override val SETTINGS: String = "設定"
  override val BOARD_SIZE: String = "盤面サイズ"
  override val LAYOUT: String = "レイアウト"
  override val PIECE_GRAPHIC: String = "駒画像"
  override val BOARD_BACKGROUND: String = "盤面背景"
  override val BOARD_INDEX_TYPE: String = "座標表示"
  override val DOUBLE_BOARD_MODE: String = "ダブル将棋盤モード"
  override val VISUAL_EFFECTS: String = "エフェクト"
  override val SOUND_EFFECTS: String = "サウンド"
  override val MESSAGE_LANG: String = "表示言語"
  override val RECORD_LANG: String = "棋譜言語"
  override val SETTINGS_INFO: String = "設定はお使いのブラウザに保存されます"

  override val ENGLISH: String = "英語"
  override val JAPANESE: String = "日本語"

  override val IMAGE_SIZE_OPTIONS: Map[ImageSize, String] = Map(
    ImageSize.Small -> "小",
    ImageSize.Medium -> "中",
    ImageSize.Large -> "大"
  )
  override val FORMAT: String = "フォーマット"

  override val BOARD_SIZE_OPTIONS: Map[Option[Int], String] = Map(
    None -> "自動",
    Some(15) -> "15 - 最小",
    Some(20) -> "20",
    Some(25) -> "25",
    Some(30) -> "30 - 小",
    Some(40) -> "40 - 中",
    Some(50) -> "50 - 大",
    Some(60) -> "60 - 特大"
  )

  override val LAYOUT_OPTIONS: Map[SVGAreaLayout, String] = Map(
    SVGStandardLayout -> "スタンダード",
    SVGCompactLayout -> "コンパクト",
    SVGWideLayout -> "ワイド"
  )

  override val PIECE_GRAPHIC_OPTIONS: Map[PieceFace, String] = {
    import PieceFace._
    Map(
      JapaneseOneCharFace -> "日本語 1",
      JapaneseOneCharGraphicalFace -> "日本語 2",
      JapaneseTwoCharGraphicalFace -> "日本語 3",
      JapaneseOneCharHidetchiFace -> "日本語 4",
      JapaneseOneCharHidetchiRyokoFace -> "日本語 5",
      WesternOneCharFace -> "西洋 1",
      HidetchiInternational -> "Hidetchi 国際駒",
      ShogiCzFace -> "Shogi.cz"
    )
  }

  override val BOARD_BACKGROUND_OPTIONS: Map[(String, String, String), String] = Map(
    FrontendSettings.color.defaultTheme -> "デフォルト",
    FrontendSettings.color.naturalTheme -> "ナチュラル"
  )

  override def BOARD_INDEX_TYPE_OPTIONS: Map[BoardIndexType, String] = Map(
    BoardIndexJapanese -> "日本語",
    BoardIndexEnglish -> "英語",
    BoardIndexNumber -> "数字のみ"
  )

  override val HELP: String = "ヘルプ"
  override val HELP_CONTENT: Seq[TypedTag[LI]] = Seq(
    li("対局者の名前をクリックすると、対局情報設定画面が現れます。"),
    li("Play モードでは駒のフリック操作で素早く指し手を入力できます。"),
    li("View モードでは、盤面をクリックまたは押し続けることで表示局面を変えることができます。盤面の右側をクリックすると 1手先に進み、左側をクリックすると 1手前に戻ります。"),
    li(WebComponent.glyph("backward"), " または ", WebComponent.glyph("forward"), " ボタンを 1秒以上押し続けると、連続して表示局面を変えることができます。")
  )

  override val ABOUT_THIS_SITE: String = "このサイトについて"

  override val ABOUT_CONTENT: Seq[Frag] = Seq(
    p(i(""""インストール不要。どんなデバイスでも OK!"""")),
    p("Shogi Playground (将棋・プレイグラウンド) は世界中の将棋ファンのためのプラットフォームです。" +
      "このモバイル・フレンドリーなウェブサイトで、棋譜や詰将棋を手軽に管理、解析、共有しましょう。"),
    p("不具合、改善のアイデア等がございましたら ",
      a(href := FrontendSettings.url.authorContactUrl, "作者"),
      " までご連絡ください。"),
    br(),
    label("スペシャル・サンクス"),
    ul(
      li(
        "駒画像 - ",
        a(href := FrontendSettings.url.credit.shineleckomaUrl, "しんえれ外部駒"),
        " / ",
        a(href := FrontendSettings.url.credit.hidetchiUrl, "81 Dojo"),
        " / ",
        a(href := FrontendSettings.url.credit.shogiCzUrl, "Shogi.cz")
      ),
      li(
        "駒用フォント - ",
        a(href := FrontendSettings.url.credit.loraFontsUrl, "Lora Fonts")
      )
    ),
    br(),
    label("対応ブラウザ"),
    ul(
      li("Firefox"),
      li("Chrome"),
      li("Safari")
    )
  )

  override val MOVES: String = "棋譜"
  override val COPY_ALL_MOVES: String = "全ての指し手をコピー"
  override val TRUNK: String = "本譜"

  override val CONFIRMATION: String = "確認"
  override val GAME_INFORMATION: String = "対局情報"
  override val PLAYER_NAMES: String = "対局者名"
  override val UPDATE: String = "更新"
  override val ASK_PROMOTE: String = "成りますか?"
  override val YES: String = "はい"
  override val NO: String = "いいえ"
  override val ASK_RESIGN: String = "本当に投了してよろしいですか?"
  override val IMAGE_DOWNLOAD_FAILURE: String = "画像ファイルのダウンロードに失敗しました:"
  override val ASK_EDIT: String = "棋譜およびコメントの情報が失われますが、よろしいですか?"

  override def ASK_DELETE_BRANCH(branchNo: BranchNo): String = s"現在の変化 (変化#${branchNo}) が削除されます。コメントも失われますが、よろしいですか?"

  override val INVALID_STATE: String = "不正な局面です。"
}
