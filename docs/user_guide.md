# Fotologue User Guide 📖📸

**Version:** 1.0.18 | **Last Updated:** September 2026 | **Audience:** All Fotologue Application Users

Welcome to the **Fotologue User Guide**. Fotologue is **A Photographic Catalogue Suite — Equally Empowering All Painting-With-Light Artists**. Structured around the **4-Stage Division of Labour (`Field → Lab → Station → Archive`)**, it guides your photography from field exposure to wet darkroom processing, studio analysis, and lifetime archival preservation.

---

## Table of Contents

1. [Application Division of Labour (Field • Lab • Station • Archive)](#1-application-division-of-labour-field--lab--station--archive)
2. [Field: Exposure Logging & Pro Light Meter](#2-field-exposure-logging--pro-light-meter)
3. [Field: Gear Library & Smart Kits](#3-field-gear-library--smart-kits)
4. [Lab: Master Items & Physical Stock In-Flow](#4-lab-master-items--physical-stock-in-flow)
5. [Lab: Maintenance & LOV Management](#5-lab-maintenance--lov-management)
6. [Station: Digital Contact Sheets & Frame Gap Adjuster](#6-station-digital-contact-sheets--frame-gap-adjuster)
7. [Station: Performance Logs & Photographer Learning](#7-station-performance-logs--photographer-learning)
8. [Archive: Backup Vault & Merge Import](#8-archive-backup-vault--merge-import)
9. [Archive: XMP Sidecar Exports & DAM Integration](#9-archive-xmp-sidecar-exports--dam-integration)
10. [For Directors of Photography (DPs) & Digital Artists](#10-for-directors-of-photography-dps--digital-artists)

---

## 1. Application Division of Labour (`Field → Lab → Station → Archive`)

```
1. FIELD             2. LAB                   3. STATION            4. ARCHIVE
 • Exposure Logs      • Item Specifications    • Contact Sheets      • Native JSON Vault
 • Pro Light Meter    • Stock Lots & In-Flow   • Exposure Trends     • Merge Import
 • Relational Gear    • Supplier Directory     • Performance Logs    • XMP Exports
 • Smart Kits         • Maintenance LOV        • Proof PDF Exporter  • Negative Registry
```

---

## 2. Field: Exposure Logging & Pro Light Meter

In the field, speed and precision are paramount.
*   **1-Tap Exposure Log**: Record camera body, lens, film stock, aperture, shutter speed, ISO, GPS coordinates, and notes in seconds.
*   **Physical Film Roll Serial Labeling Protocol**: To prevent ambiguity when shooting multiple "Roll 1" canisters simultaneously across different camera bodies, Fotologue constructs a unique physical roll serial label:
    $$\text{[Camera Body]} \bullet \text{[Film Stock]} \bullet \text{[Roll Number]} \quad (\text{e.g. } \text{Pentax ME Super} \bullet \text{Kodak Tri-X 400} \bullet \text{Roll 01})$$
    Photographers write this unique serial (*e.g. `PNTX-TX400-R01`*) on canister leader tape or negative binder sleeves.
*   **Pro Light Meter**: Spot and centre-weighted metering with live C-41 (Negative), E-6 (Slide), and B&W pre-visualization previews.
*   **Wizard's Calibration**: 2-stage hardware tuning tool (Noise Floor & Signal Gain against an 18% grey card) for industrial metering accuracy.

---

## 3. Field: Gear Library & Smart Kits

Manage your camera bodies and optical equipment in the **Gear Library** (`GearLibraryScreen`).
*   **Cameras & Lenses**: Support for 35mm, 120 (Medium Format), Half-Frame (e.g. Pentax 17), Large Format, and TLR bodies with auto-created fixed lenses.
*   **Seamless Lens Auto-Association**: Tapping **`+ Add New Lens...`** directly from the Exposure Log automatically pre-associates the new lens with the active camera body and pre-selects it upon returning to the exposure log.
*   **Accessories**: Cross-reference Viewfinders, Teleconverters, Filters, and Flashes to specific camera bodies.
*   **Smart Kits**: Pre-configure favourite camera + lens + film + accessory combinations for 1-tap loading during fieldwork.

---

## 4. Lab: Master Items & Physical Stock In-Flow

Fotologue Lab tracks your wet darkroom inventory and physical consumable supply chain.
*   **Item Specifications**: Define consumable products (Chemistry, Paper, Safety Gear, Supplies) with quantifiable inner yields (*e.g. 1 Box of 25 sheets*).
*   **Physical Stock Lots**: Track stock purchases from supplier order to darkroom usage:
    *   `🚚 IN_TRANSIT` $\to$ Tap **`[Receive]`** when parcel arrives $\to$ `🟢 ON_HAND`.
    *   `🟢 ON_HAND` $\to$ Tap **`[Open/Use]`** when bottle unsealed $\to$ `🟡 IN_USE`.
*   **Active Supplier Directory**: Keep vendor contacts (*Rewind Photo Lab, Vanbar, Ikigai, Digital Camera Warehouse*) with an active **"In Use"** checkbox.

---

## 5. Lab: Maintenance & LOV Management

Tap the Handyman icon (`🛠️` / `ic_handyman`) on the top action bar to open the full-screen **Maintenance** manager:
*   **Storage Locations**: Add, Edit, and Delete physical darkroom storage locations (*Chemical Shelf A, Darkroom Fridge, Paper Safe*).
*   **Categories & Default Units**: Edit item categories and their associated default units (*Chemistry $\to$ L, Paper $\to$ sheets*).
*   **Measurement Units**: Manage custom units (*L, mL, fl oz, gal, sheets, box, pair, pack, roll, unit*).
*   **Manufacturers**: Maintain manufacturer brands (*Ilford, Kodak, Ansell, Adox, Foma, CineStill, Paterson*).

---

## 6. Station: Digital Contact Sheets & Frame Gap Adjuster

The **Station** workspace provides digital tools for studio analysis.
*   **Digital Contact Sheets**: Interactive grid view of frame thumbnails organized by film roll (6 columns for 35mm, 3 for 120 format).
*   **In-Grid Frame Gap Adjuster**: Interactively insert or remove frame gaps (+1 / -1 offset) to align contact sheets 1-to-1 with physical film rebate numbers.
*   **Printable Proof Sheets**: Generates A4 PDF proof sheets ready for printing or physical binder archiving.

---

## 7. Station: Performance Logs & Photographer Learning

Track shot quality and build a personal darkroom knowledge base:
*   **`🟢 KEEPER`**: Rate outcomes 1 to 5 Stars with keeper notes.
*   **`🔴 REJECT`**: Classify rejects into:
    *   **System Error**: *Expired Chemistry, Fogged Paper, Light Leak, Hardware Failure*.
    *   **Photographer Learning**: *Bad Framing, Missed Moment, Underexposed, Overexposed, Focus Missed, Low Light Shake*.

---

## 8. Archive: Backup Vault & Merge Import

Your complete Fotologue database is stored in native JSON backups (`backup_YYYYMMDD_HHMM.json`).
*   **Single Source of Truth**: The native JSON backup is the canonical, full-fidelity database backup file.
*   **Non-Destructive Merge Import**: Re-import historical `Roll.Frame ID` records from Google Drive or OneDrive archives into your device without wiping local data or requiring thumbnail image files.

---

## 9. Archive: XMP Sidecar Exports & DAM Integration

Export exposure metadata directly into desktop Digital Asset Management (DAM) software (Lightroom, Capture One, digiKam, darktable):
*   **ISO 16684 (XMP)**: Generates standard `.xmp` sidecar files with Creator, Copyright, Contact, and 3-Axis Tag hierarchies (`Subject`, `Content`, `Intent`).
*   **Camera Macro Copy Presets**: Auto-names exported `.xmp` files (*e.g. `_DSF1024.xmp`*) to match RAW scan file sequences from Fujifilm, Nikon, Canon, Sony, or flatbed scanners.

---

## 10. For Directors of Photography (DPs) & Digital Artists

> *"Discipline for Digital, Freedom for Film"*

Fotologue provides Directors of Photography (DPs), hybrid cinematographers, and digital artists with:
1. **Exposure Decision Auditing**: Document why aperture, shutter speed, ISO, or ND filter choices were made on location.
2. **RAW & Scanner Sequence Alignment**: Automatically matches exported XMP sidecars to digital camera file naming sequences (*Fujifilm `_DSF`, Canon `IMG_`, Nikon `DSC_`, Sony `_DSC`*).
3. **Smart Assignment Kits**: Load pre-configured camera body, lens, and filter templates for fast-paced commercial or film set shoots.
4. **Client Privacy**: Zero cloud backup requirements—keep sensitive set photos and location logs 100% on-device.

👉 **Read the complete [WIIFY Guide for DPs, Digital & Hybrid Photographers](dp_and_digital_photographers_guide.md)** for in-depth workflow breakdowns and feature comparisons.
