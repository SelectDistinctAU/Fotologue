# Fotologue User Guide 📖📸

**Version:** 1.0.27 (Build 28) | **Desktop Version:** 1.0.5 | **Last Updated:** November 2026 | **Audience:** All Fotologue Users

Welcome to **Fotologue**—the complete photographic catalogue suite designed for analog, hybrid, and digital photographers. Whether you shoot film on a vintage 35mm camera or medium format TLR, send rolls to a commercial lab, scan negatives at home, print in a darkroom, or capture images digitally, Fotologue organizes your entire photographic journey on mobile and desktop.

---

## Table of Contents

1. [Core Concepts & App Architecture](#1-core-concepts--app-architecture)
2. [Level 1: Camera Gear Library & Master Presets](#2-level-1-camera-gear-library--master-presets)
3. [Level 2: FOTOLOGUE FIELD Light Metering Suite](#3-level-2-fotologue-field-light-metering-suite)
4. [Level 3: Film Photographers & Commercial Print Lab Users](#4-level-3-film-photographers--commercial-print-lab-users)
5. [Level 4: Hybrid & Digital Photographers (3-Axis Tags & Desktop Exports)](#5-level-4-hybrid--digital-photographers-3-axis-tags--desktop-exports)
6. [Level 5: Wet Darkroom, Processing & Printing (`LAB`)](#6-level-5-wet-darkroom-processing--printing-lab)
7. [Level 6: Studio Assets, Physical Inventory & Barcode Auto-Fill](#7-level-6-studio-assets-physical-inventory--barcode-auto-fill)
8. [Level 7: Archive Vault, Backups & Desktop Sync](#8-level-7-archive-vault-backups--desktop-sync)
9. [Privacy & Security](#9-privacy--security)

---

## 1. Core Concepts & App Architecture

Fotologue is structured into **8 focused, non-linear modules** accessible directly from the main Staging Portal:

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                       FOTOLOGUE MAIN STAGING PORTAL                          │
├──────────────────────────────────────────────────────────────────────────────┤
│ 1. [ Cameras, Lenses, Films & Accessories ]  -> Camera Gear Library          │
│ 2. [ Assets & Inventory ]                    -> Physical Stock & Barcodes    │
│ 3. [ Exposure Logging & Classification ]    -> Field Shooting & Light Meter  │
│ 4. [ Subject, Content and Intent Tags ]      -> 3-Axis Tagging & Indexing     │
│ 5. [ People & Vendor Management ]            -> Contacts, Labs & Techs        │
│ 6. [ Darkroom Processing & Printing ]        -> Film Developing & Enlarger    │
│ 7. [ Studio Analysis & Reflection ]          -> Contact Sheets & Proofs       │
│ 8. [ Archive Vault ]                         -> Backups, CSV & XMP Sidecars   │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Level 1: Camera Gear Library & Master Presets

Before logging exposures, configure your photographic equipment in **Cameras, Lenses, Films & Accessories**:

* **Master Preset Catalog**: Access curated, verified presets for iconic camera bodies (*Canon T50, T70, T80, T90, AE-1, F-1, Pentax LX, ME Super, K1000, Leica M6, Hasselblad 503CX, Yashica Mat-124G*), lenses, and film stock emulsions.
* **1-Tap Online Web Spec Engine (`WebSpecSearchEngine`)**: Powered by **Camera-wiki.org MediaWiki API** (10,000+ camera articles and `{{Camera}}` infobox templates) + **Wikidata API**, featuring query normalization (*e.g., Canon T50, Praktica LTL 3*) and brand-aware mount resolution (*Canon FD, Nikon F, Pentax K, M42 Thread, Leica M, Olympus OM, Minolta SR/MD*).
* **1-Tap Interactive Availability Status**: Tap the status badge on any camera or lens card to switch between **`In Stock & Available`**, **`In Transit`**, **`On Loan`**, **`In Repair / CLA`**, or **`Sold / Archived`**.

---

## 3. Level 2: FOTOLOGUE FIELD Light Metering Suite

### A. 5-Spot OM-4 Multi-Spot Sampling & Weighted Averaging Engine
- **Multi-Spot Viewfinder Sampling**: Tap or long-press up to **5 spot samples** on the live viewfinder feed (`[ 🎯 Spot 3 / 5 ]`).
- **Area Weighting (Olympus OM-4 Model)**: Tapping a specific shadow or highlight region multiple times drops additional spot pins at that location, giving that area **heavier mathematical weight** in the final average.
- **Compact 1-Line EV Readout**: Displays average EV, total scene contrast range in stops, and development recommendations:
  `EV: 12.6 (Avg of 5) • 1.8st Range [N]`

### B. Live Shifting Zonal Histogram & Spatial Zebra Clipping
- **Ansel Adams 11-Zone Histogram**: Bins 0 through 10 map directly to **Zone 0** (Pure Black) through **Zone X** (Pure White). Shifting exposure ($\pm \Delta \text{EV}$) shifts pixel distribution to the right or left in real time!
- **Spatial $16 \times 24$ Regional Zebra Mask**: Highlights zebra stripes **ONLY over the specific physical pixels in the frame that clip**:
  - 🔴 **Red Zebra Mask**: Overexposure in **Zone X** ($\ge 245$ luminance).
  - 🔵 **Blue Zebra Mask**: Underexposure in **Zone 0** ($\le 10$ luminance).

### C. Single Master Input Cell & Direct Parameter Locking
- **Single Master Cell Row**: Compact dual number picker with **Aperture (`Av`) on the left** and **Shutter Speed (`Tv`) on the right**.
- **Long-Press Direct Locking**:
  - **Long-Press Aperture (`f/4.0`)**: Locks `Av` at `f/4.0` 🔒 (**lightened `primaryContainer` background, bold font, lock badge**).
  - **Long-Press Shutter Speed (`1/250s`)**: Locks `Tv` at `1/250s` 🔒 (**lightened background, bold font, lock badge**).
  - **Long-Press on locked cell**: Unlocks cell 🔓 and returns to live-metered baseline ($0.0 \text{ EV}$).
- **Live Exposure Shift**: Tapping `◀` / `▶` on the unlocked cell shifts exposure, dynamically updating EV offset (*`EV 12.6 (+1.0st Over)`*), shifting the live Zonal Histogram, and updating zebra clipping masks!

---

## 4. Level 3: Film Photographers & Commercial Print Lab Users

If you shoot film and send rolls to a commercial lab (*e.g., Fitzgerald Photo, Silver Salt, Rewind Lab, B&H*):

* **1-Tap Exposure Logging**: Log shutter speed, aperture, ISO, film roll/frame number (*e.g., Roll 1.01*), GPS geotags, and notes in seconds.
* **People & Vendor Management**: Maintain a directory of your favorite print labs, phone numbers, and processing instructions.
* **Custody & Loans Ledger**: Track film rolls sent to labs or gear loaned to friends.

---

## 5. Level 4: Hybrid & Digital Photographers (3-Axis Tags & Desktop Exports)

* **3-Axis Taxonomy Tagging (`Subject, Content and Intent Tags`)**: Categorize every exposure across Subjects (*Architecture, Landscape, Portrait, Street*), Content (*Bridge, Building, Skyline*), and Intent (*Study, Series, Personal, Commission*).
* **Digital Contact Sheets & Frame Gap Alignment (`STATION`)**: Match digital RAW scan files against physical film negatives with $+1 / -1$ in-grid gap adjustment.
* **ISO 16684 XMP Sidecar Exports**: Export `.xmp` sidecars with 3-Axis tag hierarchies (`lr:hierarchicalSubject`), EXIF parameters, GPS coordinates, and creator copyright.

---

## 6. Level 5: Wet Darkroom, Processing & Printing (`LAB`)

* **Film Development Timer & Acoustic 60 BPM Metronome**: Step-by-step timers for developer, stop bath, fixer, and wash (*HP5+ in ID-11, Tri-X in D-76, Ilfosol 3, C-41, E-6*). Acoustic 60 BPM metronome with 3-second pre-warning pips.
* **TopBar Safelight Moon Toggle**: Tap the TopBar **Moon IconButton** (`R.drawable.night_sight_max_24px`) to activate red-shift night vision mode in complete darkness.
* **Enlarger Session Exposure Step Ledger**: Record enlarger lens-to-paper distances, contrast grade filters (0–5 in half-stop steps), and dodge/burn steps.

---

## 7. Level 6: Studio Assets, Physical Inventory & Barcode Auto-Fill

* **1-Tap Master Barcode & Stock Auto-Fill (`Add Inventory Item`)**: Tap the **Barcode / QR Reader IconButton** (`R.drawable.barcode_scanner`) in the TopBar of `Add Inventory Item`. Scanning product barcodes (*e.g., UPC `019498905162` for Ilford Ilfosol 3 500ml*) **auto-populates Category, Manufacturer, Item Name & Container Units in 1 tap!**
* **Consumables Stock Tracking**: Monitor chemical capacity, paper boxes, wetting agents, and safety gear in stock.
* **Pre-Arrival Inbound Orders**: Track incoming camera or lens shipments. Tapping **`Mark Delivered`** converts inbound orders into active assets without duplicates.

---

## 8. Level 7: Archive Vault, Backups & Desktop Sync

* **Catalogue Backup & Restore**: Export full-fidelity `.json` catalogue backups with non-destructive merge imports.
* **Desktop Sync Manifests**: Exchange equipment profiles, contacts, and storage locations bidirectionally with Fotologue Desktop.
* **Cloud Master Preset Refresh**: Tap **`[ 🔄 Refresh Master Presets from Cloud ]`** to pull live camera and lens catalog updates directly from GitHub!

---

## 9. Privacy & Security

* **100% On-Device Data**: All thumbnails, GPS coordinates, notes, and gear lists remain stored locally on your device.
* **On-Device Machine Learning**: Smart Face-Aware tagging operates entirely on-device.
* **Zero Tracking**: Fotologue contains zero third-party analytics, advertisements, or cloud lock-in.
