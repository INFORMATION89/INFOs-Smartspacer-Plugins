package dev.marco.smartspacer.transitgo

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon as AndroidIcon
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate

class TransitGoTarget : SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val state = GoStore.state ?: return emptyList()
        val ctx = provideContext()
        val settings = Settings(ctx)
        val launch = ctx.packageManager
            .getLaunchIntentForPackage(TransitGoListener.TRANSIT_PACKAGE)

        // Route chip (full color) > step icon (tinted like Smartspacer's text)
        //   > Transit's app icon (full color) > bus glyph
        val icon = state.chip?.takeIf { settings.showRouteChip }
            ?.let { Icon(AndroidIcon.createWithBitmap(it), contentDescription = state.routeName, shouldTint = false) }
            ?: state.stepIcon?.takeIf { settings.showStepIcon }
                ?.let { Icon(AndroidIcon.createWithBitmap(it), shouldTint = true) }
            ?: TransitIcon.get(ctx)
                ?.let { Icon(AndroidIcon.createWithBitmap(it), contentDescription = "Transit", shouldTint = false) }
            ?: Icon(AndroidIcon.createWithResource(ctx, R.drawable.ic_transit_go))

        // Small Transit app icon next to the subtitle (the "supplemental" spot, where weather
        // usually shows), so it's clear the info comes from Transit
        val source = TransitIcon.get(ctx)?.let {
            ComplicationTemplate.Basic(
                "transit_go_source_$smartspacerId",
                Icon(AndroidIcon.createWithBitmap(it), contentDescription = "Transit", shouldTint = false),
                Text(" "), // icon only
                TapAction(
                    pendingIntent = state.contentIntent,
                    intent = if (state.contentIntent == null) launch else null
                )
            ).create()
        }

        return listOf(
            TargetTemplate.Basic(
                "transit_go_$smartspacerId",
                ComponentName(ctx, TransitGoTarget::class.java),
                SmartspaceTarget.FEATURE_UNDEFINED,
                Text(state.title),
                state.text?.let { Text(it) },
                icon,
                TapAction(
                    pendingIntent = state.contentIntent,
                    intent = if (state.contentIntent == null) launch else null
                ),
                source
            ).create().apply {
                // Same trick as the Uber plugin: with Native Smartspace, the lock screen hides
                // this notification while our card is the first one showing. Nothing is cancelled.
                if (settings.hideNotification) sourceNotificationKey = state.notificationKey
            }
        )
    }

    override fun getConfig(smartspacerId: String?): Config {
        val ctx = provideContext()
        return Config(
            label = ctx.getString(R.string.target_label),
            description = ctx.getString(R.string.target_description),
            icon = AndroidIcon.createWithResource(ctx, R.drawable.ic_transit_go),
            configActivity = Intent(ctx, SettingsActivity::class.java)
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean = false
}
