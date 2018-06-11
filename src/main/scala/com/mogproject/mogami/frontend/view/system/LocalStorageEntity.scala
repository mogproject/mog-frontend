package com.mogproject.mogami.frontend.view.system

/**
  * Manages one LocalStorage entity and conversion rules.
  *
  * @param key    key string of the LocalStorage entry
  * @param reader function that takes a LocalStorage value and update a LocalStorageLike object
  * @param writer function that takes a LocalStorageLike object and returns a value string wrapped by Option:
  *               None: do nothing when saving
  *               Some(""): clear LocalStorage key
  *               Some(s): save LocalStorage value
  * @tparam A structured LocalStorage representation
  */
case class LocalStorageEntity[A <: LocalStorageLike](key: String, reader: String => A => A, writer: A => Option[Any]) {

}
