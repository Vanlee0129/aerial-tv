# Performance validation and side-by-side preview

The `preview` flavor is for comparing this fork on a TV that already has official Aerial installed.
It has package `app.orchard.tv.preview`, label **Aerial Preview**, and phone configuration port 8791.
It declares no HOME activity, boot receiver, accessibility guardian, notification revival listener,
or sync/account services. Upstream update checks are disabled. HOME continues to open the installed
launcher. Preview has its own settings, photos and icon layout; it does not migrate upstream data.

## Build

Use JDK 17 and Android SDK platform 36 / build-tools 35.0.0. Set `JAVA_HOME`, and set `sdk.dir` in
ignored `local.properties` (or `ANDROID_HOME`).

```sh
./gradlew :app:assemblePreviewRelease :app:testPreviewDebugUnitTest
./gradlew :app:lintPreviewRelease
```

Only release builds are suitable for timing. Keep the local signing key: future preview APKs need
that same key for an in-place update. Do not uninstall official Aerial to work around its signature.

```sh
adb -s "$TV_SERIAL" install -r app/build/outputs/apk/preview/release/app-preview-release.apk
adb -s "$TV_SERIAL" shell cmd package compile -m speed -f app.orchard.tv.preview
adb -s "$TV_SERIAL" shell am start -W -n app.orchard.tv.preview/app.orchard.tv.MainActivity
```

Do not run the upstream `tools/install.sh` for preview: it is designed to download and configure the
official package. Preview deliberately does not replace the default desktop. Press HOME to return
to official Aerial. The original package, its data and permissions are independent.

## A/B procedure

1. Build preview from the instrumentation baseline commit recorded in the results, install it,
   let its wallpaper/assets finish loading, and verify its HOME screenshot before sending keys.
2. Run `python3 tools/benchmark-preview.py --serial "$TV_SERIAL" --out local/bench/baseline`.
3. Install the optimized preview **over the same package without clearing data**; keep the same
   wallpaper, tile arrangement, quality settings, refresh rate and compilation mode.
4. Run the same script into a new output directory. Compare scenario distributions, not just means.
5. Inspect screenshots, foreground checks, crash buffer, memory and idle captures. Test app launch/
   return, long-press, folder, and screensaver separately. Repeat measurements when conditions differ.

The script restarts only preview. It records 10 process-cold starts (disk/OS caches stay warm),
5 navigation runs, 5 Control Center runs, and a 10-second idle sample. It sends directional
keys inside panels, not confirmation on settings or HDMI sources. Stop if another app gains focus.

`TotalTime` is Android's launch metric, not network/ADB round-trip latency. The separate diagnostic
marker measures time after Compose content setup until the loaded home reaches its next frame; it
excludes initialization before content setup. `FrameCompleted - IntendedVsync` measures frame
completion latency; `GpuCompleted - SwapBuffers` is reported separately. The percentage above
16.67ms is a 60Hz budget comparison, **not** Android's own jank/deadline metric. Known AOSP special flag bits (0–3) and unfinished fences are excluded. This Xiaomi firmware
sets undocumented bit 16 on ordinary frames; it is retained and counted explicitly. Raw-timestamp
percentiles are therefore diagnostic, not a claim of platform-conformant frame flags. The report
also keeps Android’s own global histogram/deadline counters (not duplicated per-window counters). The ring buffer is finite, so individual runs are kept short.

Raw files include device properties and screenshots and belong in ignored `local/`. Publish only
reviewed aggregate results and non-sensitive screenshots. Do not compare the installed official
app's lifetime counters with controlled preview scenarios and claim an improvement.

## Design decisions

- Preserve the 960×540dp canvas, existing font licensing and tvOS-inspired hierarchy.
- Keep focus crisp, critically damped and interruptible; keep the high-contrast option.
- Defer animated state reads to draw/layer phases and rasterize focused faces off the UI thread.
- Collapse Control Center's two black scrims into one; dim its static snapshot in the image pass.
- Retain the existing shader fallback for older GPUs; avoid introducing real-time full-screen blur.
- Remove redundant application scans, retain locale/style/size invalidation, and avoid HDMI/
  brightness queries on volume broadcasts.

References: [Apple tvOS design](https://developer.apple.com/design/human-interface-guidelines/designing-for-tvos/),
[Compose performance](https://developer.android.com/develop/ui/compose/performance/bestpractices).

Frame flag reference: [Android 14 FrameInfo.h](https://github.com/aosp-mirror/platform_frameworks_base/blob/android14-release/libs/hwui/FrameInfo.h). Vendor bit 16 has no definition there.
