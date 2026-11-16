# INFO's Smartspacer Plugins

Personal, vibecoded [Smartspacer](https://github.com/KieronQuinn/Smartspacer) plugins, built with help from Claude: reworked versions of official plugins. These are **not** official plugins and aren't affiliated with or supported by Kieron Quinn.

Based on [KieronQuinn/SmartspacerPlugins](https://github.com/KieronQuinn/SmartspacerPlugins). Only the plugins I've reworked are kept here, plus the shared code they need to build.

| Plugin | Where | What it shows | Status |
|---|---|---|---|
| Uber | [`uber/`](uber) | Your Uber ride: pickup time, driver, car, progress | Reworked official plugin |
| Transit GO | [Transit-GO-Smartspacer](https://github.com/INFORMATION89/Transit-GO-Smartspacer) | Your active Transit GO trip, with the route chip | New plugin, now in its own repo |

## Plugins

### Uber (`uber/`)

Shows your Uber ride in Smartspacer: pickup time, driver and car, and trip progress.

The original plugin stopped working once Uber redesigned its trip notifications around Android 16 Live Updates, because it only recognised Uber's old private notification layout. This version:

- reads the standard notification fields and Android 16 Live Update (`ProgressStyle`) data, so it works with Uber's current notifications
- still reads Uber's old layout when it's there
- shows only the notification that looks most like the active trip
- draws the trip card from whatever's available (driver photo, car, progress), and falls back to a text-only Target if it can't

### Transit GO

Moved to its own repo: **[INFORMATION89/Transit-GO-Smartspacer](https://github.com/INFORMATION89/Transit-GO-Smartspacer)**, with releases and its full README. Its history up to the move is still in this repo's git log (`transitgo/`).

## Installing

Download the APK from the latest run in the **Actions** tab (artifact `uber-plugin`), or build it yourself (below), then install it on your phone.

These are debug builds signed with a different key than the official plugins, so uninstall the official Uber plugin before installing this one.

## Building

GitHub Actions builds the plugin on every push that touches it. To run it by hand, open **Actions → Build Uber plugin → Run workflow**, then download the artifact.

Locally, with JDK 17+ and the Android SDK (Android 37 platform):

```
./gradlew :uber:assembleDebug
```

The APK lands in `apks/`. Delete the old APK there before building again, or packaging fails with "already contains entry".

Toolchain: Gradle 9.4, Android Gradle Plugin 9.2, Kotlin 2.4, compile SDK 37.

### Debugging

Debug builds log every Uber notification they see:

```
adb logcat -s UberPlugin
```

## License

GPL-3.0, same as the original project. See [LICENSE](LICENSE). Original code © Kieron Quinn; changes in this repo are marked in the commit history.
