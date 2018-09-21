package com.mogproject.mogami.frontend.view.control


import com.mogproject.mogami._
import com.mogproject.mogami.frontend.action.{PlaygroundAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend.model.Language
import com.mogproject.mogami.frontend.view.button.CommandButton
import com.mogproject.mogami.frontend.view.control.ControlBarType.ControlBarType
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.common.datagrid.DataGrid
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.{HTMLElement, HTMLSelectElement}
import scalatags.JsDom.all._
import org.scalajs.dom.Event

/**
  *
  */
case class ControlBar(barType: ControlBarType) extends WebComponent with PlaygroundSAMObserver {

  //
  // Elements
  //
  private[this] lazy val recordSelector: HTMLSelectElement = {
    barType match {
      case ControlBarType.LongList => throw new IllegalArgumentException("Unexpected initialization for LongList.")
      case _ =>
        val cc = barType match {
          case ControlBarType.Normal => ""
          case ControlBarType.Small => " control-small"
          case _ => throw new RuntimeException("unexpected bar type")
        }
        select(
          cls := "form-control rect-select" + cc,
          onchange := { e: Event =>
            e.target match {
              case elem: HTMLSelectElement => doAction(UpdateGameControlAction(_.copy(displayPosition = elem.selectedIndex)))
              case _ => // do nothing
            }
          }
        ).render
    }
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

  private[this] lazy val dataGrid = new DataGrid[MoveListData](
    MoveListColumn.columns,
    20,
    index => PlaygroundSAM.doAction(UpdateGameControlAction(gc => gc.copy(displayPosition = index)))
  )

  override val element: HTMLElement = barType match {
    case ControlBarType.LongList => dataGrid.element //recordSelector
    case _ => controlBar
  }

  //
  // Utility
  //
  private[this] def createRecordContent(gc: GameControl, recordLang: Language): String = {
    gc.getAllMoveRepresentation(recordLang).map {
      case (i, s, hasComment, hasFork, _) => // ignore elapsed time
        val symbolMark = hasFork.fold("+", hasComment.fold("*", ""))
        val index = i.map(_ + ": ").getOrElse("")
        option(symbolMark + index + s)
    }.mkString
  }

  private[this] def createRecordData(gc: GameControl, recordLang: Language): Seq[MoveListData] = {
    gc.getAllMoveRepresentation(recordLang).map {
      case (i, s, hasComment, hasFork, elapsedTime) => MoveListData(i, hasComment, hasFork, s, elapsedTime)
    }
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
          if (barType == ControlBarType.LongList) {
            dataGrid.updateData(createRecordData(gc, model.config.recordLang))
          } else {
            recordSelector.innerHTML = createRecordContent(gc, model.config.recordLang)
          }
        }

        if (barType == ControlBarType.LongList) {
          dataGrid.selectRow(gc.displayPosition)
        } else {
          recordSelector.selectedIndex = gc.displayPosition
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
