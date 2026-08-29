# Local emulator setup

One AVD is used both for clicking through the app by hand and for instrumented tests. The settings below speed it up
without taking anything away from manual use. All of them are picked up when the emulator is launched from Android
Studio.

## 1. System image — `google_apis`

Pick `google_apis` (x86_64, API 35) in the wizard. Do not use an ATD (Automated Test Device) image locally: ATD builds
are made for automated testing only — SystemUI, the launcher, the Settings app and the AOSP keyboard are stripped out
and hardware rendering is disabled, so there is nothing to click and nothing to type with. CI uses `aosp_atd` because it
runs headless and never touches any of that.

A lighter `default` (plain AOSP, no Google Play services) also works and is clickable, but then
`CredentialManager` has no provider: `AccountManager` swallows the exception, so saving and autofilling a password
silently does nothing. Stay on `google_apis` to be able to check that flow by hand.

## 2. AVD Manager

Device Manager → Create/Edit AVD → Show Advanced Settings. Only two fields differ from the defaults:

| Section              | Field                 | Value           | Note                                                           |
|----------------------|-----------------------|-----------------|----------------------------------------------------------------|
| Camera               | Front                 | `None`          | already the default                                            |
| Camera               | Rear                  | `None`          | change from `VirtualScene`, it renders a 3D scene              |
| Network              | Speed / Latency       | `Full` / `None` | no throttling                                                  |
| Startup              | Orientation           | `Portrait`      | the app is portrait-only                                       |
| Startup              | Default boot          | `Quick`         | restores from a snapshot instead of booting                    |
| Storage              | Internal storage      | `10 GB`         | plenty                                                         |
| Storage              | Expanded storage      | `None`          | change from `Custom 512 MB`, no SD card is used                |
| Emulated Performance | CPU cores             | `4`             | the emulator scales badly past 4                               |
| Emulated Performance | Graphics acceleration | `Automatic`     | resolves to `host`, fastest with a window                      |
| Emulated Performance | RAM                   | `2 GB`          | 16 GB host is shared with Studio, the Gradle daemon and Docker |
| Emulated Performance | VM heap size          | `228 MB`        | default is enough                                              |
| Emulated Performance | Preferred ABI         | `Optimal`       | picks x86_64                                                   |

The app uses neither the camera nor an SD card, so nothing is lost for manual use. If the dialog offers an "Enable
device frame" checkbox, uncheck it — the phone bezel is an extra compositing layer and the screen stays fully visible
without it.

## 3. config.ini

Audio is not exposed in the UI. Close the emulator and edit
`C:\Users\<user>\.android\avd\<AvdName>.avd\config.ini`:

```ini
hw.audioInput=no
hw.audioOutput=no
```

The unused sensors can be disabled there as well — `hw.gps`, `hw.sensors.light`,
`hw.sensors.pressure`, `hw.sensors.proximity`, `hw.sensors.magnetic_field`. The gain is small. Keep `hw.accelerometer`
and `hw.sensors.orientation`, otherwise screen rotation stops working.

Edit the file only while the emulator is closed, and always **after** the last AVD Manager save:
saving an AVD rewrites `config.ini` and drops these keys, so re-check them whenever anything is
changed in the dialog.

## 4. Android Studio

Settings → Tools → Emulator → uncheck **Launch in a tool window**. A standalone window renders directly, while the
embedded one streams frames into the IDE, and it is easier to resize.

## 5. Animations on the device

`testOptions { animationsDisabled = true }` in `app/build.gradle.kts` only covers the AGP test run. Turning them off
permanently also speeds up manual clicking — screens switch instantly instead of sliding.

Start the emulator first — both ways below need a running device. The values are stored on the data partition, so they
survive restarts, but are lost on Wipe Data or when the AVD is recreated.

On the device: Settings → Developer options → Window animation scale, Transition animation scale, Animator duration
scale → Off. If the section is not there, enable it first:
Settings → About emulated device → tap Build number seven times.

From the host, once `adb devices` lists the emulator — from any directory, the working directory does not matter. `adb`
lives in `~/AppData/Local/Android/Sdk/platform-tools/adb.exe` and is not on PATH by default, so either add that folder
to PATH or call it by its full path:

```
~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell settings put global window_animation_scale 0
~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell settings put global transition_animation_scale 0
~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell settings put global animator_duration_scale 0
```

## Notes

- Stop the server Docker stack (`docker compose down` in `server/`) before running instrumented tests — Docker Desktop
  and the emulator compete for the same hypervisor and CPU.
- Instrumented test coverage instrumentation is enabled only for `createInstrumentedCoverageReport`, so plain
  `connectedDebugAndroidTest` runs are not slowed down by it.
