package com.kieronquinn.app.smartspacer.plugin.uber.utils.extensions

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader

/**
 *  Pads this bitmap into a square. Doesn't recycle the source: it may still be referenced
 *  elsewhere (the original version recycled it, which could corrupt a shared bitmap).
 */
fun Bitmap.makeSquare(): Bitmap {
    val maxSize = maxOf(width, height)
    return Bitmap.createBitmap(maxSize, maxSize, Bitmap.Config.ARGB_8888).apply {
        val canvas = Canvas(this)
        canvas.drawBitmap(
            this@makeSquare,
            (maxSize - this@makeSquare.width) / 2f,
            (maxSize - this@makeSquare.height) / 2f,
            null
        )
    }
}

/**
 *  Centre-crops this bitmap into a circle, used for the driver photo when it comes from the
 *  notification's large icon (Uber's old custom layout already supplied it pre-cropped).
 */
fun Bitmap.circleCrop(): Bitmap {
    val size = minOf(width, height)
    val left = (width - size) / 2f
    val top = (height - size) / 2f
    return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
        val canvas = Canvas(this)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = BitmapShader(this@circleCrop, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                .apply {
                    setLocalMatrix(android.graphics.Matrix().apply { setTranslate(-left, -top) })
                }
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    }
}

fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
    return if (maxHeight > 0 && maxWidth > 0) {
        val width = this.width
        val height = this.height
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        Bitmap.createScaledBitmap(
            this, finalWidth.coerceAtLeast(1), finalHeight.coerceAtLeast(1), true
        )
    } else this
}
