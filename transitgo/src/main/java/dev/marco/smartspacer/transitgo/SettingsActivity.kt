package dev.marco.smartspacer.transitgo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings as AndroidSettings
import android.util.TypedValue
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider

/**
 * Settings screen. Opened from Smartspacer (the Target's settings) or the app drawer.
 * Built in code to keep the plugin free of extra UI libraries.
 */
class SettingsActivity : Activity() {

    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = Settings(this)
        val pad = dp(20)

        val list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }

        list.addView(TextView(this).apply {
            text = getString(R.string.settings_title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            setPadding(0, dp(24), 0, dp(16))
        })

        list.addView(toggle(
            R.string.setting_hide_title, R.string.setting_hide_summary,
            settings.hideNotification
        ) { settings.hideNotification = it })

        list.addView(toggle(
            R.string.setting_chip_title, R.string.setting_chip_summary,
            settings.showRouteChip
        ) { settings.showRouteChip = it })

        list.addView(toggle(
            R.string.setting_step_title, R.string.setting_step_summary,
            settings.showStepIcon
        ) { settings.showStepIcon = it })

        list.addView(Button(this).apply {
            text = getString(R.string.setting_access)
            setOnClickListener {
                startActivity(Intent(AndroidSettings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply { topMargin = dp(16) })

        setContentView(ScrollView(this).apply {
            fitsSystemWindows = true
            addView(list, MATCH_PARENT, WRAP_CONTENT)
        })
    }

    private fun toggle(title: Int, summary: Int, checked: Boolean, onChange: (Boolean) -> Unit) =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(12), 0, dp(12))
            val texts = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(TextView(context).apply {
                    text = getString(title)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                })
                addView(TextView(context).apply {
                    text = getString(summary)
                    alpha = 0.7f
                })
            }
            addView(texts, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
            addView(Switch(context).apply {
                isChecked = checked
                setOnCheckedChangeListener { _, value ->
                    onChange(value)
                    // Redraw the card with the new setting
                    SmartspacerTargetProvider.notifyChange(this@SettingsActivity, TransitGoTarget::class.java)
                }
            })
        }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
