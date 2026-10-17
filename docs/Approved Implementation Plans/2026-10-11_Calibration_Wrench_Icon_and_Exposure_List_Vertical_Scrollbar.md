# Calibration Wrench Icon & Exposure List Vertical Scrollbar Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 08:00 AM UTC

---

This plan updates the sensor calibration wizard top-bar icon to `@drawable/mobile_wrench_24px` for unambiguous clarity, and adds a visible **Vertical Scrollbar** to the exposure settings list in `LightMeterScreen.kt` so users instantly recognize that alternative aperture/shutter speed combinations are available above and below the sweet spot.

---

## 1. Technical Fix Specifications

### A. Sensor Calibration Wrench Icon Update (`LightMeterScreen.kt`)
- **User Request**: *"The Calibrate Sensor Icon is ambiguious... Try: `@drawable/mobile_wrench_24`"*
- **Fix**:
  1. Created `mobile_wrench_24px.xml` M3 vector icon.
  2. Updated `LightMeterScreen.kt` top-bar calibration button to use `R.drawable.mobile_wrench_24px` with description `"Sensor Calibration Wizard"`.

### B. Exposure Settings List Vertical Scrollbar (`LightMeterScreen.kt`)
- **User Request**: *"Can we add a vertical scrollbar to make it clear there are alternatives?"*
- **Symptom**: Android `LazyColumn` hides scrollbars by default, making it non-obvious that the list contains scrollable exposure settings above and below.
- **Fix**:
  1. Add a custom `drawWithContent` / `Scrollbar` canvas indicator to the right edge of the exposure settings `LazyColumn` in `LightMeterScreen.kt`.
  2. The scrollbar thumb renders as a high-visibility primary-colored vertical pill aligned to `LazyListState.firstVisibleItemIndex` and `layoutInfo.totalItemsCount`.

---

## 2. Proposed Code Changes

- **[NEW]** `app/src/main/res/drawable/mobile_wrench_24px.xml`
  - Created M3 mobile wrench vector drawable.
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/LightMeterScreen.kt`
  - Replace `brightness_auto_24px` with `mobile_wrench_24px`.
  - Draw visible vertical scrollbar on exposure settings `LazyColumn`.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `FIELD` $\to$ tap **`Measure Exposure`**.
3. Inspect `LightMeterScreen.kt` top bar $\to$ verify calibration icon displays a clear Wrench icon (`mobile_wrench_24px`)!
4. Inspect exposure settings list $\to$ verify a visible **vertical scrollbar** renders on the right edge, making scrollable alternatives obvious!
