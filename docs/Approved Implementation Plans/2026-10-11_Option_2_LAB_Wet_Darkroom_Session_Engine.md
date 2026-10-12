# Option 2: Wet Darkroom LAB Session Engine Polish Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 04:00 AM UTC

---

This plan finalizes **Option 2 (Wet Darkroom LAB Session Engine)** by wiring **100% Zero-White-Light Red Shift Mode** and the **`DarkroomLightGuard` White Light Sensor Intrusion Alert** directly into active **Film Development Timer Sessions** (`FilmProcessingSessionScreen.kt`) and **Enlarger Print Recipe Sessions** (`EnlargerPrintingSessionScreen.kt`).

---

## 1. Technical Enhancements for Darkroom LAB Engine

```
┌─────────────────────────────────────────────────────────────┐
│ LAB: FILM PROCESSING TIMER SESSION                          │
│ [ Step 1: Developer ] ⏱️ 09:30   [ 🔔 AGITATE NOW: 10s ]   │
│ ⚠️ White Light Guard: Active (0.2 Lux Darkroom Baseline)     │
├─────────────────────────────────────────────────────────────┤
│ LAB: ENLARGER PRINT RECIPE SESSION                          │
│ Paper: Ilford Multigrade RC • Exposure: 12.5s @ Grade 2.5   │
│ Dodge & Burn Map: +2s Top Left Corner • Outcome: 🟢 KEEPER  │
└─────────────────────────────────────────────────────────────┘
```

### Key Integrations:
1. **Red Shift Night Vision Safety in LAB Sessions**:
   - Automatically engage **100% Zero-White-Light Red Shift Mode** (`SafelightRedColorScheme`) when entering `FilmProcessingSessionScreen.kt` and `EnlargerPrintingSessionScreen.kt`.
   - All buttons, timers, chips, and text elements use strict low-luminance deep crimson fills (`#330000` / `#FF0000`) and pitch black (`#000000`).
2. **`DarkroomLightGuard` White Light Intrusion Alert**:
   - During active timer countdowns or printing sessions, activate `DarkroomLightGuard` (`Sensor.TYPE_LIGHT`).
   - If ambient illuminance jumps above $\ge 10\text{ lux}$ (white light intrusion/door opened), trigger a haptic alert and display high-visibility warning:
     > **"⚠️ White Light Detected! Ensure open film/paper is protected in light-tight box."**

---

## 2. Proposed Code Changes

- **[MODIFY]** `feature/lab/src/main/java/com/fotologue/feature/lab/FilmProcessingSessionScreen.kt`
  - Wire `DarkroomLightGuard` white light alert and enforce 100% zero-white red container styling on stage timers and agitation countdowns.
- **[MODIFY]** `feature/lab/src/main/java/com/fotologue/feature/lab/EnlargerPrintingSessionScreen.kt`
  - Wire `DarkroomLightGuard` white light alert and enforce 100% zero-white red container styling on exposure controls and print outcome buttons.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `LAB` module.
3. Start a **Film Processing Session** $\to$ verify live agitation countdown timer (`AGITATE NOW: 10s`) renders in strict zero-white dimmed crimson red!
4. Expose light sensor to room light $\to$ verify **"⚠️ White Light Detected!"** intrusion warning triggers.
5. Start an **Enlarger Printing Session** $\to$ log test strip steps and print recipe $\to$ save as **KEEPER**.
