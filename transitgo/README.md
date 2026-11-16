# Transit GO for Smartspacer

Shows your active [Transit](https://transitapp.com) **GO** trip as a Smartspacer Target, on your home screen and lock screen.

The card shows what Transit's GO notification says, for example:

- **Sal en 164 minutos** (title: when to leave or the current instruction)
- the route chip, e.g. a green **57-A**, then **en 174 minutos** (the next step)

Tapping the card opens Transit on your trip.

## Setup

1. Install the APK (see the [main README](../README.md#installing)).
2. Turn on **Notification access** for *Transit GO for Smartspacer* (Settings → Notifications → Device & app notifications, or the button in the plugin's settings).
3. In Smartspacer, add the **Transit GO** Target.
4. Start GO in Transit. The card appears while the trip is running and disappears when it ends.

## Settings

Open them from the Target's settings in Smartspacer, or from *Transit GO for Smartspacer* in the app drawer. Smartspacer can't show a plugin's settings inside its own screens, so like other plugins this opens the plugin's own screen.

| Setting | Default | What it does |
|---|---|---|
| Show route chip | On | Uses Transit's colored route chip (like 57-A) as the card icon |
| Show step icon | On | When there's no route chip (e.g. walking), uses Transit's icon for the current step, tinted to match Smartspacer |
| Hide notification on lock screen | On | Hides Transit's GO notification on the lock screen while this card is the first one showing |
| Notification access | | Shows whether access is on and opens the system screen |

The top of the screen shows a live preview of what the card is showing right now.

If both icon settings are off, or Transit has neither, the card uses the plugin's bus icon.

## How it works

**Reading the trip.** Transit posts several notifications on its `go_mode` channel: an empty group summary, one-off alerts, and the live trip. The plugin's notification listener (`TransitGoListener`) only takes the live trip: the foreground-service notification that isn't a group summary. Its title and sub text become the card's title and subtitle.

**Route chip and step icon.** Transit draws the detailed trip view as a custom notification layout (`RemoteViews`), so the route and icons aren't in the normal notification fields. `ChipRenderer` rebuilds that layout inside the plugin and finds:

| Transit view id | What it is |
|---|---|
| `floatingServiceName` | Route chip: colored pill with the route name |
| `text` (inside the chip) | Route name, e.g. "57-A" |
| `image` | Step icon: clock while waiting, walk, ride... |
| `main_instruction` / `next_instruction` | Same text as the title / sub text |

Each one is drawn at 3× into a square bitmap (Smartspacer squeezes icons into a square, so a wide image would look distorted).

**Hiding the notification.** Same approach as the Uber plugin: the card sets `sourceNotificationKey` to the GO notification's key. With Smartspacer's Native Smartspace, the lock screen hides that notification while the card is first. Nothing is cancelled or dismissed, so Transit's trip keeps running. Swiping the card away does nothing to the notification, because cancelling Transit's foreground service could break the trip.

## Limitations

- The route chip and step icon depend on Transit's internal view ids. A Transit update can rename them; the card then falls back to the bus icon until the ids are updated in `ChipRenderer.kt`.
- Only tested with Transit 6.3.7 on a Pixel 7 (Android 17), in Spanish.
- The hiding only works with Native Smartspace and only on the lock screen; the notification always stays in the shade.
- The plugin has its own notification listener, so it needs its own notification access (unlike the Uber plugin, which uses Smartspacer's).

## Debugging

```
adb logcat -s TransitGo        # one line per update: title, text, route, chip/step found
adb logcat -s TransitGoDump    # debug builds: Transit's full layout tree, once per start
```

Debug builds also save PNGs of Transit's layout and its images to the app's `files/dump/` folder:

```
adb shell run-as dev.marco.smartspacer.transitgo ls files/dump
```

## Files

| File | Purpose |
|---|---|
| `TransitGoListener.kt` | Notification listener: picks the live GO notification and stores the trip state |
| `ChipRenderer.kt` | Rebuilds Transit's layout and draws the route chip and step icon |
| `TransitGoTarget.kt` | The Smartspacer Target: builds the card from the trip state and settings |
| `Settings.kt` | The three settings, stored in SharedPreferences |
| `SettingsActivity.kt` | Compose settings screen (Material 3 Expressive) |
| `LayoutDumper.kt` | Debug-only dump of Transit's layout |
