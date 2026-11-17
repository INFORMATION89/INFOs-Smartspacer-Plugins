# (Self-)Updated Smartspacer Plugins

Official [Smartspacer](https://github.com/KieronQuinn/Smartspacer) plugins that stopped working, updated by me with help from Claude so they work again with today's apps. Based on [KieronQuinn/SmartspacerPlugins](https://github.com/KieronQuinn/SmartspacerPlugins), keeping only the plugins I've updated plus the shared code they need; not official, and not affiliated with or supported by Kieron Quinn.

**Uber** ([`uber/`](uber)) shows your ride: pickup time, driver, car and trip progress. The original broke when Uber moved its trip notifications to Android 16 Live Updates; this version reads those (and Uber's old layout when it's still there). Download the APK from the [latest release](../../releases/latest), and uninstall the official Uber plugin first, since this one is signed with a different key. If Google Play Protect blocks it, install it with `adb install` from a computer, or briefly turn off **Scan apps with Play Protect** in the Play Store. To build it yourself: `./gradlew :uber:assembleDebug` with JDK 17+ and the Android 37 SDK.

My new plugins live in their own repos, like **[Transit GO for Smartspacer](https://github.com/INFORMATION89/Transit-GO-Smartspacer)** for your Transit GO trip.

Licensed under [GPL-3.0](LICENSE), like the original. Original code © Kieron Quinn; my changes are in the commit history.
