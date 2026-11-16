# INFO's Smartspacer Plugins

Personal, vibecoded [Smartspacer](https://github.com/KieronQuinn/Smartspacer) plugins, built with help from Claude: reworked versions of official plugins, and new ones. These are **not** official plugins and aren't affiliated with or supported by Kieron Quinn.

Based on [KieronQuinn/SmartspacerPlugins](https://github.com/KieronQuinn/SmartspacerPlugins). Only the plugins I've reworked or written are kept here, plus the shared code they need to build.

| Plugin | Folder | What it shows | Status |
|---|---|---|---|
| Uber | [`uber/`](uber) | Your Uber ride: pickup time, driver, car, progress | Reworked official plugin |
| Transit GO | [`transitgo/`](transitgo) | Your active Transit GO trip, with the route chip | New plugin |

## Plugins

### Uber (`uber/`)

Shows your Uber ride in Smartspacer: pickup time, driver and car, and trip progress.

The original plugin stopped working once Uber redesigned its trip notifications around Android 16 Live Updates, because it only recognised Uber's old private notification layout. This version:

- reads the standard notification fields and Android 16 Live Update (`ProgressStyle`) data, so it works with Uber's current notifications
- still reads Uber's old layout when it's there
- shows only the notification that looks most like the active trip
- draws the trip card from whatever's available (driver photo, car, progress), and falls back to a text-only Target if it can't

### Transit GO (`transitgo/`)

Shows your active [Transit](https://transitapp.com) GO trip in Smartspacer: "leave in N minutes", the next step, and Transit's colored route chip (like a green "57-A"). Tapping the card opens Transit.

- reads Transit's GO notification (the live trip on the `go_mode` channel) with its own notification listener
- copies the route chip and step icon (wait, walk, ride) from Transit's notification layout
- can hide Transit's GO notification on the lock screen while its card is first, like the Uber plugin (needs Native Smartspace; nothing is cancelled)
- Material 3 Expressive settings screen with wallpaper colors and a live preview of the card

Full details, setup and how it works: [`transitgo/README.md`](transitgo/README.md).

## Installing

Download the APK from the latest run in the **Actions** tab (artifacts `uber-plugin` and `transitgo-plugin`), or build it yourself (below), then install it on your phone.

These are debug builds signed with a different key than the official plugins, so uninstall the official Uber plugin before installing this one.

## Building

GitHub Actions builds each plugin on every push that touches it. To run one by hand, open **Actions → Build Uber plugin** (or **Build Transit GO plugin**) **→ Run workflow**, then download the artifact.

Locally, with JDK 17+ and the Android SDK (Android 37 platform):

```
./gradlew :uber:assembleDebug
./gradlew :transitgo:assembleDebug
```

The APKs land in `apks/`. Delete the old APK there before building the same plugin again, or packaging fails with "already contains entry".

Toolchain: Gradle 9.4, Android Gradle Plugin 9.2, Kotlin 2.4, compile SDK 37. Transit GO also uses Compose with Material 3 `1.5.0-rc01` (the Expressive APIs aren't in a stable release yet).

### Debugging

Debug builds log what they read:

```
adb logcat -s UberPlugin
adb logcat -s TransitGo TransitGoDump
```

## License

GPL-3.0, same as the original project. See [LICENSE](LICENSE). Original code © Kieron Quinn; changes in this repo are marked in the commit history.
