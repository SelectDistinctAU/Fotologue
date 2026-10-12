# FlowRow Theme Multi-Line Wrapping & Streamlined Photographic Suite Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 03:15 AM UTC

---

This plan fixes the right-edge theme picker overflow using `FlowRow` for automatic multi-line wrapping, and streamlines the theme suite to focus **100% strictly on high-impact photographic palettes** (*System Default*, *Daylight Clean*, *Silver Gelatin B&W*, *Warm Sepia*, *Red Shift Night Vision*) by dropping the generic Flat themes.

---

## 1. Technical Fix Specifications

### A. Theme Picker FlowRow Multi-Line Wrapping (`SettingsScreen.kt` & Desktop `Main.kt`)
- **Symptom**: Theme chips rendered in a rigid single-line `Row`, forcing `Warm Sepia (Toned)` off the right edge into vertical text squashing (`W a r m S e p i a`).
- **Fix**: Replace `Row` with `FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp))`. Theme chips wrap cleanly to the next line when window width is constrained.

### B. Streamlined 100% Photographic Theme Suite
- **User Decision**: Streamline the theme suite by dropping generic Flat themes and focusing exclusively on photographic processes and editing environments:

| Theme Name | Theme Function / Aesthetic | Primary Accent | Background / Surface |
| :--- | :--- | :--- | :--- |
| **System Default** | Dynamic Material You | Dynamic M3 Primary | Dynamic M3 Dark |
| **Daylight Clean (Light)** | High-contrast daytime catalog editing | `#1976D2` Classic Blue | `#FFFFFF` Pure White |
| **Silver Gelatin (B&W)** | High-contrast darkroom print | `#FFFFFF` Platinum Silver | `#141414` Charcoal Black |
| **Warm Sepia (Toned)** | Gentle warm-toned fiber print | `#D4A373` Sepia Gold | `#191512` Deep Espresso |
| **Red Shift** | Astro & darkroom night vision | `#FF0000` Crimson Red | `#000000` Pitch Black |

---

## 2. Proposed Code Changes

- **[MODIFY]** `core/designsystem/src/main/java/com/fotologue/core/designsystem/Theme.kt`
  - Streamline `PhotographicTheme` enum to 5 core photographic palettes (`SYSTEM_DEFAULT`, `DAYLIGHT_LIGHT`, `SILVER_GELATIN`, `WARM_SEPIA`, `RED_SHIFT`).
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/SettingsScreen.kt`
  - Replace `Row` with `FlowRow` and update theme chip list.
- **[MODIFY]** `desktop/src/main/kotlin/com/fotologue/desktop/Main.kt`
  - Replace `Row` with `FlowRow` and update theme chip list.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest`.
2. Launch Desktop App `./gradlew :desktop:run`.
3. Inspect `Theme & Canvas Mode` panel $\to$ verify theme chips wrap cleanly across multiple lines using `FlowRow` with **ZERO OVERFLOW OR TEXT SQUASHING**!
4. Verify the streamlined 5 photographic themes (*System Default*, *Daylight Clean*, *Silver Gelatin*, *Warm Sepia*, *Red Shift*) toggle instantly on Mobile & Desktop.
