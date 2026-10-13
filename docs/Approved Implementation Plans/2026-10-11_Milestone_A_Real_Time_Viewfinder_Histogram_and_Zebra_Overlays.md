# Milestone A: Real-Time Viewfinder Histogram & Exposure Clipping Zebra Overlays Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 06:30 AM UTC

---

This plan implements a live **Real-Time Luminance Histogram** (mapped across Ansel Adams' 10 Zones) and **Specular Highlight/Shadow Clipping Zebra Overlays** directly onto the live camera light meter viewfinder feed (`LightMeterScreen.kt` & `LuminanceAnalyzer.kt`).

---

## 1. Technical & Color Science Specifications

```
┌─────────────────────────────────────────────────────────────┐
│ LIVE LIGHT METER VIEWFINDER WITH HISTOGRAM & ZEBRAS         │
│ 🎯 Spot EV 14.2 ➔ [ Zone VII: Highlight ]                   │
│ 📊 Live Histogram: ▅▆▇█▇▆▅▃  (Zones 0 ➔ X Tone Curve)      │
│ 🏁 Zebra Overlays: Red Stripes on Zone X • Blue on Zone 0  │
└─────────────────────────────────────────────────────────────┘
```

### Color Science & Performance Architecture:
1. **Live Luminance Histogram Array (`LuminanceAnalyzer.kt`)**:
   - Computes a 256-bin luminance histogram from the Y-plane YUV image buffer.
   - Maps bins into the 11 Ansel Adams Zone buckets (Zone 0 through Zone X).
   - Throttles histogram callback updates to ~15fps for zero-lag smooth rendering.
2. **Clipping Detection & Zebra Overlays (`LuminanceAnalyzer.kt` & `LightMeterScreen.kt`)**:
   - **Highlight Clipping (Zone X)**: Luminance values $\ge 250$ trigger Red "Zebra" warning stripes over clipped highlight regions.
   - **Shadow Clipping (Zone 0)**: Luminance values $\le 5$ trigger Blue "Zebra" warning stripes over crushed shadow regions.
3. **Viewfinder Canvas Overlay (`LightMeterScreen.kt`)**:
   - Renders a semi-transparent **Live Histogram Canvas Box** in the bottom-left corner of the viewfinder feed.
   - Renders animated **Zebra Stripe Overlays** over the live camera preview.

---

## 2. Proposed Code Changes

- **[MODIFY]** `app/src/main/java/com/fotologue/app/util/LuminanceAnalyzer.kt`
  - Add 256-bin histogram computation, 11-Zone bin mapping, and highlight/shadow clipping flags.
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/viewmodel/LightMeterViewModel.kt`
  - Expose `histogramBins: IntArray`, `hasHighlightClipping: Boolean`, and `hasShadowClipping: Boolean` in `LightMeterUiState`.
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/LightMeterScreen.kt`
  - Render live **Histogram Overlay Box** and **Zebra Stripe Canvas Overlay** on camera preview.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `FIELD` $\to$ tap **`Measure Exposure`**.
3. Point camera at a bright light bulb $\to$ verify **Red Zebra Stripes** render over the clipped highlight region and **Zone X** spike appears in the live histogram!
4. Point camera into a dark bag/box $\to$ verify **Blue Zebra Stripes** render over crushed shadow regions and **Zone 0** spike appears in the live histogram!
