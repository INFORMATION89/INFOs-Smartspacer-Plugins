package dev.marco.smartspacer.transitgo

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap

/**
 * Transit's own app icon, loaded from the installed Transit app at runtime
 * (nothing of Transit's is bundled in this plugin). Null if Transit isn't installed.
 */
object TransitIcon {
    @Volatile private var cached: Bitmap? = null

    fun get(context: Context, size: Int = 192): Bitmap? {
        cached?.let { return it }
        return try {
            context.packageManager.getApplicationIcon(TransitGoListener.TRANSIT_PACKAGE)
                .toBitmap(size, size)
                .copy(Bitmap.Config.ARGB_8888, false)
                .also { cached = it }
        } catch (e: Exception) {
            null
        }
    }
}
