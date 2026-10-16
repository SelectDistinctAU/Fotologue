# Fotologue User Guide 📖📸

**Version:** 3.0.0 | **Last Updated:** October 2026 | **Audience:** All Fotologue Application Users

Welcome to the **Fotologue User Guide**. Fotologue is **A Photographic Catalogue Suite — Equally Empowering All Painting-With-Light Artists**. Built around a **Modular Operational Architecture**, it guides your photography from field exposure to wet darkroom processing, studio analysis, and lifetime archival preservation across Mobile and Desktop.

---

## Table of Contents

1. [Modular Operational Architecture (Mobile Portal & Desktop Command Center)](#1-modular-operational-architecture)
2. [Field: Exposure Logging & Zone System Spot Metering](#2-field-exposure-logging--zone-system-spot-metering)
3. [Camera Gear: Brand-First Presets & Cloud Refresh](#3-camera-gear-brand-first-presets--cloud-refresh)
4. [Taxonomy & Tags: 3-Axis Hierarchical Indexing](#4-taxonomy--tags-3-axis-hierarchical-indexing)
5. [Contacts Directory & 1-Click Phone Contact Import](#5-contacts-directory--1-click-phone-contact-import)
6. [Lab: Film Development Timer, Enlarger Recipes & Custody Ledger](#6-lab-film-development-timer-enlarger-recipes--custody-ledger)
7. [Station: Digital Contact Sheets & Proof Sheet Exporter](#7-station-digital-contact-sheets--proof-sheet-exporter)
8. [Archive: Backup Vault, XMP Sidecar Exports & DAM Integration](#8-archive-backup-vault-xmp-sidecar-exports--dam-integration)
9. [Desktop Command Center (4 Processing Stages)](#9-desktop-command-center-4-processing-stages)
10. [Maintainer Tool: Master Preset Authoring & Data Grid](#10-maintainer-tool-master-preset-authoring--data-grid)

---

## 1. Modular Operational Architecture

Fotologue provides an integrated, non-linear workflow across 7 Mobile Operational Modules and 4 Desktop Processing Stages:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     FOTOLOGUE APPLICATION HUB                           │
├─────────────────────────────────────────────────────────────────────────┤
│  MOBILE STAGING PORTAL (7 OPERATIONAL MODULES)                          │
│  [1. Camera Gear]  [2. Taxonomy & Tags]  [3. Field Exposure & Metering]  │
│  [4. Contacts Directory]  [5. Lab Processing]  [6. Station]  [7. Archive] │
├─────────────────────────────────────────────────────────────────────────┤
│  DESKTOP COMMAND CENTER (4 WORKFLOW STAGES)                             │
│  [Stage 1: Ingestion] ➔ [Stage 2: Master QA] ➔ [Stage 3: Image Tagging] │
│  [Stage 4: Reference Data Administration & Data Grid]                    │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Field: Exposure Logging & Zone System Spot Metering

In the field, speed and precision are paramount.
* **1-Tap Exposure Log**: Record camera body, lens, film stock, aperture, shutter speed, ISO, GPS coordinates, and notes in seconds.
* **Ansel Adams 10-Zone System Spot Metering**: Tap any spot on the live camera viewfinder feed to inspect its Ansel Adams Zone (**Zone 0 Deep Shadow $\to$ Zone V 18% Mid-Gray $\to$ Zone X Specular Highlight**).
* **Dynamic Range Contrast Calculator**: Measures total scene contrast range (in EV/Stops) and calculates $N \pm 1$ development recommendations (**$N-1$ Pull** for high contrast $>10$ stops; **$N+1$ Push** for flat contrast $<5$ stops).
* **Real-Time 11-Zone Histogram & Zebra Overlays**: Live 11-zone histogram graph with animated **Red Zebra Stripes** on blown highlights ($\ge 250$) and **Blue Zebra Stripes** on crushed shadows ($\le 5$).
* **Pro Calibration Wizard (`brightness_auto_24px`)**: 2-stage hardware sensor tuning (*The Void* noise floor & *The Balance* 18% grey card signal gain multiplier) for industrial metering accuracy.

---

## 3. Camera Gear: Brand-First Presets & Cloud Refresh

Manage your camera bodies and optical equipment in **Camera Gear** (`GearLibraryScreen`).
* **82 Master Camera Presets & 26 Lens Presets**: Instant 1-tap setup covering 1960–2000 SLRs (*Pentax LX*, *Nikon F3*, *Canon AE-1*, *Olympus OM-1*, *Minolta SRT-101*), Rangefinders (*Leica M6*, *Contax G2*, *Canonet QL17 G-III*), TLRs (*Rolleiflex 2.8F*, *Yashica Mat-124G*, *Mamiya C330*), and Medium Format SLR flagships (*Hasselblad 503CX*, *Mamiya RZ67 Pro*, *Pentax 67*).
* **Brand-First Cascading Selection**: Select **Brand** (*Canon*, *Nikon*, *Pentax*, *Leica*, *Hasselblad*) first, and the catalog automatically restricts options to matching models and mounts.
* **1-Click Cloud Preset Refreshing**: Tap **`[ 🔄 Refresh Master Presets from Cloud ]`** in Settings to pull live catalog updates directly from GitHub.

---

## 4. Taxonomy & Tags: 3-Axis Hierarchical Indexing

Categorize every exposure across **Subject** (Who/What), **Content** (Scene elements), and **Intent** (Creative purpose).
* **Parent Category Grouping**: Organize Content under parent Subjects (*Urban/built environment*, *Nature*, *People & Portraits*) and Intent under parent Intent Groups (*Study*, *Series*, *Portfolio*, *Commercial*, *Experimental*).

---

## 5. Contacts Directory & 1-Click Phone Contact Import

The **Contacts Directory** manages suppliers, processing labs, repair technicians, borrowers, and friends.
* **1-Click Phone Contact Import**: Tap **`[ 👤 Import from Phone Contacts ]`** to pick contacts directly from your phone address book.
* **1-Click Vendor Preset Import**: Tap **`[ ⚙️ Select Supplier / Vendor from Presets ]`** to import top international and Australian vendors (*B&H Photo Video, Adorama, Analogue Wonderland, Camera Electronic Perth, Leederville Cameras, Fitzgerald Photo, Rewind Photo Lab, Vanbar, Walkens, Hillvale*).

---

## 6. Lab: Film Development Timer, Enlarger Recipes & Custody Ledger

Fotologue Lab tracks wet darkroom processing, printing recipes, and gear custody.
* **Film Processing Timer Session**: Multi-stage development wizard (*Developer $\to$ Stop Bath $\to$ Fixer $\to$ Wash*) with auto-recommended times (*HP5+ ID-11* 11m, *Tri-X D-76* 9m45s, *C-41* 3m15s, *E-6* 6m00s) and haptic/audio agitation alerts (**"🔔 AGITATE NOW: 10s"**).
* **Enlarger Print Recipe Engine**: Track paper stock, contrast grade filters (0–5 in half-stop steps), test strip increments, dodge/burn notes, **✓ KEEPER** vs **✕ REJECT** outcomes, controlled **Darkroom Defect Causes** (*Dust/Scratch*, *Exhausted Chemistry*, *Paper Fogged*), and multi-select learning tags with custom tag creation.
* **3-Pillar Custody, Loan & Repair Ledger**: Track gear loans, rentals in, service/repair tickets, pre-arrival orders (*In Transit* $\to$ *Mark Delivered*), and physical storage locations (*Cabinet*, *Shelf*, *Dry Box*, *Vault*).
* **Darkroom Red Shift & White Light Sensor Guard**: 100% zero-white red shift mode with `DarkroomLightGuard` (`Sensor.TYPE_LIGHT`) white light intrusion alerts during active darkroom sessions.

---

## 7. Station: Digital Contact Sheets & Proof Sheet Exporter

* **Digital Contact Sheets**: Interactive grid views organized by film roll / digital shoot session with 1-to-1 rebate frame-gap alignment.
* **Printable PDF Proof Sheets**: High-resolution A4/Letter PDF contact sheets ready for printing or binder archiving.

---

## 8. Archive: Backup Vault, XMP Sidecar Exports & DAM Integration

* **Canonical Native JSON Vault**: Single-source-of-truth backup files (`backup_YYYYMMDD_HHMM.json`) containing the full relational data graph.
* **Adobe XMP Core 5.6 Export**: Generates compliant `.xmp` sidecars with EXIF exposure, GPS coordinates, IPTC creator/copyright, and Lightroom/digiKam 3-level taxonomy paths (`lr:hierarchicalSubject`).

---

## 9. Desktop Command Center (4 Processing Stages)

* **Stage 1: Ingestion & Vault Inflow**: Import ZIP backup archives, process Job Lots, and extract scan thumbnails.
* **Stage 2: Master QA & Detail Workbench**: Full-fidelity Master Rolls, frame inspection with **ISO 3664 Neutral Photo Review Canvas (#505050 Mid-Gray)**, ExifTool geotagging, and XMP sidecar dispatcher.
* **Stage 3: Multi-Frame Image Tagging**: Batch 3-axis taxonomy tagging across multiple frames.
* **Stage 4: Reference Data Administration**: 14-Category Sortable Data Grid for Cameras, Lenses, Films, Kit Bags, Teleconverters, Filters, Flashes, Viewfinders, Contacts, Consumables, Lab Equipment, Storage Locations, Pre-Arrival Orders, and Custody Ledger.

---

## 10. Maintainer Tool: Master Preset Authoring & Data Grid

Maintainers can author, validate, and format master JSON catalog files using the visual Maintainer Tool (`./gradlew :desktop:runMaintainer`).
* **Sortable Data Grid Architecture**: View presets in a clean tabular grid with 1-click column sorting (`Brand ▲`, `Preset Name`, `Type`, `Mount`, `Preset ID`).
* **Header-Integrated LOV Filtering**: Click `BRAND ▾`, `TYPE ▾`, or `MOUNT ▾` directly in column header cells to apply multi-criteria filtering.
