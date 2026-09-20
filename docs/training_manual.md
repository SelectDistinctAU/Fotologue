# Fotologue Master Training Manual 📖📸

**Version:** 1.0.18 | **Last Updated:** September 2026 | **Audience:** Photographers, DPs, Workshop Instructors & Lab Technicians

**A Photographic Catalogue Suite — Equally Empowering All Painting-With-Light Artists.**

---

## Table of Contents

1. [Application Architecture & Division of Labour](#1-application-architecture--division-of-labour)
2. [Module 1: FIELD (In-situ Exposure & Metering)](#2-module-1-field-in-situ-exposure--metering)
3. [Module 2: LAB (Wet Darkroom & Inventory Supply Chain)](#3-module-2-lab-wet-darkroom--inventory-supply-chain)
4. [Module 3: STATION (Digital Contact Sheets & Reflection)](#4-module-3-station-digital-contact-sheets--reflection)
5. [Module 4: ARCHIVE (Master Vault & XMP Exports)](#5-module-4-archive-master-vault--xmp-exports)
6. [Educational Curriculum & Workshop Exercises](#6-educational-curriculum--workshop-exercises)

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
Smart Gear Kits      Equipment Maintenance      Analytics          Vault / XMP Export
```

---

## 2. Module 1: FIELD (In-situ Exposure & Metering)

### Pro Light Meter Calibration (Wizard's Calibration)
1. **Stage 1: The Void**: Measures sensor Noise Floor with covered lens.
2. **Stage 2: The Balance**: Calibrates Signal Gain multiplier against an 18% grey card.

### Field Logging Workflow & Physical Roll Serials
- Tap **`+ Log Exposure`** to record: Camera Body, Lens, Film/Sensor Stock, Aperture, Shutter Speed, ISO, GPS Location, and 3-Axis Tags (`Subject • Content • Intent`).
- **Physical Film Roll Serial Labeling Protocol**: To eliminate ambiguity when shooting multiple "Roll 1" canisters simultaneously across different camera bodies, Fotologue constructs a unique physical roll serial label:
  `Pentax ME Super • Kodak Tri-X 400 • Roll 01 (36 frames)`
  Photographers write this unique serial (*e.g. `PNTX-TX400-R01`*) on canister leader tape or negative binder sleeves.
- Use **Smart Kits** to pre-load Camera + Lens + Film + Accessory combinations for 1-tap fieldwork setup.

---

## 3. Module 2: LAB (Wet Darkroom & Inventory Supply Chain)

### Master Item Catalogue & Physical Stock In-Flow
- **Item Specification**: Define product specifications with strict Major/Minor Unit derivative rules (*e.g. 1 Box of 25 sheets*).
- **Stock In-Flow**:
  `🚚 IN_TRANSIT` $\to$ Tap **`[Receive]`** $\to$ `🟢 ON_HAND` $\to$ Tap **`[Open/Use]`** $\to$ `🟡 IN_USE` $\to$ `🔴 EXHAUSTED`.
- **Active Suppliers**: Filter vendors by "In Use" checkbox; view store addresses on Google Maps with 1-tap.
- **Vendor Issue Tracker**: Log order inquiries, missing items, and call follow-ups with status badges (`🔴 OPEN`, `🟡 FOLLOWED UP`, `🟢 RESOLVED`).

### Film Processing & Enlarger Printing Sessions
- **Film Development Timer**: Select imported film roll (*e.g. Roll 03 - Tri-X 400*) and developer chemistry lot; run live countdown timer with progress bar; finish session to auto-deplete developer roll capacity.
- **Enlarger Printing Session**: Select `Roll.Frame ID` and paper stock lot (*e.g. Multigrade RC Deluxe Satin 8x10*); log exposure recipe, grade filter, and dodge/burn notes; record outcome (*`🟢 Keeper`* vs *`🔴 Reject`* with Photographer Learning reflection); auto-deplete paper stock!

---

## 4. Module 3: STATION (Digital Contact Sheets & Reflection)

### Digital Contact Sheets
- Interactive grid view of frame thumbnails organized by film roll (6 columns for 35mm, 3 for 120 format).
- **In-Grid Frame Gap Adjuster**: Insert/remove frame gaps (+1 / -1 offset) to keep contact sheets 1-to-1 aligned with physical film rebate numbers.
- **Printable PDF Proof Sheets**: A4 PDF proof sheet generator ready for printing or binder archiving.

---

## 5. Module 4: ARCHIVE (Master Vault & XMP Exports)

### Single Source of Truth & Merge Import
- **Canonical Native JSON Vault**: Full relational database backup (`backup_YYYYMMDD_HHMM.json`).
- **Non-Destructive Merge Import**: Re-import historical `Roll.Frame ID` records from Google Drive / OneDrive archives into local database without thumbnail bloat.
- **ISO 16684 (XMP) Export**: Export `.xmp` sidecars with Creator, Copyright, Contact, and 3-Axis Tag hierarchies for Lightroom, Capture One, digiKam, and darktable.

---

## 6. Educational Curriculum & Workshop Exercises

### Exercise A: Field Exposure & Intention
*Objective*: Practice logging 5 frames using calibrated light metering and 3-Axis Tagging (`Intent|Study` vs `Intent|Series`).

### Exercise B: Darkroom Development & Chemistry Capacity
*Objective*: Execute a Film Processing Session with live timer, log temperature, and verify chemical roll capacity auto-depletion.

### Exercise C: Enlarger Printing & Error Classification
*Objective*: Log an Enlarger Printing Session, create a dodge/burn map, and classify a test print reject between *System Error* and *Photographer Learning*.
