package com.mogproject.mogami.frontend.view.control


import com.mogproject.mogami.core.move.{DeclareWin, SpecialMove}
import com.mogproject.mogami._
import com.mogproject.mogami.frontend.action.{PlaygroundAction, UpdateGameControlAction}
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

  def getMaxRecordIndex: Int = recordSelector.options.length - 1

  def getSelectedIndex: Int = recordSelector.selectedIndex

  def getRecordIndex(index: Int): Int = (index < 0).fold(getMaxRecordIndex, math.min(index, getMaxRecordIndex))

  def updateRecordIndex(index: Int): Unit = {
    val x = getRecordIndex(index)
    recordSelector.selectedIndex = x
  }

  /**
    * Create move description
    *
    * @param game     Game instance
    * @param branchNo branch number (trunk:0)
    * @param lng      language
    * @return
    */
  private[this] def getMoves(game: Game, branchNo: BranchNo, lng: Language): List[String] = {
    val f: Move => String = lng match {
      case Japanese => _.toJapaneseNotationString
      case English => _.toWesternNotationString
    }
    val g: SpecialMove => String = lng match {
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

  def updateRecordContent(game: Game, branchNo: BranchNo, lng: Language): Unit = {

    val prefix = lng match {
      case Japanese => "初期局面"
      case English => "Start"
    }
    val initTurn = game.trunk.initialState.turn

    game.withBranch(branchNo) { br =>
      // moves
      val xs = (prefix +: getMoves(game, branchNo, lng)).zipWithIndex.map { case (m, i) =>
        val pos = i + game.trunk.offset
        val symbolMark = game.hasFork(GamePosition(branchNo, pos)).fold("+", game.hasComment(GamePosition(branchNo, pos)).fold("*", ""))
        val indexNotation = if (i == 0) "" else s"${pos}: " + (i % 2 == 0).fold(!initTurn, initTurn).toSymbolString()
        symbolMark + indexNotation + m
      }

      val suffix = (br.status, lng) match {
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
      val s = (xs ++ suffix).map(s => option(s)).mkString

      recordSelector.innerHTML = s
    }
  }

  //
  // control buttons
  //
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

//  override def childComponents: Seq[WebComponent] = super.childComponents ++ (barType match {
//    case ControlBarType.Small | ControlBarType.Normal => Seq(controlInputStepBackward, controlInputBackward, controlInputForward, controlInputStepForward)
//    case _ => Seq.empty
//  })

  //
  // actions
  //
  def updateLabels(backwardEnabled: Boolean, forwardEnabled: Boolean): Unit = {
    controlInputStepBackward.element.disabled = !backwardEnabled
    controlInputBackward.element.disabled = !backwardEnabled
    controlInputForward.element.disabled = !forwardEnabled
    controlInputStepForward.element.disabled = !forwardEnabled
  }
//
//  //
//  // initialization
//  //
//  override def initialize(): Unit = {
//    super.initialize()
//  }

}
