# Location Permission State Reactive Recomposition Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 01:20 AM UTC

---

This plan fixes the GPS Location Tagging toggle state lag in `WelcomeScreen.kt` by converting `isLocationPermissionGranted` into a reactive Compose state (`remember { mutableStateOf(...) }`) updated immediately by the permission launcher callback and lifecycle `ON_RESUME` events.

---

## 1. Technical Root Cause & Fix Specifications

### Bug: GPS Location Tagging Switch State Lag
- **User Report**: *"When you approve the location permissions the GPS Toggle isn't moving until the Thumbnail is clicked"*
- **Root Cause**:
  1. `WelcomeScreen.kt` used an empty callback `{ _ -> }` for `locationPermissionLauncher`.
  2. `hasLocationPermission` was calculated as a plain non-reactive variable (`val hasLocationPermission = ContextCompat.checkSelfPermission(...)`).
  3. When permission was granted, no Compose state changed, so the UI did not recompose and the `Switch` stayed visually unchecked until another component triggered a recomposition.
- **Fix**:
  1. Wrap location permission in reactive Compose state: `var isLocationPermissionGranted by remember { mutableStateOf(...) }`.
  2. In `locationPermissionLauncher` callback, update `isLocationPermissionGranted = isGranted` immediately upon permission dialog approval.
  3. Add `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` to re-check location permission state whenever the activity resumes.
  4. **Result**: The GPS Location Tagging switch flips **INSTANTLY** as soon as location permission is approved!

---

## 2. Proposed Code Changes

- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/WelcomeScreen.kt`
  - Implement reactive `isLocationPermissionGranted` state updated on launcher callback and lifecycle resume.
