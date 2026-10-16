# Un-Squashed Exposure Selection & Auto-Scroll Sweet Spot Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 07:30 AM UTC

---

This plan restores **Shot Cataloguing** as the #1 primary function on `LightMeterScreen.kt` under strict vertical screen space constraints, compacting controls into a 1-line tool header and **automatically scrolling the exposure list directly to the recommended sweet spot setting (`f/5.6 1/250` / `f/8 1/125`)**, bypassing overexposed/underexposed extremes.

---

## 1. Architectural Strategy: Unobstructed Exposure Selection & Auto-Scroll

```
┌─────────────────────────────────────────────────────────────┐
│ LIVE LIGHT METER VIEWFINDER                                 │
├─────────────────────────────────────────────────────────────┤
│ EV: 13.5  [🎯 Zone V]   [Zoom: 80mm -/+]            [ Spot ]│ ◄── Compact 1-Line Tool Header
├─────────────────────────────────────────────────────────────┤
│ EXPOSURE SELECTION (AUTO-SCROLLED TO RECOMMENDED SWEET SPOT)│ ◄── Bypasses extreme overexposures!
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ ⭐ RECOMMENDED: f/5.6   1/250                  [ Use ]  │ ◄── Initial Auto-Scroll Focus!
│ └─────────────────────────────────────────────────────────┘ │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ f/8.0    1/125                                 [ Use ]  │ │
│ └─────────────────────────────────────────────────────────┘ │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ f/11.0   1/60                                  [ Use ]  │ │
│ └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### De-Cluttering & Auto-Scroll Rules:
1. **Initial Auto-Scroll to Recommended Exposure "Sweet Spot"**:
   - Calculate the recommended middle exposure setting (e.g. `f/5.6 1/250` or `f/8 1/125`).
   - When the exposure suggestions update, use `rememberLazyListState()` and `LaunchedEffect` to automatically scroll the list directly to the recommended sweet-spot index, bypassing unusable overexposed/underexposed settings at the top.
2. **Compact 1-Line Tool Header**:
   - Merge EV readout, Spot mode button, Zone badge (`🎯 Zone V`), and Zoom lens focal length controls into a **single compact horizontal tool header**:
     - **For Zoom Lenses** (`isZoomLens == true`): Render a simple, intuitive inline `80mm [-] [+]` control with $\ge 48\text{dp}$ touch targets for effortless interaction.
     - **For Prime / Fixed Lenses** (`isZoomLens == false`): Hide focal length adjustment entirely.
3. **Eliminate Large Vertical Stack Cards**:
   - Remove the bulky 3-line Zone System card and standalone 60dp Focal Length row that previously consumed over 170dp of vertical height.
4. **Guaranteed Unobstructed Exposure Selection List**:
   - The **Exposure Selection Cards (`f/3.5  1/500  [ Use ]`)** receive **100% of the bottom panel height**, rendering prominently with large touch targets and ample room for scrolling.

---

## 2. Proposed Code Changes

- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/LightMeterScreen.kt`
  - Implement `LazyListState` initial auto-scroll to the recommended exposure index.
  - Compact EV, Zone, and Zoom controls into a single 1-line tool header.
  - Expand Exposure Settings Suggestions List container.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `FIELD` $\to$ tap **`Measure Exposure`**.
3. Inspect `LightMeterScreen.kt` $\to$ verify the exposure suggestions list automatically scrolls directly to the recommended sweet-spot setting (`f/5.6 1/250` / `f/8 1/125`)!
4. Verify Zoom lens controls render simply and intuitively, and Exposure Selection cards (`f/3.5  1/500  [ Use ]`) render as the **unobstructed primary centerpiece** with large touch targets!
