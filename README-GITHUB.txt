RIDE FILTER ANDROID SOURCE

This package contains the complete Android project in the RideFilter folder.
Keep the internal folder structure exactly as it is; Gradle needs app/src/... paths.

IMPORTANT:
- Uploading this ZIP file alone to GitHub does not run a build.
- GitHub Actions requires a workflow file at .github/workflows/build-apk.yml in the repository.
- After uploading this ZIP to the repository root, create that workflow file separately.
- The workflow must unzip Ride-Filter-Android-Source.zip and run Gradle against RideFilter.

Build command after extracting:
  gradle --no-daemon -p RideFilter assembleDebug

Expected APK:
  RideFilter/app/build/outputs/apk/debug/app-debug.apk

This source has not been verified by a successful Android build in this environment.
