# Ride Filter (Android source)

A starter Android app for filtering visible Ola/Uber/Rapido driver-app notifications against user-set area, minimum fare, minimum fare-per-km, and maximum distance. It only raises a separate alert; it does not accept rides or click inside other apps.

## Build
1. Open this folder in Android Studio (Giraffe or newer).
2. Let Gradle sync.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. Install the debug APK on the Android phone.
5. Open Ride Filter, set filters, Save settings.
6. Tap Enable notification access and enable Ride Filter.
7. Allow notifications when prompted.

## Limitations
- It can only inspect notification text delivered by the supported driver app. It cannot read offers that are not exposed in notifications.
- Fare, distance, and area parsing depends on the exact notification wording. Unknown fare/distance is ignored rather than treated as a match.
- It does not automatically accept rides. Automatic acceptance requires official platform integration/permission; automating taps in third-party driver apps is not included.
- Notification access is powerful. Enable it only if you trust this app. This starter stores filter settings locally and does not upload notification contents.
