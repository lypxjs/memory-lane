package com.memorylane.tv

/** Navigation routes. */
object Routes {
    const val GRID = "grid"
    const val PHOTO = "photo/{photoId}"
    const val RECORD = "record/{photoId}"
    const val FAMILY = "family"
    fun photo(photoId: String) = "photo/$photoId"
    fun record(photoId: String) = "record/$photoId"
}
