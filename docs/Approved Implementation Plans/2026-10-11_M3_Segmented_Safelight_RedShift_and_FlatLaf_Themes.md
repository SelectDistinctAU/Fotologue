# M3 Segmented Safelight & Red Shift Night Vision Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 02:15 AM UTC

---

This plan implements **Red Shift Mode** (a zero-liability, photographically evocative name for night vision preservation), enforces **100% Zero-White-Light Spectral Safety with Dimmed Luminance Output**, restores top bar layout in `HubScreen.kt`, and replaces `(e.g. GB)` with neutral `(e.g. P1)` in `SettingsScreen.kt`.

---

## 1. Nomenclature & Integrity: Red Shift Night Vision Mode

### A. Selected Name: "Red Shift"
- **Official Name**: **`Red Shift`** (Subtitle: *Night Vision Preservation*)
- **Why `Red Shift`**:
  1. **Zero Optical Safety Liability**: Accurately describes the physical screen spectrum shift to red without promising optical paper-safe filtering.
  2. **Photographically & Astronomically Evocative**: Instantly recognized by astrophotographers and classic photographers as a red-spectrum night-adapted vision mode.

### B. On-Screen Transparency Notice
- In `SettingsScreen.kt`, `HubScreen.kt`, and darkroom session views, display the notice:
  > **"ℹ️ Red Shift & Night Vision Notice**: Red Shift eliminates blue light to preserve night-adapted vision during astrophotography and darkroom logbook duties. **Digital screens cannot replace optical darkroom safelights**. Always test with your specific paper stock, maintain distance from paper areas, and load panchromatic film in total darkness."

### C. Low-Luminance Red Spectrum Output
- In **Red Shift Mode**:
  - `activeContainerColor` = `Color(0xFF330000)` (Dimmed Crimson).
  - `activeContentColor` = `Color(0xFFDD0000)` (Soft Crimson Red).
  - `inactiveContainerColor` = `Color(0xFF100000)` (Near-Black Dark Red).
  - `inactiveContentColor` = `Color(0xFF770000)` (Muted Dark Red).
  - `surface & background` = `Color(0xFF000000)` (Pitch Black).
  - **ZERO WHITE OR LIGHT-GREY PIXELS ARE EMITTED ANYWHERE ON SCREEN**.

---

## 2. Top Bar Header Layout & Un-Squashing Fix (`HubScreen.kt`)

1. **Title Layout Weight**:
   - Apply compact padding to `SingleChoiceSegmentedButtonRow` and set `Modifier.weight(1f, fill = false)` on `TopAppBar` title.
2. **Zero Wrapping**:
   - Ensures `Fotologue` and `A Photographic Catalogue Suite` render on a single clean line across the top bar without text squashing or line breaks.

---

## 3. Contributor Alias Placeholder Neutralization (`SettingsScreen.kt`)

- **User Request**: *"Don't use my initials here please"*
- **Fix**:
  - In `SettingsScreen.kt`: Replace `Alias / Shortcode (e.g. GB)` with `Alias / Shortcode (e.g. P1)`.

---

## 4. Proposed Code Changes

- **[MODIFY]** `core/designsystem/src/main/java/com/fotologue/core/designsystem/Theme.kt`
  - Name enum `PhotographicTheme.RED_SHIFT`.
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/HubScreen.kt`
  - Label control as **`Red Shift: [ Auto ] [ On ] [ Off ]`** with deep crimson fills; un-squash `Fotologue` title.
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/SettingsScreen.kt`
  - Replace `(e.g. GB)` with `(e.g. P1)`. Add Red Shift Safety Notice disclaimer & full Segmented Red Shift controls.
