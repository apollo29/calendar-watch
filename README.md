# What? Calendar – Android App

An Android companion app for the **What? Calendar Watch**. The app reads the
calendars on your phone and sends your upcoming appointments to the watch via
Bluetooth Low Energy.

> [!IMPORTANT]
> **This is an unofficial, updated version of the original app.**
>
> - The original "What? Calendar Watch" app
>   ([`com.whatcalendar`](https://apkpure.com/what-calendar-watch-app/com.whatcalendar))
>   is **no longer available on the Google Play Store**. This project is a
>   replacement based on the original app, updated so it keeps working on
>   current Android versions.
> - This project is **not related to, affiliated with, endorsed by or supported
>   by What Watch AG** in any way. The names "What?", "What? Watch" and "What?
>   Calendar" refer to products of What Watch AG and are used here only to
>   describe which watch this app works with.
> - This app was developed **voluntarily and free of charge**, so owners of a
>   Calendar Watch can keep using their watch. The app is and stays free; a
>   donation (see [Support](#support)) is entirely optional.
> - The software is provided **"as is", without any warranty**. The author takes
>   no responsibility for the use of this app with your Calendar Watch. Please
>   do not contact What Watch AG for support with this app.

## Features

- Pair a What? Calendar Watch via Bluetooth
- Choose which of the phone's calendars are shown on the watch
- Transfer appointments and alerts for today and the next two days
- Automatic update when calendar events or the time/time zone change
- Calibrate the watch hands, fixed mode and airplane mode
- Battery level of the watch

## Requirements

- Android 9 (API 28) or newer, targets Android 17 (API 37)
- A What? Calendar Watch
- Bluetooth enabled

On first start the app asks for access to your calendars, Bluetooth ("Nearby
devices") and location (needed for Bluetooth scanning on older Android versions).

## Building

Requirements: JDK 17 or newer and the Android SDK (platform 37).

```bash
./gradlew assembleDebug      # debug APK: app/build/outputs/apk/debug/
./gradlew assembleRelease    # unsigned release APK: app/build/outputs/apk/release/
./gradlew testDebugUnitTest lintDebug
```

The `versionCode` is the number of git commits (`git rev-list --count HEAD`),
so the project has to be built from a git checkout; `versionName` is set in
`gradle.properties`.

## Tech stack

Kotlin, Android Gradle Plugin 9, Hilt (KSP), AndroidX Navigation, View Binding
and the [Nordic Android BLE Library](https://github.com/NordicSemiconductor/Android-BLE-Library).

## Support

This app was made voluntarily and is free. If it helps you keep using your
Calendar Watch and you'd like to say thanks, you can buy me a coffee:

<a href="https://www.buymeacoffee.com/thomasdasca" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me a Coffee" height="60" width="217" style="height: 60px !important;width: 217px !important;" ></a>
