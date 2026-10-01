package com.memorylane.tv.data

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/** Loads bundled sample photos at a sane size for base64 upload / on-screen use. */
fun loadAssetBitmap(assets: AssetManager, path: String, maxDim: Int): Bitmap? {
    return try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        assets.open(path).use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / sample > maxDim || bounds.outHeight / sample > maxDim) {
            sample *= 2
        }
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        assets.open(path).use { BitmapFactory.decodeStream(it, null, opts) }
    } catch (e: Exception) {
        null
    }
}
