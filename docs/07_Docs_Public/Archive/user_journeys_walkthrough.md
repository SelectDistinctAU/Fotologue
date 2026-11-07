# Fotologue User Journeys Walkthrough Guide

## Overview

Fotologue is structured across 4 interconnected operational phases: **Field**, **Lab**, **Station**, and **Archive**. This guide walks through the step-by-step user journeys across each phase of the analogue and hybrid photography process.

---

## 🗺️ Master Application Staging Portal (`HubScreen`)

```
    ┌────────────────────────────────────────────────────────────────────────┐
    │                        STAGING PORTAL (`HubScreen`)                     │
    └───────┬───────────────────┬────────────────────┬──────────────────┬────┘
            │                   │                    │                  │
            ▼                   ▼                    ▼                  ▼
      JOURNEY 1           JOURNEY 2            JOURNEY 3          JOURNEY 4
    🥾 FIELD LOGGING     🧪 WET DARKROOM       📷 SCANNING        🏛️ MASTER VAULT
    & LIGHT METERING     SUPPLY & TIMER       & CONTACT SHEETS   & DESKTOP DAM
```

---

## 🥾 Journey 1: FIELD (In-situ Exposure & Metering)

### Goal
Log camera exposures in the field with industrial metering precision, zero friction, and automatic gear cross-referencing.

```
[Camera & Kit Selection] ──► [Spot/Center Metering] ──► [EV Fine-Tuning] ──► [1-Tap Exposure Log]
 (Leica M6 + 50mm Summicron) (CameraX Live Viewfinder Zoom) (+0.33 EV Offset)    (GPS + 3-Axis Tags)
```

### Step-by-Step Walkthrough:
1. **Smart Kit Selection**:
   - Open **Field** (`TimelineScreen`). Tap **`+ Log Exposure`**.
   - Select your Camera (*e.g., Leica M6*). If you have 1 film stock loaded or 1 lens mounted, Fotologue **auto-selects them automatically**.
2. **Pro Light Metering & Optical Zoom Emulation**:
   - Tap **`[Measure Exposure]`**.
   - The CameraX live viewfinder opens and **automatically zooms to 2.08x** (simulating the 50mm optical field of view of your lens).
   - Toggle between **Spot** or **Centre-Weighted** metering mode.
3. **Tactile EV Fine-Tuning**:
   - Tap **`[+]`** or **`[-]`** buttons to apply EV exposure compensation (*e.g., +0.33 EV for backlit subjects*).
4. **1-Tap Log Saving**:
   - Tap **`[Use Settings & Save Log]`**.
   - Saves the frame with a unique physical roll serial (*e.g., `LCA-TX400-R01.01`*), GPS coordinates, and 3-Axis Tags (`Subject • Content • Intent`).

---

## 🧪 Journey 2: LAB (Wet Darkroom & Chemical Supply Chain)

### Goal
Manage darkroom stock in-flow, run guided film development countdown timers in total darkness with haptic vibration cues, and record enlarger print reflection logs.

```
[Vendor Supply In-Flow] ──► [Process Wizard] ──► [Guided Multi-Step Timer] ──► [Enlarger Reflection Log]
 (Receive D-76 at Vanbar)  (B&W 20°C / 9m 45s)   (Haptic Agitation Cues 📳)   (Keeper vs Reject + LOV)
```

### Step-by-Step Walkthrough:
1. **Supplier & Stock In-Flow**:
   - Open **Lab** (`LabHomeScreen`). Tap **`Suppliers`** to view active vendors or locate stores on Google Maps.
   - Receive ordered chemistry (*e.g., Kodak D-76*) into darkroom stock (`IN_TRANSIT` $\to$ `ON_HAND`).
2. **Darkroom Safelight Mode Auto-Trigger**:
   - Tap **`Develop Roll`** (`FilmProcessingSessionScreen`).
   - The UI automatically transforms into **100% Pure Darkroom Red (`#FF0000`) & Black (`#000000`)** to prevent subpixel paper fogging.
3. **Process Wizard & Recommended Auto-Timing**:
   - Select process type: **`B&W`** (20.0°C).
   - Select film roll: **`Pentax LX • Ilford FP4 Plus 125 • Roll 01`**.
   - Select developer: **`Kodak D-76 Developer ([In Use] - Darkroom Shelf A)`**.
   - Fotologue auto-calculates the recommended development time (**7 min 30 sec**).
4. **Guided Multi-Step Timer & Haptic Agitation Cues**:
   - Tap **`[Start]`**.
   - Live timer runs through **Step 1 Developer**, **Step 2 Stop Bath**, **Step 3 Fixing Chemistry**, and **Step 4 Final Wash**.
   - **Haptic Vibration**: The smartphone **vibrates 2 seconds BEFORE every 10s agitation boundary**, alerting the photographer in total darkness when to invert or rest the tank!
