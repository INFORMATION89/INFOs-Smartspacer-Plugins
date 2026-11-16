package dev.marco.smartspacer.transitgo

import android.content.Context

/** Plugin settings, stored in SharedPreferences. */
class Settings(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /** Hide Transit's GO notification on the lock screen (needs Native Smartspace). */
    var hideNotification: Boolean
        get() = prefs.getBoolean(KEY_HIDE, true)
        set(value) = prefs.edit().putBoolean(KEY_HIDE, value).apply()

    /** Use Transit's route chip (e.g. green "57-A") as the card icon. */
    var showRouteChip: Boolean
        get() = prefs.getBoolean(KEY_CHIP, true)
        set(value) = prefs.edit().putBoolean(KEY_CHIP, value).apply()

    /** When there's no route chip, use Transit's step icon (wait, walk, ride...). */
    var showStepIcon: Boolean
        get() = prefs.getBoolean(KEY_STEP, true)
        set(value) = prefs.edit().putBoolean(KEY_STEP, value).apply()

    companion object {
        private const val KEY_HIDE = "hide_notification"
        private const val KEY_CHIP = "show_route_chip"
        private const val KEY_STEP = "show_step_icon"
    }
}
