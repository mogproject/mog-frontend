package com.mogproject.mogami.frontend.model

/**
  * Actions
  */
sealed trait Action

case object PlayAction extends Action

case object ImageAction extends Action

case object NotesAction extends Action
