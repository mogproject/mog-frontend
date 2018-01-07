//package com.mogproject.mogami.frontend.view.i18n
//
//import org.scalajs.dom.raw.HTMLElement
//
//import scalatags.JsDom.all._
//
///**
//  *
//  */
//case class DynamicLabel(f: Messages => String, glyphicon: Option[String] = None) extends DynamicElementLike[HTMLElement] {
//  override val element: HTMLElement = span().render
//
//  private[this] val additional = glyphicon.map { g => " " + span(cls := s"glyphicon glyphicon-${g}", aria.hidden := true).toString }.getOrElse("")
//
//  override def refresh(): Unit = {
//    element.innerHTML = f(Messages.get) + additional
//  }
//}
