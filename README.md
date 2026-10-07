# Fotologue 📸🎞️

**Version:** 3.0.0 | **Last Updated:** October 2026 | **Audience:** Photographers, Directors of Photography (DPs), Maintainers & Developers

**A Photographic Catalogue Suite — Equally Empowering All Painting-With-Light Artists.**

Fotologue (*Foto* + *Catalogue*) is a high-precision, privacy-first cross-platform application designed to bridge physical film shooting, wet darkroom craftsmanship, and lifetime digital archiving. Built around the **4-Stage Division of Labour (`Field → Lab → Station → Archive`)**, Fotologue replaces paper shot lists and darkroom notebooks with a robust, metadata-rich catalogue ecosystem.

---

## 🌟 Core Project Guiding Principles

> **"We have no users yet, no sacred cows. This is greenfields and we do not have to incur technology debt. We need progress over perfections, form follows function and the user experience is important. It must be intuitive and simple - which is harder, but ultimately more rewarding for us."**

---

## 🏛️ Application Division of Labour (`Field → Lab → Station → Archive`)

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

### 1. 🟢 FIELD (In-situ Exposure & Observation)
*   **Tactile Field Logging**: Record Who, What, When, Where, Why, and How for every exposure in seconds.
*   **Pro Light Meter**: Calibrated spot/centre-weighted light metering with live C-41, E-6, and B&W pre-visualization previews. Automatic exposure compensation calculation for ND filters (*Hoya ND8* +3 stops), polarizers (*CPL* +1.5 stops), and contrast filters (*Yellow K2* +1 stop).
*   **Relational Gear Library & Cloud Presets**: Instant 1-tap setup covering **82 Master Cameras**, **93 Master Lenses**, Viewfinders, Teleconverters, Filters, and Flashes.
*   **1-Click Cloud Preset Refreshing**: Tap **`[ 🔄 Refresh Master Presets from Cloud ]`** in Settings to fetch live catalog updates over HTTPS directly from GitHub without needing an App Store update.
*   **Smart Kits**: One-tap loading for favourite Camera + Lens + Film/Digital Sensor + Accessory combinations.

### 2. 🧪 LAB (Wet Darkroom Processing & Inventory)
*   **Master Item Catalogue**: 30 Darkroom Chemistry & Paper Presets (*Kodak D-76, Xtol, HC-110; Ilford ID-11, DD-X, Rapid Fixer, Multigrade Papers; Rodinal; Cinestill Cs41 & Cs63; Tetenal*) and 37 Lab Equipment Presets (*Beseler 23C III, Durst M605/L1200, LPL 7700/4500II, Paterson Super System 4, Jobo 1520/2520, GraLab 300, Peak Grain Scope*).
*   **Physical Stock Lots**: Track stock in-flow from vendor to darkroom (*`🚚 IN_TRANSIT` $\to$ `🟢 ON_HAND` $\to$ `🟡 IN_USE` $\to$ `🔴 EXHAUSTED`*).
*   **Contacts Directory & 1-Click Vendor Import**: Import top international and Australian suppliers & processing labs (*B&H Photo Video, Adorama, Analogue Wonderland, Camera Electronic Perth, Leederville Cameras, Fitzgerald Photo, Rewind Photo Lab, Vanbar, Walkens, Hillvale*).

### 3. 🔬 STATION (Studio Research, Analytics & Reflection)
*   **Digital Contact Sheets**: Interactive grid views organized by film roll / digital shoot session with 1-to-1 rebate frame-gap alignment.
*   **Performance Logs & Knowledge Base**: Track frame quality (*Keepers vs. Rejects*) and classify errors between **System Errors** (*Expired Chemistry, Fogged Paper, Light Leak*) and **Photographer Learning** (*Framing, Exposure, Focus*).
*   **Printable PDF Proof Sheets**: A4 PDF proof sheet generator ready for printing or binder archiving.

### 4. 🏛️ ARCHIVE (Lifetime Preservation & Master Vault)
*   **Canonical Native JSON Vault**: Single-source-of-truth backup files (`backup_YYYYMMDD_HHMM.json`) containing the full relational data graph.
*   **Non-Destructive Merge Import**: Re-import historical `Roll.Frame ID` records from Google Drive / Base Camp archives without thumbnail disk bloat.
*   **ISO 16684 (XMP) Export**: Generates industry-standard sidecars with 3-Axis Tag hierarchies (`Subject`, `Content`, `Intent`) for desktop DAMs (digiKam, Lightroom, darktable).

---

## 🛠️ Maintainer Tooling (`:desktop:runMaintainer`)

For catalog maintainers, Fotologue includes a visual **Master Preset Authoring GUI**:
```bash
./gradlew :desktop:runMaintainer
```
- **Sortable Data Grid Architecture**: View and sort presets in a clean tabular grid (`Brand ▲`, `Preset Name`, `Type`, `Mount`, `Preset ID`).
- **Header-Integrated LOV Filtering**: Click column headers directly to filter by Brand, Type, or Mount.
- **Dual-Output Build Task**: Run `./gradlew :core:data:assembleMasterCatalog` to compile all 10 category JSON files into `master_preset_catalog.json` for both Android APK assets and JVM Desktop classpath resources.

---

## 🎬 For Directors of Photography (DPs), Hybrid & Digital Artists

> *"Discipline for Digital, Freedom for Film"*

Fotologue provides digital photographers, DPs, and hybrid artists with **unmatched field metadata control, RAW sequence matching, and zero-trust privacy**:
- 🎯 **Capture Intent at Exposure**: Record creative lighting intent before SD cards are dumped and context is lost.
- ⚡ **RAW & Scanner Sequence Matching**: Auto-names exported `.xmp` sidecars (*e.g. `_DSF1024.xmp`*) to match RAW file sequences from Fujifilm, Nikon, Canon, Sony, or flatbed scanners.
- 🧰 **Assignment Smart Kits**: One-tap pre-loading for commercial editorial, street, or landscape kits.

👉 **Read the full [WIIFY Guide for DPs & Digital Photographers](docs/dp_and_digital_photographers_guide.md)** for detailed feature breakdowns and workflow comparisons.

---

## 🔒 Privacy & Security

**Privacy is the core of Fotologue's architecture.** See our full [Privacy Policy](PRIVACY_POLICY.md) for complete details.
*   **100% Local-First**: No required user accounts, no cloud database tracking, and zero user telemetry.
*   **Privacy-First Contact Picker**: Leverages Android's native `PickContact()` contract (zero address book harvesting).
*   **Optional HTTPS Cloud Refresh**: Optional 1-click cloud refresh fetches public read-only catalog JSON files with zero user data transmitted.

---

## 🧪 Quality Assurance

Fotologue is built to professional engineering standards.
*   **Unit Tests**: 62 core logic unit tests covering light meter calibration math, session management, camera-gear cross-referencing, macro copy preset formatting, PDF proof sheet generation, Room migrations v29 to v48, and JSON merge imports.
*   **Verification**: Field-tested on Pixel devices, emulators, and Windows 11 Desktop builds.
