# Fotologue User Guide 📖📸

**Version:** 3.0.0 | **Last Updated:** October 2026 | **Audience:** All Fotologue Application Users

Welcome to the **Fotologue User Guide**. Fotologue is **A Photographic Catalogue Suite — Equally Empowering All Painting-With-Light Artists**. Structured around the **4-Stage Division of Labour (`Field → Lab → Station → Archive`)**, it guides your photography from field exposure to wet darkroom processing, studio analysis, and lifetime archival preservation.

---

## Table of Contents

1. [Application Division of Labour (Field • Lab • Station • Archive)](#1-application-division-of-labour-field--lab--station--archive)
2. [Field: Exposure Logging & Pro Light Meter](#2-field-exposure-logging--pro-light-meter)
3. [Field: Camera Gear, Brand-First Presets & Cloud Refresh](#3-field-camera-gear-brand-first-presets--cloud-refresh)
4. [Lab: Darkroom Inventory, Consumables & Equipment](#4-lab-darkroom-inventory-consumables--equipment)
5. [Lab: Contacts Directory & Vendor Presets](#5-lab-contacts-directory--vendor-presets)
6. [Maintainer Tool: Master Preset Authoring & Data Grid](#6-maintainer-tool-master-preset-authoring--data-grid)
7. [Station: Digital Contact Sheets & Frame Gap Adjuster](#7-station-digital-contact-sheets--frame-gap-adjuster)
8. [Archive: Backup Vault, XMP Sidecar Exports & DAM Integration](#8-archive-backup-vault-xmp-sidecar-exports--dam-integration)
9. [For Directors of Photography (DPs) & Digital Artists](#9-for-directors-of-photography-dps--digital-artists)

---

## 1. Application Division of Labour (`Field → Lab → Station → Archive`)

```
1. FIELD             2. LAB                   3. STATION            4. ARCHIVE
 • Exposure Logs      • Item Specifications    • Contact Sheets      • Native JSON Vault
 • Pro Light Meter    • Stock Lots & In-Flow   • Exposure Trends     • Merge Import
 • Relational Gear    • Contacts Directory     • Performance Logs    • XMP Exports
 • Smart Kits         • Maintenance LOVs       • Proof PDF Exporter  • Negative Registry
```

---

## 2. Field: Exposure Logging & Pro Light Meter

In the field, speed and precision are paramount.
*   **1-Tap Exposure Log**: Record camera body, lens, film stock, aperture, shutter speed, ISO, GPS coordinates, and notes in seconds.
*   **Physical Film Roll Serial Labeling Protocol**: To prevent ambiguity when shooting multiple "Roll 1" canisters simultaneously across different camera bodies, Fotologue constructs a unique physical roll serial label:
    $$\text{[Camera Body]} \bullet \text{[Film Stock]} \bullet \text{[Roll Number]} \quad (\text{e.g. } \text{Pentax ME Super} \bullet \text{Kodak Tri-X 400} \bullet \text{Roll 01})$$
    Photographers write this unique serial (*e.g. `PNTX-TX400-R01`*) on canister leader tape or negative binder sleeves.
*   **Pro Light Meter**: Spot and centre-weighted metering with live C-41 (Negative), E-6 (Slide), and B&W pre-visualization previews. Automatic exposure compensation is calculated when selecting ND filters, polarizers, or B&W contrast filters (*Hoya ND8*, *B+W CPL*, *Yellow K2*).
*   **Wizard's Calibration**: 2-stage hardware tuning tool (Noise Floor & Signal Gain against an 18% grey card) for industrial metering accuracy.

---

## 3. Field: Camera Gear, Brand-First Presets & Cloud Refresh

Manage your camera bodies and optical equipment in **Camera Gear** (`GearLibraryScreen`).
*   **82 Master Camera Presets & 93 Lens Presets**: Instant 1-tap setup covering 1960–2000 SLRs (*Pentax LX*, *Nikon F3*, *Canon AE-1*, *Olympus OM-1*, *Minolta SRT-101*), Rangefinders (*Leica M6*, *Contax G2*, *Canonet QL17 G-III*), TLRs (*Rolleiflex 2.8F*, *Yashica Mat-124G*, *Mamiya C330*), and Medium Format SLR flagships (*Hasselblad 503CX*, *Mamiya RZ67 Pro*, *Pentax 67*).
*   **Brand-First Cascading Selection**: Select **Brand** (*Canon*, *Nikon*, *Pentax*, *Leica*, *Hasselblad*) first, and the catalog automatically restricts options to matching models and mounts.
*   **1-Click Cloud Preset Refreshing**: Tap **`[ 🔄 Refresh Master Presets from Cloud ]`** in Settings to pull live catalog updates directly from GitHub without needing an App Store update.
*   **Smart Kits**: Pre-configure favourite camera + lens + film + accessory combinations for 1-tap loading during fieldwork.

---

## 4. Lab: Darkroom Inventory, Consumables & Equipment

Fotologue Lab tracks your wet darkroom inventory and physical consumable supply chain.
*   **30 Master Darkroom Consumables**: Pre-loaded with B&W & Color chemistry (*Kodak D-76, Xtol, HC-110, T-Max; Ilford ID-11, DD-X, Rapid Fixer, Ilfostop; Rodinal; Cinestill Cs41 & Cs63; Tetenal Colortec C-41*) and darkroom papers (*Ilford Multigrade V RC, FB Classic; Fomabrom FB; Adox Lupex*).
*   **37 Master Lab Equipment Presets**: Pre-loaded with enlargers (*Beseler 23C III, Durst M605/L1200, LPL 7700/4500II, Omega D5-XL*), developing tanks & reels (*Paterson Super System 4, Jobo 1520/2520, AP Compact, Kindermann SS*), timers (*GraLab 300*), light tables, safelights, and grain focus scopes (*Peak 10x*).
*   **Physical Stock Lots**: Track stock purchases from supplier order to darkroom usage (`🚚 IN_TRANSIT` $\to$ `🟢 ON_HAND` $\to$ `🟡 IN_USE`).

---

## 5. Lab: Contacts Directory & Vendor Presets

The **Contacts Directory** manages suppliers, processing labs, repair technicians, borrowers, and friends.
*   **1-Click Vendor Preset Import**: Tap **`[ ⚙️ Select Supplier / Vendor from Presets ]`** when adding a contact to import top international and Australian vendors (*B&H Photo Video, Adorama, Analogue Wonderland, Camera Electronic Perth, Leederville Cameras, Fitzgerald Photo, Rewind Photo Lab, Vanbar, Walkens, Hillvale*).
*   **Auto-Populated Details**: Automatically pre-fills Contact Name, Website URL, Region, and tags the **`#Supplier`** or **`#Lab`** role tag.

---

## 6. Maintainer Tool: Master Preset Authoring & Data Grid

Maintainers can author, validate, and format master JSON catalog files using the visual Maintainer Tool (`./gradlew :desktop:runMaintainer`).
*   **Sortable Data Grid Architecture**: View presets in a clean tabular grid with 1-click column sorting (`Brand ▲`, `Preset Name`, `Type`, `Mount`, `Preset ID`).
*   **Header-Integrated LOV Filtering**: Click `BRAND ▾`, `TYPE ▾`, or `MOUNT ▾` directly in column header cells to apply multi-criteria filtering.
*   **Tailored Category Forms**: Automatically tailors form inputs per category (e.g., hiding camera fields for darkroom consumables, restricting brands to chemical/hardware manufacturers).
*   **Active File Path Display**: Prominently displays the resolved absolute path (`C:\...\core\data\src\main\resources\presets\cameras.json`).

---

## 7. Station: Digital Contact Sheets & Frame Gap Adjuster

The **Station** workspace provides digital tools for studio analysis.
*   **Digital Contact Sheets**: Interactive grid view of frame thumbnails organized by film roll (6 columns for 35mm, 3 for 120 format).
*   **In-Grid Frame Gap Adjuster**: Interactively insert or remove frame gaps (+1 / -1 offset) to align contact sheets 1-to-1 with physical film rebate numbers.
*   **Printable Proof Sheets**: Generates A4 PDF proof sheets ready for printing or physical binder archiving.

---

## 8. Archive: Backup Vault, XMP Sidecar Exports & DAM Integration

Your complete Fotologue database is stored in native JSON backups (`backup_YYYYMMDD_HHMM.json`).
*   **Single Source of Truth**: The native JSON backup is the canonical, full-fidelity database backup file.
*   **Non-Destructive Merge Import**: Re-import historical `Roll.Frame ID` records without wiping local data or custom user gear.
*   **ISO 16684 (XMP) Sidecar Exports**: Export exposure metadata directly into Lightroom, Capture One, digiKam, or darktable, matching RAW scan sequences (*Fujifilm `_DSF`, Canon `IMG_`, Nikon `DSC_`, Sony `_DSC`*).

---

## 9. For Directors of Photography (DPs) & Digital Artists

> *"Discipline for Digital, Freedom for Film"*

Fotologue provides Directors of Photography (DPs), hybrid cinematographers, and digital artists with:
1. **Exposure Decision Auditing**: Document why aperture, shutter speed, ISO, or ND filter choices were made on location.
2. **RAW & Scanner Sequence Alignment**: Automatically matches exported XMP sidecars to digital camera file naming sequences (*Fujifilm `_DSF`, Canon `IMG_`, Nikon `DSC_`, Sony `_DSC`*).
3. **Smart Assignment Kits**: Load pre-configured camera body, lens, and filter templates for fast-paced commercial or film set shoots.
4. **Client Privacy**: Zero cloud telemetry—keep sensitive set photos and location logs 100% on-device.

👉 **Read the complete [WIIFY Guide for DPs, Digital & Hybrid Photographers](dp_and_digital_photographers_guide.md)** for in-depth workflow breakdowns and feature comparisons.
