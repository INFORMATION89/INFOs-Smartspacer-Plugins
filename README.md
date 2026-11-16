# INFO's Smartspacer Plugins

Personal, vibecoded updates to [Smartspacer](https://github.com/KieronQuinn/Smartspacer) plugins, built with help from Claude. These are **not** official plugins and aren't affiliated with or supported by Kieron Quinn.

Based on [KieronQuinn/SmartspacerPlugins](https://github.com/KieronQuinn/SmartspacerPlugins). Only the plugins I've reworked are kept here, plus the shared code they need to build.

## Plugins

### Uber (`uber/`)

Shows your Uber ride in Smartspacer: pickup time, driver and car, and trip progress.

The original plugin stopped working once Uber redesigned its trip notifications around Android 16 Live Updates, because it only recognised Uber's old private notification layout. This version:

- reads the standard notification fields and Android 16 Live Update (`ProgressStyle`) data, so it works with Uber's current notifications
- still reads Uber's old layout when it's there
- shows only the notification that looks most like the active trip
- draws the trip card from whatever's available (driver photo, car, progress), and falls back to a text-only Target if it can't

## Building

GitHub Actions builds the APK on every push that touches the plugin. To run it by hand, open **Actions → Build Uber plugin → Run workflow**, then download the `uber-plugin` artifact.

Locally: `./gradlew :uber:assembleDebug` (the APK lands in `apks/`).

These are debug builds signed with a different key than the official plugins, so uninstall the official Uber plugin before installing.

### Debugging

Debug builds log every Uber notification they see:

```
adb logcat -s UberPlugin
```

## License

GPL-3.0, same as the original project. See [LICENSE](LICENSE). Original code © Kieron Quinn; changes in this repo are marked in the commit history.
