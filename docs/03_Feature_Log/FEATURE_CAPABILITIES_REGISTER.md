# Fotologue Feature & Capability Register 📜📱🖥️

This document serves as the permanent **Feature & Capability Register** for the Fotologue Application Suite across Mobile (`:app`), Desktop (`:desktop`), Core Data (`:core:data`), Design System (`:core:designsystem`), and Feature Modules (`:feature:lab`).

---

## 1. Core Feature Inventory Matrix

| Feature Module | Key Capabilities | Target Platforms | Status |
| :--- | :--- | :--- | :--- |
| **CAMERA GEAR** | 8 Category Management (Cameras, Lenses, Films, Teleconverters, Filters, Flashes, Viewfinders, Kit Bags). Unification Bridge merging developer presets with custom local gear. | Mobile & Desktop | **Active / Production** |
| **TAXONOMY & TAGS** | 3-Level Hierarchical Subject Indexing (`Subject` $\to$ `Content` $\to$ `Intent`). Multi-line `FlowRow` category chip wrapping. XMP sidecar `lr:hierarchicalSubject` generation. | Mobile & Desktop | **Active / Production** |
| **FIELD (Shot Logging)** | Exposure Logging, CameraX Viewfinder integration, Sensor Light Meter, Filter Exposure Factor compensation (+3 stops ND8), Location GPS pinning, FAB safe-save. | Mobile & Desktop | **Active / Production** |
| **ANSEL ADAMS LIGHT METER** | Spot Metering across **11 Ansel Adams Zones (Zone 0 $\to$ Zone X)**. Scene Dynamic Range Contrast Range Calculator. $N \pm 1$ Push/Pull Development Recommendations. Real-time 11-Zone Histogram with animated Red/Blue Zebra clipping overlays. Initial Auto-Scroll to Exposure Sweet Spot (`f/5.6 1/250`). Conditional Zoom Lens controls (`80mm [-] [+]`). | Mobile | **Active / Production** |
| **CONTACTS DIRECTORY** | People & Vendor Management (Suppliers, Commercial Labs, Repair Techs, Friends). Native Android `PickContact()` integration (zero dangerous permissions). 1-click Google Maps store location navigation. Call/Issue tracker log. | Mobile & Desktop | **Active / Production** |
| **LAB (Wet Processing)** | Multi-Stage Home Film Processing Timer Wizard (Pre-wet, Dev, Stop, Fix, Wash). Automatic dev time recommendations. Haptic Agitation Alert Countdown (**"🔔 AGITATE NOW: 10s"**). Chemical Capacity Counter. Pre-Arrival Order Tracker (*Mark Delivered*). Storage Location Management (*Cabinet*, *Shelf*, *Dry Box*, *Vault*). | Mobile & Desktop | **Active / Production** |
| **GOLD-STANDARD ENLARGER PRINTING** | Enlarger Model, Light Head Type (*Condenser*, *Cold Light*, *Dichroic*, *LED*), Enlarger Lens & Taking Aperture (*f/11*), Enlarger Column Height ($cm$), Magnification Ratio ($M_1$), **Inverse Square Law Resizing Calculator** ($T_2 = T_1 \times \left(\frac{M_2+1}{M_1+1}\right)^2$), Single vs. **Split-Grade Exposure** (Grade 00 Soft $s$ + Grade 5.0 Hard $s$), **Dodge & Burn Step Ledger** (`DodgeBurnStep` JSON list: Type, Region, Seconds, Filter Grade, Tool), Paper Stock Emulsion Batch #, Paper Surface Finish (*Glossy*, *Matt*, *Baryta*), Dry-Down Factor % (-10%), Toning Chemistry (*Selenium 1+19*), **✓ KEEPER** vs **✕ REJECT** ratings, and controlled Reject Defect Causes (*Dust/Scratch*, *Underexposed*, *Paper Fog*). | Mobile & Desktop | **Active / Production** |
| **STATION (Digital Proofing)** | Interactive Digital Contact Sheets, Grid Frame Gap Alignment (+1 / -1 offset), Frame Reflection Cards, 1-tap PDF Proof Sheet Generator. | Mobile & Desktop | **Active / Production** |
| **ARCHIVE (Vault & XMP)** | Master Archive ZIP export/import, database backup/restore, Adobe XMP Core 5.6 sidecar dispatcher, ExifTool metadata embedding pipeline. | Mobile & Desktop | **Active / Production** |
| **THEME SYSTEM & RED SHIFT** | 8 Photographic Themes (*Daylight Clean*, *Warm Sepia*, *Red Shift Night Vision*, *Silver Gelatin*, *Flat Darcula*, *Flat Mac Dark*, *Flat Nord*, *System Default*). ISO 3664 Neutral Review Canvas. **100% Darkroom-Safe Red Shift Mode** (`#FF0000` on `#000000`) with darkroom-safe `fotologueredshift.png` logo and universal **Material Symbols Night Sight Max** 1-tap Crescent Moon + toggle (`night_sight_max_24px`). Android 15 (SDK 35) Edge-to-Edge `WindowCompat` compliance. | Mobile & Desktop | **Active / Production** |
| **MASTER PRESET ENGINE** | Developer catalog for 10 categories (82 Cameras, 26 Lenses, Films, Chemicals, Papers, Enlargers, Toners). 1-click Remote Cloud Catalog Sync (`refreshFromCloud()`) fetching raw JSON from `SelectDistinctAU/Fotologue` GitHub repository with disk caching layer. Maintainer Tooling (`:desktop:runMaintainer`) with Sortable Data Grid. | Mobile, Desktop & Maintainer | **Active / Production** |
| **NATIVE DESKTOP DISTRIBUTION** | Standalone Windows `.msi` and `.exe` installer packaging via Gradle `:desktop:packageWindows` with jpackage toolchain, full Java module inclusion (`includeAllModules = true`), and in-place upgrade support (`upgradeUuid`). | Desktop | **Active / Production** |

---

## 2. Recent Feature Additions (v1.0.18 $\to$ v1.0.20)

### 🔬 Gold-Standard Darkroom Printing & Preset Cloud Sync (October 2026)
- **Inverse Square Law Resizing Calculator**: Auto-calculates exposure time scaling when enlarging prints from 8x10" to 16x20".
- **Split-Grade Exposure Ledger**: Soft Grade 00 exposure seconds + Hard Grade 5.0 exposure seconds.
- **Structured Dodge & Burn Step Ledger**: Step type, target region, exposure seconds, contrast filter grade, and dodging/burning tool used.
- **Darkroom Paper, Enlarger, & Toner Preset Cloud Sync**: Darkroom paper stocks, enlargers, enlarger lenses, and toners cataloged in `master_preset_catalog.json` and synchronized remotely via `MasterPresetEngine.refreshFromCloud()`.
- **Night Sight Max Icon & Red Shift Standardization**: Material Symbols `night_sight_max_24px` Crescent Moon + icon button used across all top bars for 1-tap Red Shift toggling.
- **ISO-Style Categorized Documentation Structure**: Reorganized `docs/` into `01_Governance/`, `02_Specifications/`, `03_Feature_Log/`, and `07_Docs_Public/`. Created `CHANGELOG.md`, `SECURITY.md`, and `SUPPORT.md`.
