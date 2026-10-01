package com.memorylane.tv

/** Navigation routes. */
object Routes {
    const val GRID = "grid"
    const val PHOTO = "photo/{photoId}"
    fun photo(photoId: String) = "photo/$photoId"
}
