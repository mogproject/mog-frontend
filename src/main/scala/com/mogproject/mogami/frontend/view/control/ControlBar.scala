package com.mogproject.mogami.frontend.view.control


import com.mogproject.mogami.core.move.{DeclareWin, SpecialMove}
import com.mogproject.mogami._
import com.mogproject.mogami.frontend.action.{PlaygroundAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend.model.{English, Japanese, Language}
import com.mogproject.mogami.frontend.view.button.CommandButton
import com.mogproject.mogami.frontend.view.control.ControlBarType.ControlBarType
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.{HTMLElement, HTMLSelectElement}

import scalatags.JsDom.all._

/**
  *
  */
case class ControlBar(barType: ControlBarType) extends WebComponent with SAMObserver[BasePlaygroundModel] {

  private[this] val LONG_LIST_SIZE = 29

  //
  // Elements
  //
  private[this] val recordSelector: HTMLSelectElement = {
    val (c, sz) = barType match {
      case ControlBarType.LongList => ("control-long", Some(LONG_LIST_SIZE))
      case _ =>
        val cc = barType match {
          case ControlBarType.Normal => ""
          case ControlBarType.Small => " control-small"
          case _ => throw new RuntimeException("unexpected bar type")
        }
        ("rect-select" + cc, None)
    }

    select(
      cls := "form-control " + c,
      sz.map(size := _),
      onchange := (() => PlaygroundSAM.doAction(UpdateGameControlAction(gc => gc.copy(displayPosition = getSelectedIndex))))
    ).render
  }

  private[this] def createControlInput(glyph: String, clickAction: PlaygroundAction, holdCheck: Option[() => Boolean]): WebComponent = {
    val hasHoldAction = holdCheck.isDefined

    val cs = Seq(classButtonDefault, "control-button") ++ (barType match {
      case ControlBarType.Small => Seq("control-small", "input-small")
      case _ => Seq.empty
    })

    val ret = CommandButton(
      cs.mkString(" "),
      (!hasHoldAction).option(onclick := { () => doAction(clickAction) })
    )
      .withTextContent("", glyph)

    holdCheck match {
      case Some(f) => ret.withHoldAction(
        () => doAction(clickAction),
        () => {
          val flg = f()
          if (flg) doAction(clickAction)
          flg
        })
      case None => ret
    }
  }

  private[this] lazy val controlInputStepBackward: WebComponent = createControlInput("step-backward", UpdateGameControlAction(_.withFirstDisplayPosition), None)
  private[this] lazy val controlInputBackward: WebComponent = createControlInput("backward", UpdateGameControlAction(_.withPreviousDisplayPosition), Some(() => !controlInputBackward.isDisabled))
  private[this] lazy val controlInputForward: WebComponent = createControlInput("forward", UpdateGameControlAction(_.withNextDisplayPosition), Some(() => !controlInputForward.isDisabled))
  private[this] lazy val controlInputStepForward: WebComponent = createControlInput("step-forward", UpdateGameControlAction(_.withLastDisplayPosition), None)

  private[this] lazy val controlBar = div(
    cls := "center-block control-bar", role := "toolbar",
    div(cls := "btn-group", role := "group", width := 100.pct,
      div(cls := "btn-group", role := "group", controlInputStepBackward.element),
      div(cls := "btn-group", role := "group", controlInputBackward.element),
      div(cls := "btn-group control-select", role := "group", recordSelector),
      div(cls := "btn-group", role := "group", controlInputForward.element),
      div(cls := "btn-group", role := "group", controlInputStepForward.element)
    )
  ).render

  override val element: HTMLElement = barType match {
    case ControlBarType.LongList => recordSelector
    case _ => controlBar
  }

  //
  // Utility
  //
  private[this] def getSelectedIndex: Int = recordSelector.selectedIndex

  /**
    * Create move description
    *
    * @param game       Game instance
    * @param branchNo   branch number (trunk:0)
    * @param recordLang language
    * @return
    */
  private[this] def getMoves(game: Game, branchNo: BranchNo, recordLang: Language): List[String] = {
    val f: Move => String = recordLang match {
      case Japanese => mv => mv.player.toSymbolString() + mv.toJapaneseNotationString
      case English => mv => mv.player.toSymbolString() + mv.toWesternNotationString
    }
    val g: SpecialMove => String = recordLang match {
      case Japanese => _.toJapaneseNotationString
      case English => _.toWesternNotationString
    }
    game.withBranch(branchNo) { br =>
      (game.getAllMoves(branchNo).map(f) ++ (br.status match {
        case GameStatus.Resigned | GameStatus.TimedUp => List(g(br.finalAction.get))
        case GameStatus.IllegallyMoved => g(br.finalAction.get).split("\n").toList.take(1)
        case _ => Nil
      })).toList
    }.getOrElse(Nil)
  }

  private[this] def createRecordContent(game: Game, branchNo: BranchNo, recordLang: Language): String = {

    val prefix = recordLang match {
      case Japanese => "初期局面"
      case English => "Start"
    }

    game.withBranch(branchNo) { br =>
      // moves
      val xs = (prefix +: getMoves(game, branchNo, recordLang)).zipWithIndex.map { case (m, i) =>
        val pos = i + game.trunk.offset
        val symbolMark = game.hasFork(GamePosition(branchNo, pos)).fold("+", game.hasComment(GamePosition(branchNo, pos)).fold("*", ""))
        val indexNotation = if (i == 0) "" else s"${pos}: "
        symbolMark + indexNotation + m
      }

      val suffix = (br.status, recordLang) match {
        case (GameStatus.Playing, _) => None
        case (GameStatus.Mated, Japanese) => Some("詰み")
        case (GameStatus.Mated, English) => Some("Mated")
        case (GameStatus.Drawn, Japanese) => Some("千日手")
        case (GameStatus.Drawn, English) => Some("Drawn")
        case (GameStatus.PerpetualCheck, Japanese) => Some("連続王手の千日手")
        case (GameStatus.PerpetualCheck, English) => Some("Perpetual Check")
        case (GameStatus.Uchifuzume, Japanese) => Some("打ち歩詰め")
        case (GameStatus.Uchifuzume, English) => Some("Uchifuzume")
        case (GameStatus.Jishogi, Japanese) => Some(DeclareWin().toJapaneseNotationString)
        case (GameStatus.Jishogi, English) => Some(DeclareWin().toWesternNotationString)
        case (GameStatus.IllegallyMoved, Japanese) => Some(IllegalMove.kifKeyword)
        case (GameStatus.IllegallyMoved, English) => Some("Illegal Move")
        case _ => None
      }

      (xs ++ suffix).map(s => option(s)).mkString
    }.getOrElse("")
  }

  //
  // Observer
  //
  override val samObserveMask: Int = {
    import ObserveFlag._
    GAME_BRANCH | GAME_COMMENT | GAME_POSITION | GAME_BRANCH_CHANGED | CONF_RCD_LANG | MODE_EDIT
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    import ObserveFlag._

    model.mode.getGameControl match {
      case Some(gc) =>
        show()

        if ((flag & (GAME_BRANCH | GAME_COMMENT | GAME_BRANCH_CHANGED | CONF_RCD_LANG | MODE_EDIT)) != 0) {
          recordSelector.innerHTML = createRecordContent(gc.game, gc.displayBranchNo, model.config.recordLang)
        }

        recordSelector.selectedIndex = gc.displayPosition

        if (barType != ControlBarType.LongList) {
          controlInputStepBackward.setDisabled(gc.isFirstDisplayPosition)
          controlInputBackward.setDisabled(gc.isFirstDisplayPosition)
          controlInputForward.setDisabled(gc.isLastDisplayPosition)
          controlInputStepForward.setDisabled(gc.isLastDisplayPosition)
        }
      case None =>
        hide()
    }
  }
}
