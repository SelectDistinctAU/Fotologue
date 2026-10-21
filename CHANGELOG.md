# Changelog 📜

All notable changes to the **Fotologue** application suite (Mobile & Desktop) will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.20 / versionCode 21] - 2026-10-11 (Mobile) & [1.0.3] (Desktop)

### Added
- **Clean Architecture Domain Use Cases (`core:data`)**: Extracted `CalculateZoneExposureUseCase.kt` (Ansel Adams 11-Zone color science, contrast range, $N \pm 1$ recommendations) and `ProcessDarkroomSessionUseCase.kt` (wet darkroom chemical timing and agitation countdowns).
- **ISO-Style Categorized Documentation Structure**: Re-organized documentation in `docs/` into 4 structured subdirectories (`01_Governance`, `02_Specifications`, `03_Feature_Log`, `07_Docs_Public`). Added `CHANGELOG.md`, `SECURITY.md`, and `SUPPORT.md` to repository root. Recorded Architectural **Decision 006** and updated `CONTRIBUTING.md` Section 3 with concrete pattern examples.
- **Standalone Windows Desktop Installers (v1.0.3)**: Native `FotologueDesktop-1.0.3.msi` and `FotologueDesktop-1.0.3.exe` packaging via Gradle `:desktop:packageWindows` toolchain with in-place Windows upgrade support (`upgradeUuid`).

### Fixed
- **Un-Squashed Exposure Selection List**: Compacted light meter tool header into a 1-line row, reclaiming over 170dp of vertical height and giving 100% of bottom panel height to Exposure Selection cards (`f/stop Shutter [Use]`).
- **Initial Auto-Scroll to Exposure Sweet Spot**: Light meter automatically scrolls directly to the recommended "sweet spot" setting (`f/5.6 1/250` or `f/8 1/125`), bypassing unusable overexposures at the top.
- **Visible Vertical Scrollbar**: Added custom primary-colored vertical scrollbar to exposure settings list.
- **Conditional Zoom Controls**: Hides focal length controls for Prime/Fixed lenses (`isZoomLens == false`) and provides simple 36dp `80mm [-] [+]` controls for Zoom lenses.
- **Standardized Red Shift Controls**: Replaced bulky labeled switches with Material Symbols `night_sight_max_24px` Crescent Moon + icon button across Staging Portal, Lab, Station, and Archive.
- **Darkroom-Safe Red Shift Logo**: Integrated `fotologueredshift.png` asset with 0% white/green subpixel light leakage.
- **Contacts Directory FlowRow Wrapping & De-Cluttering**: Replaced warehouse icon with `patient_list_24px` icon, removed crossed-tools icon and Red Shift toggle, and used `FlowRow` for filter chips (`[ Active Contacts ] [ All Contacts ] [ Australia ] [ USA / Global ]`).
- **Camera Gear Top-Bar Icon**: Added `settings_photo_camera_24px` vector icon next to `Camera Gear` title on both list and detail pane top bars.
- **Android 15 (SDK 35) Edge-to-Edge Compliance**: Integrated `WindowCompat.getInsetsController` `SideEffect` to dynamically adapt system bar icon contrast across all 8 photographic themes.
- **Official GitHub Privacy Policy URL**: Migrated all privacy policy links to `https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md` and website to `https://www.selectdistinct.au`.

---

## [1.0.19 / versionCode 20] - 2026-10-11 (Mobile) & [1.0.2] (Desktop)

### Added
- **Ansel Adams 10-Zone System Viewfinder**: Live Zone 0 $\to$ Zone X spot metering, scene dynamic range contrast calculator, and $N \pm 1$ push/pull development recommendations in `LightMeterScreen.kt`.
- **Real-Time 11-Zone Histogram & Zebra Overlays**: Live 11-zone histogram graph overlay with animated Red (highlight clip $\ge 250$) and Blue (shadow clip $\le 5$) zebra stripes.
- **Wet Darkroom LAB Session Engine**: Multi-stage film development timer wizard with auto-recommended times and haptic agitation alerts (**"🔔 AGITATE NOW: 10s"**).
- **Enlarger Print Recipe Engine**: Paper stock, contrast grade filters (0–5), test strip steps, dodge/burn notes, **✓ KEEPER** vs **✕ REJECT** outcomes, controlled **Darkroom Defect Causes**, and multi-select learning tags with custom tag authoring.
- **3-Pillar Custody, Loan & Repair Ledger**: Gear loans out, rentals in, service/repair tickets, native Android contact picker, pre-arrival order tracking (*Mark Delivered*), and physical storage locations (*Cabinet*, *Shelf*, *Dry Box*, *Vault*).
- **Adobe XMP Core 5.6 Sidecar Dispatcher**: Generates compliant `.xmp` sidecars with EXIF exposure, GPS coordinates, IPTC creator/copyright, and Lightroom/digiKam 3-level taxonomy paths (`lr:hierarchicalSubject`).
- **Standalone Windows Installers**: Gradle packaging task `:desktop:packageWindows` generating native `FotologueDesktop-1.0.2.msi` and `FotologueDesktop-1.0.2.exe` installers.

### Fixed
- **Top-Bar Icon Alignment**: Standardized 100% on Material Symbols `night_sight_max_24px` Crescent Moon + icon button for 1-tap Red Shift toggling.
- **Contacts Directory De-Cluttering**: Replaced warehouse icon with `patient_list_24px` icon, removed crossed-tools icon, and used `FlowRow` for supplier contacts filter chips.
- **Darkroom Red Shift Logo Tinting**: Applied dynamic `#FF0000` tint filter to Staging Portal logo during Red Shift mode to eliminate green/white light leakage.
- **Desktop Consumables Preset Modal Target**: Fixed Consumables form preset button target to open dedicated `ConsumablePresetsDialog`.
- **Android 15 (SDK 35) Edge-to-Edge**: Added `WindowCompat.getInsetsController` appearance controllers to adapt system bar icon contrast dynamically across all 8 photographic themes.

---

## [1.0.18 / versionCode 19] - 2026-10-11
- Incremented versionCode to 19 and versionName to 1.0.18 for Google Play Store release.

---

## [1.0.0] - 2026-10-01
- Initial major release of the Fotologue Application Suite across Mobile and Desktop.
