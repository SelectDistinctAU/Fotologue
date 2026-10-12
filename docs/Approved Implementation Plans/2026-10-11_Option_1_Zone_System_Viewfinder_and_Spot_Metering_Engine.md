# Option 1: Zone System Viewfinder & Spot Metering Engine Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 05:30 AM UTC

---

This plan implements Ansel Adams' 10-Zone System directly onto the live camera light meter viewfinder feed (`LightMeterScreen.kt` & `LuminanceAnalyzer.kt`), calculating **Ansel Adams Zones (Zone 0 $\to$ Zone X)**, **Dynamic Range Contrast Range (in EV/Stops)**, and **Push/Pull ($N+1$ / $N-1$) Development Recommendations**.

---

## 1. Ansel Adams Zone System & Color Science Specifications

```
┌─────────────────────────────────────────────────────────────┐
│ LIVE LIGHT METER VIEWFINDER (ANSEL ADAMS ZONE VISUALIZER)   │
│ 🎯 Spot EV 14.2 ➔ [ Zone VII: Textured Highlight ]          │
│ 📊 Scene Dynamic Range: 8.5 Stops (Min EV 8.2 ... Max EV 16.7)│
│ 🧪 Recommended N-Development: Normal (N) Development         │
└─────────────────────────────────────────────────────────────┘
```

### Color Science Calculations:
1. **Ansel Adams Zone Mapping**:
   - $\text{Zone} = \text{Zone V (5.0)} + (EV_{\text{spot}} - EV_{\text{metered}})$.
   - **Zone 0**: $EV \le -5.0$ (Pure Black / Maximum Density).
   - **Zone III**: $EV = -2.0$ (Textured Shadow Detail).
   - **Zone V**: $EV = 0.0$ (18% Neutral Mid-Gray Baseline).
   - **Zone VII**: $EV = +2.0$ (Textured Highlight Detail).
   - **Zone X**: $EV \ge +5.0$ (Specular Pure White Highlight).
2. **Dynamic Range Contrast Range**:
   - Contrast Range = $EV_{\text{max\_spot}} - EV_{\text{min\_spot}}$ (in EV / Stops).
   - **Latitude Alert**: Warns if scene contrast exceeds active film stock latitude (e.g. $> 7$ stops for Slide film).
3. **Push / Pull ($N \pm 1$) Development Recommendation**:
   - **High Contrast ($> 10$ Stops)**: Recommend **$N-1$ Pull Development** (soften contrast, hold highlight detail).
   - **Flat Contrast ($< 5$ Stops)**: Recommend **$N+1$ Push Development** (expand contrast, pop shadows).
   - **Normal Contrast ($5 - 10$ Stops)**: Recommend **Normal $N$ Development**.

---

## 2. Proposed Code Changes

- **[NEW]** `app/src/main/java/com/fotologue/app/util/ZoneSystemCalculator.kt`
  - Ansel Adams Zone System mapping, scene dynamic range calculation, and $N \pm 1$ development recommendation logic.
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/viewmodel/LightMeterViewModel.kt`
  - Track spot metering zone points, scene EV min/max bounds, and active Zone status.
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/LightMeterScreen.kt`
  - Display live **Ansel Adams Zone Badge** on spot tap, **Dynamic Range Bar**, and **$N \pm 1$ Push/Pull Recommendation Card**.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `FIELD` $\to$ tap **`Measure Exposure`**.
3. Tap bright highlight spot on viewfinder $\to$ verify **Zone VII / VIII (Highlight)** badge renders live.
4. Tap dark shadow spot $\to$ verify **Zone II / III (Shadow)** badge renders live.
5. Verify **Scene Dynamic Range (in Stops)** and **Push/Pull ($N \pm 1$) Recommendation** update in real time!
