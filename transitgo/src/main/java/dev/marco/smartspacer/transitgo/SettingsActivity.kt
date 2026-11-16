package dev.marco.smartspacer.transitgo

import android.content.Intent
import android.os.Bundle
import android.provider.Settings as AndroidSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider

/**
 * Settings screen, opened from the Target's settings in Smartspacer or the app drawer.
 * Material 3 Expressive with wallpaper (dynamic) colors.
 */
class SettingsActivity : ComponentActivity() {

    private var accessGranted by mutableStateOf(false)
    private var settingsVersion by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { SettingsTheme { SettingsScreen() } }
    }

    override fun onResume() {
        super.onResume()
        // Refresh when coming back from the notification access screen
        accessGranted = NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun SettingsScreen() {
        val settings = remember { Settings(this) }
        val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Scaffold(
            modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            topBar = {
                LargeTopAppBar(
                    title = { Text(stringResource(R.string.target_label), fontWeight = FontWeight.SemiBold) },
                    scrollBehavior = scroll,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item { PreviewCard() }

                item { SectionHeader(R.string.section_card) }
                item {
                    ToggleRow(
                        Icons.Rounded.DirectionsBus, MaterialShapes.Cookie9Sided.toShape(),
                        R.string.setting_chip_title, R.string.setting_chip_summary,
                        settings.showRouteChip, position = Position.First
                    ) { settings.showRouteChip = it }
                }
                item {
                    ToggleRow(
                        Icons.AutoMirrored.Rounded.DirectionsWalk, MaterialShapes.Clover4Leaf.toShape(),
                        R.string.setting_step_title, R.string.setting_step_summary,
                        settings.showStepIcon, position = Position.Middle
                    ) { settings.showStepIcon = it }
                }
                item { ExtraRow(settings, MaterialShapes.Arch.toShape()) }

                item { SectionHeader(R.string.section_lock_screen) }
                item {
                    ToggleRow(
                        Icons.Rounded.VisibilityOff, MaterialShapes.Sunny.toShape(),
                        R.string.setting_hide_title, R.string.setting_hide_summary,
                        settings.hideNotification, position = Position.Single
                    ) { settings.hideNotification = it }
                }

                item { SectionHeader(R.string.section_permissions) }
                item {
                    LinkRow(
                        Icons.Rounded.NotificationsActive, MaterialShapes.Pill.toShape(),
                        R.string.setting_access,
                        if (accessGranted) R.string.setting_access_on else R.string.setting_access_off
                    ) { startActivity(Intent(AndroidSettings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
                }
            }
        }
    }

    /**
     * Mirrors the Smartspacer card exactly: title, then the card icon (same order as the
     * Target: route chip > step icon > Transit icon), the next step, and Transit's logo + name.
     */
    @Composable
    private fun PreviewCard() {
        val state = GoStore.state
        settingsVersion // re-read the settings when a toggle changes
        val settings = Settings(this)
        val onCard = MaterialTheme.colorScheme.onPrimaryContainer
        val glyph = remember { TransitIcon.glyph(this@SettingsActivity) }
        val appIcon = remember { TransitIcon.get(this@SettingsActivity) }
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    state?.title ?: stringResource(R.string.preview_idle_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = onCard
                )
                if (state != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                        val chip = state.chip?.takeIf { settings.showRouteChip }
                        val step = state.stepIcon?.takeIf { settings.showStepIcon }
                        when {
                            chip != null -> Image(chip.asImageBitmap(), state.routeName, Modifier.size(28.dp))
                            step != null -> Image(step.asImageBitmap(), null, Modifier.size(24.dp),
                                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(onCard))
                            appIcon != null -> Image(appIcon.asImageBitmap(), "Transit", Modifier.size(24.dp))
                        }
                        Spacer(Modifier.size(8.dp))
                        Text(
                            cardSubtitle(this@SettingsActivity, state, settings).orEmpty(),
                            style = MaterialTheme.typography.titleMedium, color = onCard,
                            maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        glyph?.let {
                            Spacer(Modifier.size(14.dp))
                            Image(it.asImageBitmap(), null, Modifier.size(22.dp),
                                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(onCard))
                            Spacer(Modifier.size(6.dp))
                            Text("Transit", style = MaterialTheme.typography.titleMedium, color = onCard)
                        }
                    }
                }
            }
        }
    }

    /** Arrival time / Time left / Off, as a segmented button under the row's text. */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ExtraRow(settings: Settings, iconShape: Shape) {
        var selected by remember { mutableStateOf(settings.extra) }
        val options = listOf(
            Settings.Extra.ARRIVAL to R.string.setting_extra_arrival,
            Settings.Extra.TIME_LEFT to R.string.setting_extra_time_left,
            Settings.Extra.NONE to R.string.setting_extra_none
        )
        Surface(
            shape = Position.Last.shape(),
            color = MaterialTheme.colorScheme.surfaceBright,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.secondaryContainer, iconShape)
                    ) {
                        Icon(Icons.Rounded.Schedule, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(22.dp))
                    }
                    Column(Modifier.weight(1f).padding(start = 16.dp)) {
                        Text(stringResource(R.string.setting_extra_title), style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(R.string.setting_extra_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                androidx.compose.material3.SingleChoiceSegmentedButtonRow(
                    Modifier.fillMaxWidth().padding(start = 60.dp, top = 12.dp)
                ) {
                    options.forEachIndexed { i, (value, label) ->
                        SegmentedButton(
                            selected = selected == value,
                            onClick = {
                                selected = value
                                settings.extra = value
                                settingsVersion++
                                SmartspacerTargetProvider.notifyChange(this@SettingsActivity, TransitGoTarget::class.java)
                            },
                            shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(i, options.size)
                        ) { Text(stringResource(label), maxLines = 1) }
                    }
                }
            }
        }
    }

    @Composable
    private fun SectionHeader(text: Int) = Text(
        stringResource(text),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
    )

    private enum class Position { First, Middle, Last, Single }

    // Grouped rows: big outer corners, small inner ones (Android 16 settings style)
    private fun Position.shape(): Shape {
        val big = 24.dp; val small = 6.dp
        return when (this) {
            Position.First -> RoundedCornerShape(big, big, small, small)
            Position.Middle -> RoundedCornerShape(small)
            Position.Last -> RoundedCornerShape(small, small, big, big)
            Position.Single -> RoundedCornerShape(big)
        }
    }

    @Composable
    private fun ToggleRow(
        icon: ImageVector, iconShape: Shape, title: Int, summary: Int,
        initial: Boolean, position: Position, onChange: (Boolean) -> Unit
    ) {
        var checked by remember { mutableStateOf(initial) }
        val toggle = { value: Boolean ->
            checked = value
            onChange(value)
            settingsVersion++
            // Redraw the card with the new setting
            SmartspacerTargetProvider.notifyChange(this, TransitGoTarget::class.java)
        }
        SettingRow(icon, iconShape, title, summary, position, onClick = { toggle(!checked) }) {
            Switch(
                checked = checked,
                onCheckedChange = toggle,
                thumbContent = if (checked) {
                    { Icon(Icons.Rounded.Check, null, Modifier.size(SwitchDefaults.IconSize)) }
                } else null
            )
        }
    }

    @Composable
    private fun LinkRow(icon: ImageVector, iconShape: Shape, title: Int, summary: Int, onClick: () -> Unit) =
        SettingRow(icon, iconShape, title, summary, Position.Single, onClick) {
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

    @Composable
    private fun SettingRow(
        icon: ImageVector, iconShape: Shape, title: Int, summary: Int, position: Position,
        onClick: () -> Unit, trailing: @Composable () -> Unit
    ) {
        Surface(
            shape = position.shape(),
            color = MaterialTheme.colorScheme.surfaceBright,
            modifier = Modifier.fillMaxWidth().clip(position.shape()).clickable(onClick = onClick)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.secondaryContainer, iconShape)
                ) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(22.dp))
                }
                Column(Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(summary),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                trailing()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val colors = if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    MaterialExpressiveTheme(
        colorScheme = colors,
        motionScheme = MotionScheme.expressive(),
        typography = googleSansTypography(),
        content = content
    )
}

/** Google Sans Text, the font Smartspacer and its other plugins use in their settings. */
private val GoogleSansText = androidx.compose.ui.text.font.FontFamily(
    androidx.compose.ui.text.font.Font(R.font.google_sans_text_regular, FontWeight.Normal),
    androidx.compose.ui.text.font.Font(R.font.google_sans_text_medium, FontWeight.Medium),
    androidx.compose.ui.text.font.Font(R.font.google_sans_text_medium, FontWeight.SemiBold),
    androidx.compose.ui.text.font.Font(R.font.google_sans_text_bold, FontWeight.Bold)
)

/** The default Material 3 type scale with every style switched to Google Sans Text. */
private fun googleSansTypography(): androidx.compose.material3.Typography {
    val t = androidx.compose.material3.Typography()
    fun androidx.compose.ui.text.TextStyle.gs() = copy(fontFamily = GoogleSansText)
    return androidx.compose.material3.Typography(
        displayLarge = t.displayLarge.gs(), displayMedium = t.displayMedium.gs(), displaySmall = t.displaySmall.gs(),
        headlineLarge = t.headlineLarge.gs(), headlineMedium = t.headlineMedium.gs(), headlineSmall = t.headlineSmall.gs(),
        titleLarge = t.titleLarge.gs(), titleMedium = t.titleMedium.gs(), titleSmall = t.titleSmall.gs(),
        bodyLarge = t.bodyLarge.gs(), bodyMedium = t.bodyMedium.gs(), bodySmall = t.bodySmall.gs(),
        labelLarge = t.labelLarge.gs(), labelMedium = t.labelMedium.gs(), labelSmall = t.labelSmall.gs()
    )
}
