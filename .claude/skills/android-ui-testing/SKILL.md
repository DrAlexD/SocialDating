---
name: android-ui-testing
description: Use when testing, navigating, or visually assessing the Android client UI - interacting with buttons/fields, taking screenshots, finding element coordinates
user-invocable: false
---

# Android UI Testing via ADB

## Overview

Test and assess the Android client UI using ADB commands. Covers capturing screenshots for visual review, finding
element positions via UI dump, and simulating user interactions. Run `pull` commands from `client/`.

## Screenshot (Visual UI Assessment)

1. `~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell screencap //sdcard/screen.png`
2. `~/AppData/Local/Android/Sdk/platform-tools/adb.exe pull //sdcard/screen.png ./gen/screen.png`

## Find element coordinates

1. `~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell uiautomator dump //sdcard/ui.xml`
2. `~/AppData/Local/Android/Sdk/platform-tools/adb.exe pull //sdcard/ui.xml ./gen/ui.xml`

Output contains bounds for every element: `bounds="[32,450][1048,530]"`.
Use center coordinate `(x, y) = ((x1+x2)/2, (y1+y2)/2)` for taps.

## Interactions (do **not** use screenshot for this, use dump instead)

- Tap element by coordinate: `~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell input tap x y`
- Type text in field:
    1. `~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell input tap x y`
    2. `~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell input text "abc"`
- Delete all text in field:
    1. `~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell input keycombination 113 29`
    2. `~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell input keyevent 67`
- Scroll / swipe: `~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell input swipe x1 y1 x2 y2`