5. **Enlarger Session & Reflection Log**:
   - Tap **`Enlarger Session`** (`EnlargerPrintingSessionScreen`).
   - Select Developed Roll and Frame (*e.g., Frame #2*), paper stock, enlarger body, lens, print size (*8x10"*), base exposure time, and grade filter (*Grade 2.5*).
   - Log print outcome (*`🔴 KEEPER`* vs *`🔴 REJECT`* with Photographer Learning reflection tags like *Misfire*, *Underexposed*, or *Focus Shift*).
6. **Session Logs & Reflections**:
   - Tap **`Session Logs & Reflections`** in Lab Home to review past completed film development events and print reflection records.

---

## 📷 Journey 3: STATION (Camera Macro Scanning & Digital Contact Sheets)

### Goal
Bridge camera macro scans and flatbed scans with field exposure logs, align rebate frame numbers, and inspect digital contact sheets.

```
[Scan / Digitise Entry] ──► [Digital Contact Sheet] ──► [Frame Gap Adjuster] ──► [PDF Proof Exporter]
 (Tap Scan in Lab Menu)      (6-Col 35mm Grid)           (Rebate Alignment +1/-1)  (Printable A4 Sheet)
```

### Step-by-Step Walkthrough:
1. **Scan / Digitise Roll Entry Point**:
   - In Lab Home, tap **`📷 Scan / Digitise Roll`**.
   - Directly transitions to **Station** (`ContactSheetScreen`).
2. **Digital Contact Sheet Grid**:
   - View frame thumbnails arranged in a 6-column grid (35mm) or 3-column grid (120 format) organized by physical roll.
3. **In-Grid Frame Gap Adjuster**:
   - Use `+1` / `-1` offset controls to align digital frame numbers with physical film rebate numbers on the film strip.
4. **Printable Proof Sheet Export**:
   - Tap **`[Export A4 Contact Sheet PDF]`** to generate a printable proof sheet with camera/lens metadata, exposure tables, and frame thumbnails.

---

## 🏛️ Journey 4: ARCHIVE & DESKTOP BRIDGE (Master Vault & DAM Integration)

### Goal
Maintain a single canonical relational database backup vault, extract embedded thumbnails, non-destructively merge legacy roll backups, and dispatch open ISO 16684 XMP sidecars to desktop RAW processors and DAMs.

```
[Fotologue Desktop] ──► [Stage 1: Job Lot Ingest] ──► [Stage 2: Master QA] ──► [Stage 3: XMP Dispatcher]
 (Win / Linux / Mac)      (Extract metadata.json)      (Interactive Edits)      (Geotag RAW Scans)
```

### Step-by-Step Desktop Walkthrough:
1. **Stage 1: Ingestion & Vault Inflow**:
   - Open **Fotologue Desktop Command Center** (Windows 11 Pro / macOS / Linux).
   - In **Stage 1**, select your `Fotologue exports` folder containing your ZIP backups.
   - Click **`[Process Job Lot of ZIP Exports & Merge metadata.json Graphs...]`**.
   - Fotologue seamlessly extracts the JSON graphs and embedded image thumbnails, merging them chronologically into your Master Vault.
2. **Stage 2: Master QA & Detail Workbench**:
   - Navigate to **Stage 2** to view your full-fidelity Master Rolls.
   - Use the **`[⚡ Auto-Standardize Legacy Rolls]`** button to batch-update generic "Camera" or "Film Stock" entries to your canonical gear (e.g., *Pentax LX*, *Ilford FP4 Plus 125*).
   - Multi-select split legacy rolls and click **`[🔗 Merge Selected Rolls]`** to safely combine them chronologically (preserving all 36 frames and respecting physical 12/36 frame format limits).
   - Review your **Field Exposure Specs**, **Lab Processing Lineage**, **Enlarger Print Reflections**, and decoded **Image Thumbnails** in the Master-Detail panel.
   - Use the **`[🗺️ View Location on Web Map]`** button or manually correct GPS Coordinates directly on the frame.
3. **Stage 3: ExifTool Geotagging & XMP Dispatcher**:
   - On an inspected roll, click **`[🛠️ Geotag Scans in Stage 3]`**.
   - Browse your local drive or NAS share for the folder containing your camera macro RAW scans (e.g., `_DSF1024.RAF`).
   - Click **`[Geotag Scan & Emit ISO XMP Sidecar Now]`**.
   - Fotologue's bundled `exiftool` engine silently writes your field GPS coordinates, 3-Axis Tags (`Subject • Content • Intent`), and roll serials into `.xmp` sidecars.
4. **Desktop Software Interoperability**:
   - Open the scan folder in **digiKam**, **DxO PhotoLab**, **Adobe Lightroom**, or **Immich**. The sidecars automatically drop map pins on the exact shooting location and populate all metadata!
   - Tap **`[Mount NAS / Cloud Directory]`** to select your local NAS share (`//NAS/FilmArchive/`), Proton Drive, or Google Drive folder via Storage Access Framework (SAF).
2. **Canonical Native JSON Vault**:
   - Tap **`[Export Vault]`** to save a full-fidelity database backup (`backup_YYYYMMDD_HHMM.json`).
   - Tap **`[Merge Import]`** to perform an idempotent non-destructive merge of historical rolls from backups without creating duplicate rolls or split ZIP files.
3. **Desktop DAM Sidecar Exporter**:
   - Tap **`[Export XMP Sidecars]`** to generate ISO 16684 `.xmp` sidecars (`_DSF1024.xmp`) alongside RAW camera macro scans.
   - Sidecars contain 3-Axis tag trees (`Subject • Content • Intent`), creator copyright, GPS map coordinates, and darkroom print recipe notes.
4. **Desktop Software Interoperability**:
   - Open the NAS folder in **digiKam**, **DxO PhotoLab**, **Adobe Lightroom**, or **Capture One**. The sidecars automatically populate all metadata!
