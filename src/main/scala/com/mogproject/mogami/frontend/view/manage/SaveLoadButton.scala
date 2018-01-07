package com.mogproject.mogami.frontend.view.manage

import com.mogproject.mogami.frontend.action.manage.{CopyRecordAction, SaveRecordAction}
import com.mogproject.mogami.frontend.io.TextReader
import com.mogproject.mogami.frontend.model.io.{KIF, RecordFormat}
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.button._
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.html._

import scala.util.{Failure, Success, Try}
import scalatags.JsDom.all._

/**
  *
  */
class SaveLoadButton(isMobile: Boolean) extends WebComponent with RecordLoader {
  // constants
  private[this] final val DEFAULT_FILE_NAME = "record"

  private[this] val textLoadInputId = "textLoadInput"

  //
  // elements #1: Load from File
  //
  private[this] lazy val fileLoadInput: Input = input(
    tpe := "file",
    display := "none",
    onchange := { () =>
      displayFileLoadMessage(Messages.get.LOADING + "...")
      fileLoadButton.disabled = true
      dom.window.setTimeout(() => readSingleFile(fileName => content => loadRecord(fileName, content)), 500)
    }
  ).render

  private[this] lazy val fileLoadButton: Label = label(
    cls := "btn btn-default btn-block",
    onclick := { () =>
      displayFileLoadMessage("")
      fileLoadInput.value = ""
    },
    DynamicComponent(_.BROWSE).element,
    fileLoadInput
  ).render

  private[this] lazy val fileLoadMessage: Div = div(
    cls := "col-xs-7 col-sm-9 text-muted",
    marginTop := 6.px,
    height := 40.px
  ).render

  //
  // elements #2: Load from Text
  //
  // @note `textLoadInput` area is also used for clipboard copy
  private[this] lazy val textLoadInput: TextAreaComponent = TextAreaComponent("", 5, (m: Messages) => m.LOAD_FROM_TEXT_PLACEHOLDER, id := textLoadInputId)

  private[this] lazy val textLoadButton: WebComponent = CommandButton(
    classButtonDefaultBlock,
    onclick := { () =>
      val text = textLoadInput.element.value
      val format = RecordFormat.detect(text)
      displayTextLoadMessage(s"Loading as ${format} Format...")
      textLoadButton.disableElement()
      dom.window.setTimeout(() => readRecordText(format, text), 500)
    }
  )
    .withDynamicTextContent(_.LOAD)
    .withDynamicHoverTooltip(_.LOAD_FROM_TEXT_TOOLTIP)

  private[this] lazy val textLoadMessage: Div = div(
    cls := "col-sm-9 col-xs-8 text-muted",
    marginTop := 6
  ).render

  private[this] lazy val textClearButton: WebComponent = CommandButton(
    classButtonDefaultBlock, onclick := { () =>
      displayTextLoadMessage("")
      textLoadInput.element.value = ""
      displayTextLoadTooltip(Messages.get.TEXT_CLEARED)
    }
  )
    .withDynamicTextContent(_.TEXT_CLEAR)
    .withDynamicHoverTooltip(_.TEXT_CLEAR_TOOLTIP)

  //
  // elements #3: Save to File/ Clipboard
  //
  private[this] val fileSaveName = WebComponent(
    input(
      tpe := "text",
      cls := "form-control",
      value := DEFAULT_FILE_NAME
    )
  )
    .withDynamicPlaceholder(_.FILE_NAME)

  private[this] val fileSaveFormat: DropdownMenu[RecordFormat] = DropdownMenu(
    RecordFormat.all,
    DropdownMenu.buildLabels(RecordFormat.all),
    dropdownClass = "input-group-btn",
    labelClass = "dropdown-record",
    dropdownHeader = Some("Format")
  )

  private[this] val fileSaveButton: SingleButton = SingleButton(
    Map(English -> "Save".render),
    tooltip = isMobile.fold(Map.empty, Map(English -> "Save record as a file")),
    clickAction = Some({ () => doAction(SaveRecordAction(fileSaveFormat.getValue, getFileName)) })
  )

