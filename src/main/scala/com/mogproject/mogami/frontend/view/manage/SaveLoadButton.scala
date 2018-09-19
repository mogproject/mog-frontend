package com.mogproject.mogami.frontend.view.manage

import java.net.URI

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
class SaveLoadButton(isMobile: Boolean, freeMode: Boolean) extends WebComponent with RecordLoader {
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
      dom.window.setTimeout(() => readSingleFile(fileName => content => loadRecord(fileName, content, freeMode)), 500)
    }
  ).render

  private[this] lazy val fileLoadButton: Label = label(
    cls := "btn btn-default btn-block",
    onclick := { () =>
      displayFileLoadMessage("")
      fileLoadInput.value = ""
    },
    WebComponent.dynamicSpan(_.BROWSE).element,
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
      displayTextLoadMessage(Messages.get.LOADING_TEXT(format))
      textLoadButton.disableElement()
      dom.window.setTimeout(() => readRecordText(format, text), 500)
    }
  )
    .withDynamicTextContent(_.LOAD)
    .withDynamicHoverTooltip(_.LOAD_FROM_TEXT_TOOLTIP)

  private[this] lazy val textLoadMessage: Div = div(
    cls := "col-sm-9 col-xs-7 text-muted",
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
    (m: Messages) => RecordFormat.all.map { k => k -> k.toString }.toMap,
    dropdownClass = "input-group-btn",
    labelClass = "dropdown-record",
    dropdownHeader = Some(_.FORMAT)
  )

  private[this] val fileSaveButton = CommandButton(
    classButtonDefault,
    onclick := { () => doAction(SaveRecordAction(fileSaveFormat.getValue, getFileName)) }
  )
    .withDynamicTextContent(_.SAVE)
    .withDynamicHoverTooltip(_.SAVE_TO_FILE_TOOLTIP)

  private[this] lazy val textCopyButton: WebComponent = CommandButton(
    classButtonDefault,
    onclick := { () =>
      displayTextLoadMessage("")
      doAction(CopyRecordAction(fileSaveFormat.getValue))
    },
    clipboardTarget(textLoadInputId)
  )
    .withManualTooltip()
    .withDynamicTextContent(_.COPY)

  def renderRecord(record: String): Unit = {
    textLoadInput.element.value = record
    dom.window.setTimeout(() => textCopyButton.element.asInstanceOf[Button].focus(), 0)
  }

  //
  // elements #4: Load from URL
  //
  protected lazy val urlLoadInput: InputComponent = InputComponent(
    (m: Messages) => m.LOAD_FROM_URL_PLACEHOLDER,
    (s: String) => urlLoadButton.setDisabled(s.isEmpty),
    () => displayUrlLoadMessage(""),
    onchange := { () => if (urlLoadInput.getValue.isEmpty) urlLoadButton.disableElement() else clearUrlLoad() }
  )

  private[this] lazy val urlLoadButton: WebComponent = CommandButton(
    classButtonDefaultBlock,
    disabled := true,
    onclick := { () =>
      urlLoadButton.disableElement()

      // validate
      urlLoadInput.updateValue(urlLoadInput.getValue.trim)

      Some(urlLoadInput.getValue).flatMap {
        case url if !Seq(".csa", ".kif", ".ki2").exists(url.toLowerCase().endsWith) =>
          displayUrlLoadMessage(Messages.get.INVALID_URL_SUFFIX)
          None
        case url if Try(new URI(url)).toOption.exists(_.isAbsolute) =>
          // ok
          Some(url)
        case url if Try(new URI("http://" + url)).toOption.exists(_.isAbsolute) =>
          // complement
          val u = "http://" + url
          urlLoadInput.updateValue(u)
          Some(u)
        case _ =>
          displayUrlLoadMessage(Messages.get.INVALID_URL)
          None
      } map { url =>
        displayUrlLoadMessage(Messages.get.DOWNLOADING + "...")
        dom.window.setTimeout(() => readUrl(url), 500)
      }
    }
  )
    .withDynamicTextContent(_.LOAD)
    .withDynamicHoverTooltip(_.LOAD_FROM_URL_TOOLTIP)

  private[this] lazy val urlLoadMessage: Div = div(
    cls := "col-sm-9 col-xs-7 text-muted",
    marginTop := 6
  ).render

  lazy val externalUrlCopyButton = new ExternalUrlCopyButton

  //
  // layout
  //
  override lazy val element: Div = {
    val elem = div(
      WebComponent.dynamicLabel(_.LOAD_FROM_FILE).element,
      div(
        cls := "row",
        marginTop := 3.px,
        div(cls := "col-xs-5 col-sm-3", fileLoadButton),
        fileLoadMessage
      ),
      br(),
      WebComponent.dynamicLabel(_.LOAD_FROM_TEXT).element,
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
      WebComponent.dynamicLabel(_.LOAD_FROM_URL).element,
      div(
        urlLoadInput.element
      ),
      div(
        cls := "row",
        marginTop := 3,
        div(cls := "col-xs-5 col-sm-3", urlLoadButton.element),
        urlLoadMessage
      ),
      div(
        marginTop := 3,
        externalUrlCopyButton.element
      ),
      br(),
      WebComponent.dynamicLabel(_.SAVE_TO_FILE_CLIPBOARD).element,
      div(cls := "input-group",
        fileSaveName.element,
        span(cls := "input-group-addon", padding := 6, "."),
        div(cls := "input-group-btn", fileSaveFormat.element),
        div(
          cls := "input-group-btn",
          fileSaveButton.element,
          textCopyButton.element
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
        abortFileLoad(s"[${Messages.get.ERROR}] ${Messages.get.FILE_TOO_LARGE(maxFileSizeKB)}")
        true
      }

      Try(TextReader.readTextFile(f, callback(f.name), sizeChecker)) match {
        case Success(_) => // do nothing
        case Failure(_) => abortFileLoad(s"[${Messages.get.ERROR}] ${Messages.get.ERROR_OPEN_FILE}")
      }
    }).getOrElse {
      abortFileLoad(s"[${Messages.get.ERROR}] ${Messages.get.ERROR_SELECT_FILE}")
    }
  }

  private[this] def readRecordText(format: RecordFormat, text: String): Unit = {
    loadRecordText(format, text, freeMode)
    textLoadButton.enableElement()
  }

  private[this] def readUrl(url: String): Unit = {
    loadRecordUrl(url, freeMode)
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

  protected def displayUrlLoadMessage(message: String): Unit = {
    urlLoadMessage.innerHTML = message
  }

  protected def displayUrlInputTooltip(message: String): Unit = {
    Tooltip.display(urlLoadInput.element, message, 2000)
  }

  protected def displayUrlLoadTooltip(message: String): Unit = {
    Tooltip.display(urlLoadButton.element, message, 2000)
  }

  private[this] def abortFileLoad(message: String): Unit = {
    displayFileLoadMessage(message)
    displayFileLoadTooltip(Messages.get.LOAD_FAILURE)
    clearFileLoad()
  }

  protected def abortUrlLoad(message: String): Unit = {
    displayUrlLoadMessage(message)
    displayUrlLoadTooltip(Messages.get.LOAD_FAILURE)
    clearUrlLoad()
  }

  private[this] def clearFileLoad(): Unit = {
    fileLoadButton.disabled = false
  }

  private[this] def clearUrlLoad(): Unit = {
    urlLoadButton.enableElement()
  }

  //
  // helper functions
  //
  private[this] def getFileName: String = {
    val s = fileSaveName.element.asInstanceOf[Input].value
    s.isEmpty.fold(DEFAULT_FILE_NAME, s) + "." + fileSaveFormat.getValue.toString.toLowerCase()
  }
}