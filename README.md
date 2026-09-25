SecureScreen
============

What it does
------------
SecureScreen is a simple Android app that opens images and PDFs and prevents screenshots and screen recordings by setting FLAG_SECURE at runtime for all activities. If a user attempts to capture the screen on most devices, the OS blocks the capture. The app also renders multi-page PDFs with pinch-to-zoom support.

Why this is needed
------------------
Some apps display sensitive content (financial documents, exam papers, proprietary PDFs). Preventing screenshots and recordings reduces accidental or malicious data leakage when viewing content on-device.

How it works
------------
- The Application class (MyApp) registers ActivityLifecycleCallbacks and sets WindowManager.LayoutParams.FLAG_SECURE for each activity (on creation and resume).
- MainActivity lets users pick an image or PDF via ACTION_OPEN_DOCUMENT.
- PdfPagerAdapter uses PdfRenderer to render each PDF page into a bitmap and displays it in a zoomable ImageView.
- All activities/windows have FLAG_SECURE so the platform refuses screenshot/screen-recording APIs.

Limitations
-----------
- FLAG_SECURE prevents capture on most physical devices and standard screenshot APIs. Some emulator snapshots, host-side screen capture tools, or rooted devices may still capture content.
- Android does not provide a reliable callback when a user presses screenshot keys. The app shows a Toast informing users screenshots are disabled, but cannot intercept keypress events system-wide.
- PDF rendering uses the platform PdfRenderer and renders pages as bitmaps; very large PDFs may require additional memory/caching logic.

Build & run
-----------
1. Open the project in Android Studio (File → Open) or use the Gradle wrapper.
2. Gradle wrapper is included; run:
   ./gradlew assembleDebug
   ./gradlew installDebug   # install to a connected device
3. Or Run from Android Studio. Minimum SDK: 21. Target: 34.

Testing notes
-------------
- Test on a physical device to verify screenshots are blocked (emulators may still be captured by the host).
- Open a PDF and attempt a screenshot — expected: OS blocks capture and resulting screenshot is blank or blocked.


