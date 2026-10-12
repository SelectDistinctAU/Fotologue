# Reinstate Flat Themes & Remove Desktop Red Shift Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 03:25 AM UTC

---

This plan reinstates the **Flat themes** (*Flat Darcula*, *Flat Mac Dark*, *Flat Nord*) across Mobile and Desktop with high-contrast text and `FlowRow` multi-line wrapping, while removing **Red Shift** specifically from the Desktop application.

---

## 1. Technical Fix Specifications

### A. Reinstated Theme Suite
- **Mobile Theme Suite** (`SettingsScreen.kt`):
  1. `System Default`
  2. `Daylight Clean (Light)`
  3. `Flat Darcula (IntelliJ)`
  4. `Flat Mac Dark (macOS)`
  5. `Flat Nord (Arctic Cyan)`
  6. `Silver Gelatin (B&W)`
  7. `Warm Sepia (Toned)`
  8. `Red Shift (Night Vision)`
- **Desktop Theme Suite** (`Main.kt`):
  1. `System Default`
  2. `Daylight Clean (Light)`
  3. `Flat Darcula (IntelliJ)`
  4. `Flat Mac Dark (macOS)`
  5. `Flat Nord (Arctic Cyan)`
  6. `Silver Gelatin (B&W)`
  7. `Warm Sepia (Toned)`
  *(Red Shift removed from Desktop options)*.

### B. High-Contrast Chip & FlowRow Wrapping
- Apply `FlowRow` on both Mobile and Desktop so the expanded 8/7-chip set wraps cleanly across lines with zero right-edge overflow or text squashing.
- Enforce explicit `FilterChipColors(selectedContainerColor = primary, selectedLabelColor = onPrimary)` on all chips for high contrast.

---

## 2. Proposed Code Changes

- **[MODIFY]** `core/designsystem/src/main/java/com/fotologue/core/designsystem/Theme.kt`
  - Re-add `FLAT_DARCULA`, `FLAT_MAC_DARK`, `FLAT_NORD` to `PhotographicTheme` enum with `darkColorScheme` definitions.
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/SettingsScreen.kt`
  - Re-add Flat theme chips to Mobile Settings.
- **[MODIFY]** `desktop/src/main/kotlin/com/fotologue/desktop/Main.kt`
  - Re-add Flat theme chips, remove `SAFELIGHT_RED` from Desktop ThemeAdminPanel options.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest`.
2. Launch Desktop App `./gradlew :desktop:run`.
3. Open `Theme & Canvas Mode` $\to$ verify Flat themes are available on Desktop, Red Shift is absent, and theme chips wrap cleanly on multiple lines with `FlowRow`!
4. Launch Mobile App $\to$ verify all 8 themes (including Flat themes and Red Shift) render with high-contrast text and zero overflow!