  private[this] lazy val textCopyButton: Button = button(
    cls := "btn btn-default",
    tpe := "button",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("trigger") := "manual",
    data("clipboard-target") := "#" + textLoadInputId,
    onclick := { () =>
      displayTextLoadMessage("")
      doAction(CopyRecordAction(fileSaveFormat.getValue))
    },
    "Copy"
  ).render

  def renderRecord(record: String): Unit = {
    textLoadInput.element.value = record
    dom.window.setTimeout(() => textCopyButton.focus(), 0)
  }

  //
  // layout
  //
  override lazy val element: Div = {
    val elem = div(
      label(DynamicComponent(_.LOAD_FROM_FILE).element),
      div(
        cls := "row",
        marginTop := 3.px,
        div(cls := "col-xs-5 col-sm-3", fileLoadButton),
        fileLoadMessage
      ),
      br(),
      label(DynamicComponent(_.LOAD_FROM_TEXT).element),
      textLoadInput.element,
      div(
        cls := "row",
        marginTop := 3,
        div(cls := "col-xs-5 col-sm-3", textLoadButton.element),
        textLoadMessage
      ),
      div(
        cls := "row",
        marginTop := 3,
        div(cls := "col-xs-5 col-sm-3", textClearButton.element)
      ),
      br(),
      label(DynamicComponent(_.SAVE_TO_FILE_CLIPBOARD).element),
      div(cls := "input-group",
        fileSaveName.element,
        span(cls := "input-group-addon", padding := 6, "."),
        div(cls := "input-group-btn", fileSaveFormat.element),
        div(
          cls := "input-group-btn",
          fileSaveButton.element,
          textCopyButton
        )
      )
    ).render
    fileSaveFormat.select(KIF)
    elem
  }


  //
  // File I/O
  //
  private[this] def readSingleFile(callback: String => String => Unit): Unit = {
    val maxFileSizeKB = 100

    val head = (fileLoadInput.files.length >= 0).option(fileLoadInput.files(0))
    (for {
      f <- head
    } yield {
      def sizeChecker(sz: Int): Boolean = if (sz <= maxFileSizeKB * 1024) {
        false
      } else {
        abortFileLoad(s"[Error] File too large. (must be <= ${maxFileSizeKB}KB)")
        true
      }

      Try(TextReader.readTextFile(f, callback(f.name), sizeChecker)) match {
        case Success(_) => // do nothing
        case Failure(_) => abortFileLoad("[Error] Failed to open the file.")
      }
    }).getOrElse {
      abortFileLoad("[Error] Failed to select the file.")
    }
  }

  private[this] def readRecordText(format: RecordFormat, text: String): Unit = {
    loadRecordText(format, text)
    clearTextLoad()
  }

  //
  // messaging
  //
  protected def displayFileLoadMessage(message: String): Unit = {
    fileLoadMessage.innerHTML = message
  }

  protected def displayFileLoadTooltip(message: String): Unit = {
    Tooltip.display(fileLoadButton, message, 2000)
  }

  protected def displayTextLoadMessage(message: String): Unit = {
    textLoadMessage.innerHTML = message
  }

  protected def displayTextLoadTooltip(message: String): Unit = {
    Tooltip.display(textLoadInput.element, message, 2000)
  }

  private[this] def abortFileLoad(message: String): Unit = {
    displayFileLoadMessage(message)
    displayFileLoadTooltip("Failed!")
    clearFileLoad()
  }

  private[this] def clearFileLoad(): Unit = {
    fileLoadButton.disabled = false
  }

  private[this] def clearTextLoad(): Unit = {
    textLoadButton.enableElement()
  }

  //
  // helper functions
  //
  private[this] def getFileName: String = {
    val s = fileSaveName.element.asInstanceOf[Input].value
    s.isEmpty.fold(DEFAULT_FILE_NAME, s) + "." + fileSaveFormat.getValue.toString.toLowerCase()
  }
}