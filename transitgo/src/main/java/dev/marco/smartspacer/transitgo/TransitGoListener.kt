package dev.marco.smartspacer.transitgo

import android.app.Notification
import android.app.PendingIntent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider

/** Latest GO-mode state, or null when no trip is active. */
data class GoState(
    val title: String,
    val text: String?,
    val contentIntent: PendingIntent?,
    val chip: android.graphics.Bitmap? = null,
    val routeName: String? = null,
    val stepIcon: android.graphics.Bitmap? = null,
    /** Key of Transit's GO notification, so Native Smartspace can hide it on the lock screen */
    val notificationKey: String? = null
)

object GoStore {
    // Compose snapshot state, so the settings screen's preview updates live with the trip
    var state by androidx.compose.runtime.mutableStateOf<GoState?>(null)
}

class TransitGoListener : NotificationListenerService() {

    private var dumped = false

    override fun onListenerConnected() {
        // Pick up a trip that was already running before we connected
        activeNotifications?.forEach { if (isGo(it)) update(it) }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (isGo(sbn)) update(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (isGo(sbn)) {
            GoStore.state = null
            notifyTarget()
        }
    }

    // Transit posts several notifications on go_mode: an empty group summary, one-off
    // alerts (AUTO_CANCEL), and the live trip, which is the foreground-service one.
    private fun isGo(sbn: StatusBarNotification): Boolean {
        val n = sbn.notification
        return sbn.packageName == TRANSIT_PACKAGE &&
            n.channelId == GO_CHANNEL &&
            n.flags and Notification.FLAG_GROUP_SUMMARY == 0 &&
            n.flags and Notification.FLAG_FOREGROUND_SERVICE != 0
    }

    private fun update(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: "Transit GO"
        // Observed: title = "Sal en 194 minutos", subText = "en 204 minutos", text = null.
        // The detailed layout is custom RemoteViews, so subText is the best plain-text extra.
        val text = (extras.getCharSequence(Notification.EXTRA_TEXT)
            ?: extras.getCharSequence(Notification.EXTRA_SUB_TEXT))?.toString()

        // Debug builds: dump the custom layout once per process (adb logcat -s TransitGoDump)
        if (!dumped && applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            dumped = true
            LayoutDumper.dump(this, sbn.notification.bigContentView, "big")
        }

        // The route chip (e.g. green "57-A") and step icon live in the expanded custom layout
        val r = ChipRenderer.render(this, sbn.notification.bigContentView)
        Log.d(TAG, "update: title=$title text=$text route=${r?.routeName} chip=${r?.chip != null} step=${r?.stepIcon != null}")

        GoStore.state = GoState(
            title, text, sbn.notification.contentIntent,
            r?.chip, r?.routeName, r?.stepIcon, sbn.key
        )
        notifyTarget()
    }

    private fun notifyTarget() =
        SmartspacerTargetProvider.notifyChange(this, TransitGoTarget::class.java)

    companion object {
        const val TAG = "TransitGo"
        const val TRANSIT_PACKAGE = "com.thetransitapp.droid"
        const val GO_CHANNEL = "go_mode"
    }
}
