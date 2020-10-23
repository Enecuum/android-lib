package com.enecuum.app.utils

import android.content.Context
import android.graphics.Bitmap
import com.enecuum.app.extensions.invert

object IconGenerator {
    fun generate(context: Context, data: String, destWidthRes: Int): Bitmap? {
        val identicon = IdenticonGenerator.generate(data.toLowerCase())
        val destWidth = context.resources.getDimension(destWidthRes).toInt()
        return Bitmap.createScaledBitmap(identicon, destWidth, destWidth, false).invert()
    }
}