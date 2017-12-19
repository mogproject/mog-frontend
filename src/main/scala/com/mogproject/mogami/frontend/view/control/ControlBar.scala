package com.mogproject.mogami.frontend.view.control


import com.mogproject.mogami.core.move.{DeclareWin, SpecialMove}
import com.mogproject.mogami._
import com.mogproject.mogami.frontend.action.{PlaygroundAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend.model.GameControl
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.button.SingleButton
import com.mogproject.mogami.frontend.view.control.ControlBarType.ControlBarType
import com.mogproject.mogami.frontend.view.{English, Japanese, Language, WebComponent}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.{HTMLElement, HTMLSelectElement}

import scalatags.JsDom.all._

/**
  *
  */
case class ControlBar(barType: ControlBarType) extends WebComponent {

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

  private[this] def createControlInput(glyph: String, clickAction: PlaygroundAction, holdCheck: Option[() => Boolean]): SingleButton = {
    val lbl = span(cls := s"glyphicon glyphicon-${glyph}", aria.hidden := true).render

    val cs = Seq("btn-default", "control-button") ++ (barType match {
      case ControlBarType.Small => Seq("control-small", "input-small")
      case _ => Seq.empty
    })

    SingleButton(Map(English -> lbl), cs, None,
      Some(() => PlaygroundSAM.doAction(clickAction)),
      holdCheck.isDefined.fold(Some(() => PlaygroundSAM.doAction(clickAction)), None),
      holdCheck.getOrElse(() => false),
      useOnClick = false
    )
  }

  private[this] lazy val controlInputStepBackward: SingleButton = createControlInput("step-backward", UpdateGameControlAction(_.withFirstDisplayPosition), None)
  private[this] lazy val controlInputBackward: SingleButton = createControlInput("backward", UpdateGameControlAction(_.withPreviousDisplayPosition), Some(() => controlInputBackward.isDisabled))
  private[this] lazy val controlInputForward: SingleButton = createControlInput("forward", UpdateGameControlAction(_.withNextDisplayPosition), Some(() => controlInputForward.isDisabled))
  private[this] lazy val controlInputStepForward: SingleButton = createControlInput("step-forward", UpdateGameControlAction(_.withLastDisplayPosition), None)

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
      case Japanese => _.toJapaneseNotationString
      case English => _.toWesternNotationString
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
    val initTurn = game.trunk.initialState.turn

    game.withBranch(branchNo) { br =>
      // moves
      val xs = (prefix +: getMoves(game, branchNo, recordLang)).zipWithIndex.map { case (m, i) =>
        val pos = i + game.trunk.offset
        val symbolMark = game.hasFork(GamePosition(branchNo, pos)).fold("+", game.hasComment(GamePosition(branchNo, pos)).fold("*", ""))
        val indexNotation = if (i == 0) "" else s"${pos}: " + (i % 2 == 0).fold(!initTurn, initTurn).toSymbolString()
        symbolMark + indexNotation + m
      }

      val suffix = (br.status, recordLang) match {
        case (GameStatus.Mated, Japanese) => List("詰み")
        case (GameStatus.Mated, English) => List("Mated")
        case (GameStatus.Drawn, Japanese) => List("千日手")
        case (GameStatus.Drawn, English) => List("Drawn")
        case (GameStatus.PerpetualCheck, Japanese) => List("連続王手の千日手")
        case (GameStatus.PerpetualCheck, English) => List("Perpetual Check")
        case (GameStatus.Uchifuzume, Japanese) => List("打ち歩詰め")
        case (GameStatus.Uchifuzume, English) => List("Uchifuzume")
        case (GameStatus.Jishogi, Japanese) => List(DeclareWin().toJapaneseNotationString)
        case (GameStatus.Jishogi, English) => List(DeclareWin().toWesternNotationString)
        case (GameStatus.IllegallyMoved, Japanese) => br.finalAction.get.toJapaneseNotationString.split("\n").toList.drop(1)
        case (GameStatus.IllegallyMoved, English) => br.finalAction.get.toWesternNotationString.split("\n").toList.drop(1)
        case _ => Nil
      }

      (xs ++ suffix).map(s => option(s)).mkString
    }.getOrElse("")
  }

  //
  // Actions
  //
  def refresh(gameControl: GameControl, recordLang: Language): Unit = {
    recordSelector.innerHTML = createRecordContent(gameControl.game, gameControl.displayBranchNo, recordLang)
    recordSelector.selectedIndex = gameControl.displayPosition

    if (barType != ControlBarType.LongList) {
      controlInputStepBackward.element.disabled = gameControl.isFirstDisplayPosition
      controlInputBackward.element.disabled = gameControl.isFirstDisplayPosition
      controlInputForward.element.disabled = gameControl.isLastDisplayPosition
      controlInputStepForward.element.disabled = gameControl.isLastDisplayPosition
    }
  }
}
