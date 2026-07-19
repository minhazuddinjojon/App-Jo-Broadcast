# Jo Broadcast — CSE 489 Assignment 2 (Kotlin, Android Jetpack)

Mobile Application Development — Fragments + Navigation Drawer + Jetpack Navigation Component.

## How to open this project

1. Unzip the folder anywhere on your computer.
2. Open **Android Studio** → **File > Open** → select the `Jo_Broadcast` folder (the one with `settings.gradle` in it).
3. Let Gradle sync finish (Android Studio downloads the Gradle/AGP versions automatically the first time — no manual SDK setup needed beyond having an Android SDK installed, which Android Studio already manages for you).
4. Pick an emulator or device running **Android 8.0 (API 26) or higher**, with an active internet connection (needed for the Image/Video/Audio screens), and press **Run ▶**.
5. Tap the hamburger icon (☰) top-left to open the Navigation Drawer and switch between the four screens.

No other setup is required — everything (theme, icons, navigation graph, all four features) is already wired up.

## Project structure

```
CSE489_Assignment2/
├── build.gradle, settings.gradle, gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
├── app/
│   ├── build.gradle                     (deps: Navigation, Material, Picasso, ViewBinding, Safe Args)
│   ├── src/main/AndroidManifest.xml
│   ├── src/main/java/com/bracu/cse489/assignment2/
│   │   ├── MainActivity.kt              (DrawerLayout + Toolbar + NavHostFragment host)
│   │   └── ui/
│   │       ├── broadcast/
│   │       │   ├── BroadcastSelectionFragment.kt      → Part A, screen 1 (Spinner + Proceed)
│   │       │   ├── CustomBroadcastInputFragment.kt    → Part A, screen 2 (Custom path: text input)
│   │       │   ├── BatteryBroadcastFragment.kt         → Part A, screen 2 (Battery path: % display)
│   │       │   └── CustomBroadcastReceiverFragment.kt → Part A, screen 3 (registers + fires custom receiver)
│   │       ├── imagescale/
│   │       │   ├── ImageScaleFragment.kt              → Part B (Picasso load from internet)
│   │       │   └── PinchZoomImageView.kt              → Part B (pinch-to-zoom ImageView)
│   │       ├── video/VideoFragment.kt                 → Part C (VideoView + MediaController)
│   │       └── audio/AudioFragment.kt                 → Part D (MediaPlayer + custom controls)
│   └── src/main/res/
│       ├── navigation/nav_graph.xml     (all screens + actions + the "message" Safe Args argument)
│       ├── menu/drawer_menu.xml         (the 4 drawer items)
│       ├── layout/                      (one XML per screen, ViewBinding-ready)
│       └── values/                      (strings, colors, theme, arrays)
```

## How each requirement is implemented

### A. Broadcast Receiver (3-screen flow)
- **Screen 1 — `BroadcastSelectionFragment`**: a `Spinner` listing *Custom Broadcast Receiver* / *System Battery Notification Receiver*. A subtitle line updates live as you change the selection, explaining what that path does. **Proceed** navigates based on the selected index, with a slide+fade screen transition.
- **Screen 2, Custom path — `CustomBroadcastInputFragment`**: a Material outlined text field (with a live 120-character counter) collects a message; **Proceed** is disabled until there's real text, and enables/disables live as you type. It then passes the message to screen 3 as a type-safe Navigation **Safe Args** argument.
- **Screen 2, Battery path — `BatteryBroadcastFragment`**: dynamically registers a `BroadcastReceiver` for `Intent.ACTION_BATTERY_CHANGED` in `onStart()`/unregisters in `onStop()`, and drives a custom **animated circular gauge** (`BatteryRingView`, hand-drawn with Canvas arcs) that smoothly fills to the live percentage and shifts color — green / amber / red — based on the level. As specified, this path stops here.
- **Screen 3, Custom path — `CustomBroadcastReceiverFragment`**: registers its own custom `BroadcastReceiver` for a private action (scoped to the app via `setPackage(...)`). Tapping **Send Broadcast** actually calls `sendBroadcast(...)`; the receiver's `onReceive()` catches it live and the result card **pulses** to give clear, immediate feedback that a real broadcast round-trip just happened.
- Receivers use `Context.RECEIVER_NOT_EXPORTED` on API 33+ (required by modern Android; guarded with an `SDK_INT` check so it still runs on API 26–32).

### B. Image Scale — `ImageScaleFragment` + `PinchZoomImageView`
Loads an image from the internet with **Picasso** into a custom `PinchZoomImageView` that supports:
- **Pinch-to-zoom** (1×–5×) via `ScaleGestureDetector`.
- **Drag-to-pan** once zoomed in, with the pan mathematically clamped so the image can never be dragged past its own edge.
- **Double-tap to reset**, plus a floating **Reset zoom** button and a live **zoom-percentage badge** that both appear only while zoomed in.

### C. Video — `VideoFragment`
Plays a video inside the app with `VideoView` + `MediaController` (play/pause/seek overlay). When playback finishes, a **Replay** overlay appears — tap it to watch again.

### D. Audio — `AudioFragment`
Plays audio inside the app with `MediaPlayer` directly: a custom Play/Pause `FloatingActionButton`, a live `SeekBar`, current/total time labels, and a **vinyl-style disc that spins while playing** and freezes in place when paused (a real `ObjectAnimator` on the album-art view, paused/resumed in sync with the player).

> The three `SAMPLE_..._URL` constants are widely-used public test/demo resources (Picsum for the photo, Google's public GTV test bucket for the video, SoundHelix for the audio) — each is a single `const val` at the top of its Fragment, so you can swap in your own URL, or point it at a local `res/raw` file instead, in one line.

## Design
- A deliberate indigo/amber palette (`colors.xml`) instead of default Material blue, with a dedicated near-black surface color for the media screens.
- Consistent rounded-corner shape theming (`ShapeAppearance.Assignment2.Small/Medium`) applied globally via the theme, so buttons, cards, and text fields share one visual language.
- Custom hand-drawn drawer icons (signal rings, frame+mountain, frame+play, note) with a selected/unselected color state list (`nav_item_color.xml`).
- Slide+fade screen transitions on the Broadcast Receiver flow (`res/anim/*`, wired via `app:enterAnim`/`exitAnim` on the nav graph actions) and a subtle fade/rise entrance animation on every screen.

## Jetpack pieces used (as required by the assignment)
- **Fragments** for every screen (no extra Activities beyond the single `MainActivity` host).
- **Navigation Component** (`nav_graph.xml`, `NavHostFragment`, `findNavController()`) drives all screen-to-screen movement, including drawer taps.
- **Navigation Drawer widget** (`DrawerLayout` + `NavigationView`), wired to the graph with `NavigationUI` so the toolbar automatically shows ☰ on top-level screens and ← (Up) on sub-screens.
- **Safe Args** for compile-time-checked argument passing between fragments (no manual `Bundle`/`getString(key)`).
- **ViewBinding** everywhere — no `findViewById`.

## Notes
- `minSdk 26`, `compileSdk`/`targetSdk 34`.
- Internet permission is declared in the manifest; Image/Video/Audio screens need a working connection on the device/emulator.
- The Gradle wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`) is included, so the project also builds from the command line with `./gradlew assembleDebug` (macOS/Linux) or `gradlew.bat assembleDebug` (Windows) if you have a JDK 17 available — you don't need Android Studio open for that, though opening it in Android Studio is still the easiest way to run/debug on an emulator.
