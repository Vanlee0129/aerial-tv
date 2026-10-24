# Using this fork as the daily launcher

The `daily` flavor enables HOME, boot, accessibility guard and revival services. It upgrades the
locally signed preview in place (`app.orchard.tv.preview`) while showing the normal **Aerial**
name. Keeping this application ID preserves preview data and its signing continuity. Its sync
account type is isolated from the upstream app; its phone page stays on port 8791. Official
update checks/install flows remain disabled because this fork has its own signature and identity.

Build with JDK 17 and SDK 36:

```sh
./gradlew :app:assembleDailyRelease :app:testDailyDebugUnitTest :app:testPreviewDebugUnitTest
```

The identity and icon decode tests cover HOME/service registration, independent sync account,
preview isolation, and bounded decoding of every shipped icon mapping.

## Migration

Back up the upstream APK before removing it. Only after the daily APK has installed and launched,
uninstall upstream `app.orchard.tv` with `pm uninstall -k --user 0`: this removes the old app from
the active user but keeps its private data for recovery. This does **not** migrate those settings
into the fork; the fork retains the existing preview's settings. Never uninstall any system launcher.

Grant the daily package WRITE_SECURE_SETTINGS and SYSTEM_ALERT_WINDOW, enable its ConfigReceiver
`keep_adb` option, and use the full class name for every component:
`app.orchard.tv.preview/app.orchard.tv.guard.ConfigReceiver`, not a shorthand based on the new ID.
Preserve unrelated accessibility services. HOME takeover may use the firmware HOME role or the
fork's HomeGuard. Verify both HOME and reboot before claiming boot takeover.

Old APK/data recovery: reinstall the backed-up original signed APK and restore its guard settings
from the local migration records. Stop the daily guardian before restoring the original guardian
so two launchers do not compete for HOME. Private migration records and original APK belong in
ignored `local/migration/`; do not publish them.

## Release validation — 2026-10-24

- Version code 51 / 1.46-fork; optimized release built and compiled with `speed` on Xiaomi MiTV-MFTR0.
- 23 daily and 25 preview unit tests passed. Full lint retains 10 upstream errors; no new errors
  versus the previous preview report. One new non-blocking `UseKtx` style warning remains.
- Six original generated PNGs, 1672×941 pixels each, embedded offline; eight package mappings.
  Confirmed the TV's actual media explorer package and its final rendered tile.
- Official `app.orchard.tv` uninstalled from user 0 with `-k`; its signed APK saved locally.
  Only the fork remains in the installed package list. The stock Xiaomi launcher was not removed
  or disabled. The old app's private data is retained for recovery, not copied into the fork.
- Final APK SHA-256: `abc7dba8b49647d01e0c6bbe8d769deb4a733e7a5d321524370e37cad16f0906`.

### Device acceptance

After an actual reboot, the fork's MainActivity became foreground without injecting a launch
or HOME event. ADB reconnected and `adb_enabled` remained `1`; the fork's HomeGuard was enabled.
The firmware HOME resolver still reports Xiaomi's launcher, so takeover is through HomeGuard.
Opening Weather from its new tile and pressing HOME returned to the fork. The original system
screensaver component was unchanged. No Aerial crash appeared in the captured crash buffer.
Raw records and the final tile sheet are saved locally under `local/migration/`.
