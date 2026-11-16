package com.kieronquinn.app.smartspacer.plugin.uber.model

import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification

/**
 *  Data pulled from an Uber trip notification. Only [title] is guaranteed: depending on the Uber
 *  version and Android version, the notification may be Uber's own custom layout (older Uber
 *  versions), an Android 16 Live Update (`Notification.ProgressStyle`), or a plain ongoing
 *  notification, so everything else is optional and the renderer draws whatever is available.
 */
data class NotificationData(
    val notification: StatusBarNotification,
    val icon: Icon,
    val title: String,
    val subtitle: String,
    /** Trip progress from 0 to 1, or null if the notification doesn't expose any */
    val progress: Float?,
    /** The vehicle drawn on the progress track */
    val progressCar: Bitmap?,
    val driver: Bitmap?,
    val car: Bitmap?,
    val expandedTitle: String?,
    val expandedSubtitle: String?,
    /** How confident we are this is the active trip notification, higher is better */
    val score: Int
)
