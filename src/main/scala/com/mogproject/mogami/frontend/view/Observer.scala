package com.mogproject.mogami.frontend.view

/**
  *
  */
trait Observer[T] {
  def handleUpdate(subject: T): Unit
}
