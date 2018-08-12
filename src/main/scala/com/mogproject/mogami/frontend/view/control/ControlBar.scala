package com.mogproject.mogami.frontend.view.control


import com.mogproject.mogami._
import com.mogproject.mogami.frontend.action.{PlaygroundAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend.model.Language
import com.mogproject.mogami.frontend.view.button.CommandButton
import com.mogproject.mogami.frontend.view.control.ControlBarType.ControlBarType
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.{HTMLElement, HTMLSelectElement}

import scalatags.JsDom.all._

/**
  *
  */
case class ControlBar(barType: ControlBarType) extends WebComponent with PlaygroundSAMObserver {

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


  private[this] def createRecordContent(gc: GameControl, recordLang: Language): String = {
    gc.getAllMoveRepresentation(recordLang).map {
      case (i, s, hasComment, hasFork) =>
        val symbolMark = hasFork.fold("+", hasComment.fold("*", ""))
        val index = i.map(_ + ": ").getOrElse("")
        option(symbolMark + index + s)
    }.mkString
  }

  //
  // Observer
  //
  override val samObserveMask: Long = {
    import ObserveFlag._
    GAME_BRANCH | GAME_COMMENT | GAME_POSITION | GAME_BRANCH_CHANGED | CONF_RCD_LANG | MODE_EDIT
  }

  override def refresh(model: PlaygroundModel, flag: StateHash): Unit = {
    import ObserveFlag._

    model.mode.getGameControl match {
      case Some(gc) =>
        show()

        if ((flag & (GAME_BRANCH | GAME_COMMENT | GAME_BRANCH_CHANGED | CONF_RCD_LANG | MODE_EDIT)) != 0) {
          recordSelector.innerHTML = createRecordContent(gc, model.config.recordLang)
        }

        recordSelector.selectedIndex = gc.displayPosition

        if (barType != ControlBarType.LongList) {
          if (model.mode.isLivePlaying) {
            controlInputStepBackward.setDisabled(true)
            controlInputBackward.setDisabled(true)
            controlInputForward.setDisabled(true)
            controlInputStepForward.setDisabled(true)
          } else {
            controlInputStepBackward.setDisabled(gc.isFirstDisplayPosition)
            controlInputBackward.setDisabled(gc.isFirstDisplayPosition)
            controlInputForward.setDisabled(gc.isLastDisplayPosition)
            controlInputStepForward.setDisabled(gc.isLastDisplayPosition)
          }
        }
      case None =>
        hide()
    }
  }
}
