# Fotologue Master Training Manual 📖📸

**Version:** 3.0.0 | **Last Updated:** October 2026 | **Audience:** Photographers, DPs, Maintainers, Workshop Instructors & Lab Technicians

**A Photographic Catalogue Suite — Equally Empowering All Painting-With-Light Artists.**

---

## Table of Contents

1. [Application Architecture & Division of Labour](#1-application-architecture--division-of-labour)
2. [Module 1: FIELD (In-situ Exposure & Metering)](#2-module-1-field-in-situ-exposure--metering)
3. [Module 2: LAB (Wet Darkroom, Contacts Directory & Inventory)](#3-module-2-lab-wet-darkroom-contacts-directory--inventory)
4. [Module 3: MAINTAINER (Master Preset Authoring & Data Grid)](#4-module-3-maintainer-master-preset-authoring--data-grid)
5. [Module 4: STATION (Digital Contact Sheets & Reflection)](#5-module-4-station-digital-contact-sheets--reflection)
6. [Module 5: ARCHIVE (Master Vault, Cloud Refreshes & XMP Exports)](#6-module-5-archive-master-vault-cloud-refreshes--xmp-exports)
7. [Educational Curriculum & Workshop Exercises](#7-educational-curriculum--workshop-exercises)

---

## 1. Application Architecture & Division of Labour

Fotologue (*Foto* + *Catalogue*) is built around the **4-Stage Division of Labour**:

```
                          FOTOLOGUE APPLICATION HUB
                                      │
     ┌──────────────────┬─────────────┴────────────┬──────────────────┐
     ▼                  ▼                          ▼                  ▼
1. FIELD             2. LAB                     3. STATION         4. ARCHIVE
Exposures & Metering Darkroom Processing        Digital Contact    Master Negative
Roll & Frame Logs    Chemistry & Paper Stock    Sheets & Exposure  Registry & JSON
Smart Gear Kits      Contacts Directory         Analytics          Vault / XMP Export
```

---

## 2. Module 1: FIELD (In-situ Exposure & Metering)

### Pro Light Meter Calibration (Wizard's Calibration)
1. **Stage 1: The Void**: Measures sensor Noise Floor with covered lens.
2. **Stage 2: The Balance**: Calibrates Signal Gain multiplier against an 18% grey card.
3. **Filter Compensation**: Automatic exposure compensation is calculated when selecting ND filters (*Hoya ND8* +3 stops), polarizers (*CPL* +1.5 stops), or B&W contrast filters (*Yellow K2* +1 stop).

### Field Logging Workflow & Physical Roll Serials
- Tap **`+ Log Exposure`** to record: Camera Body, Lens, Film/Sensor Stock, Aperture, Shutter Speed, ISO, GPS Location, and 3-Axis Tags (`Subject • Content • Intent`).
- **Physical Film Roll Serial Labeling Protocol**: To eliminate ambiguity when shooting multiple "Roll 1" canisters simultaneously across different camera bodies, Fotologue constructs a unique physical roll serial label:
  `Pentax ME Super • Kodak Tri-X 400 • Roll 01 (36 frames)`
  Photographers write this unique serial (*e.g. `PNTX-TX400-R01`*) on canister leader tape or negative binder sleeves.
- Use **Smart Kits** to pre-load Camera + Lens + Film + Accessory combinations for 1-tap fieldwork setup.

---

## 3. Module 2: LAB (Wet Darkroom, Contacts Directory & Inventory)

### Master Item Catalogue & Physical Stock In-Flow
- **Item Specification**: Define product specifications with strict Major/Minor Unit derivative rules (*e.g. 1 Box of 25 sheets*).
- **Stock In-Flow**:
  `🚚 IN_TRANSIT` $\to$ Tap **`[Receive]`** $\to$ `🟢 ON_HAND` $\to$ Tap **`[Open/Use]`** $\to$ `🟡 IN_USE` $\to$ `🔴 EXHAUSTED`.
- **Contacts Directory & Vendor Presets**:
  - Tap **`[ ⚙️ Select Supplier / Vendor from Presets ]`** when adding a contact to import top vendors (*B&H Photo, Adorama, Analogue Wonderland, Camera Electronic Perth, Leederville Cameras, Fitzgerald Photo, Rewind Photo Lab, Vanbar*).
  - Pre-fills Vendor Name, Website, Region, and tags `#Supplier` or `#Lab` role tags.

---

## 4. Module 3: MAINTAINER (Master Preset Authoring & Data Grid)

### Master Preset Authoring GUI (`./gradlew :desktop:runMaintainer`)
Maintainers use the visual Maintainer Tool to author, edit, and validate master preset JSON files:
- **Sortable Data Grid**: View presets in a clean tabular grid with 1-click column sorting (`Brand ▲`, `Preset Name`, `Type`, `Mount`, `Preset ID`).
- **Header-Integrated LOV Filtering**: Click `BRAND ▾`, `TYPE ▾`, or `MOUNT ▾` directly in column header cells to apply multi-criteria filtering.
- **Tailored LOV Controls**: Category-specific forms (e.g. darkroom chemistry brands for `CONSUMABLE`, darkroom hardware brands for `EQUIPMENT`, fixed lens references for `CAMERA`).
- **Gradle Release Assembly**: Run `./gradlew :core:data:assembleMasterCatalog` to compile all 10 category JSON files into `master_preset_catalog.json` for release.

---

## 5. Module 4: STATION (Digital Contact Sheets & Reflection)

### Digital Contact Sheets & Performance Logs
- Interactive grid view of frame thumbnails organized by film roll (6 columns for 35mm, 3 for 120 format).
- **In-Grid Frame Gap Adjuster**: Insert/remove frame gaps (+1 / -1 offset) to keep contact sheets 1-to-1 aligned with physical film rebate numbers.
- **Printable PDF Proof Sheets**: A4 PDF proof sheet generator ready for printing or binder archiving.
- **Shot Reflection**: Rate outcomes (*`🟢 Keeper`* vs *`🔴 Reject`* with Photographer Learning notes).

---

## 6. Module 5: ARCHIVE (Master Vault, Cloud Refreshes & XMP Exports)

### Single Source of Truth & Cloud Preset Refresh
- **Canonical Native JSON Vault**: Full relational database backup (`backup_YYYYMMDD_HHMM.json`).
- **1-Click Cloud Preset Refreshing**: Tap **`[ 🔄 Refresh Master Presets from Cloud ]`** in Settings to pull the latest 115+ camera and lens presets directly from GitHub over HTTPS without an app update.
- **ISO 16684 (XMP) Export**: Export `.xmp` sidecars with Creator, Copyright, Contact, and 3-Axis Tag hierarchies for Lightroom, Capture One, digiKam, and darktable.

---

## 7. Educational Curriculum & Workshop Exercises

### Exercise A: Field Exposure & Intention
*Objective*: Practice logging 5 frames using calibrated light metering and 3-Axis Tagging (`Intent|Study` vs `Intent|Series`).

### Exercise B: Darkroom Development & Chemistry Capacity
*Objective*: Execute a Film Processing Session with live timer, log temperature, and verify chemical roll capacity auto-depletion.

### Exercise C: Maintainer Preset Assembly & Cloud Refresh
*Objective*: Author a custom lens preset in the Maintainer Tool, compile master catalog via Gradle, and trigger a 1-click Cloud Refresh on Mobile.