# Fotologue 📸🎞️

**Version:** 1.0.18 | **Last Updated:** September 2026 | **Audience:** Photographers, Directors of Photography (DPs) & Developers

**A Photographic Catalogue Suite — Equally Empowering All Painting-With-Light Artists.**

Fotologue (*Foto* + *Catalogue*) is a high-precision, privacy-first mobile application designed to bridge physical film shooting, wet darkroom craftsmanship, and lifetime digital archiving. Built around the **4-Stage Division of Labour (`Field → Lab → Station → Archive`)**, Fotologue replaces paper shot lists and darkroom notebooks with a robust, metadata-rich catalogue ecosystem.

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
Smart Gear Kits      Equipment Maintenance      Analytics          Vault / XMP Export
```

### 1. 🟢 FIELD (In-situ Exposure & Observation)
*   **Tactile Field Logging**: Record Who, What, When, Where, Why, and How for every exposure in seconds.
*   **Pro Light Meter**: Calibrated spot/centre-weighted light metering with live C-41, E-6, and B&W pre-visualization previews.
*   **Relational Gear Library**: Cameras (35mm, Medium Format, Half-Frame, Large Format, Digital Cinema), Lenses, Viewfinders, Teleconverters, Filters, and Flashes.
*   **Smart Kits**: One-tap loading for favourite Camera + Lens + Film/Digital Sensor + Accessory combinations.

### 2. 🧪 LAB (Wet Darkroom Processing & Inventory)
*   **Master Item Catalogue**: Define consumable specifications (Chemistry, Paper, Safety Gear, Supplies) with quantifiable inner yield capacities (*e.g. 1 Box of 25 sheets*).
*   **Physical Stock Lots**: Track stock in-flow from vendor to darkroom (*`🚚 IN_TRANSIT` $\to$ `🟢 ON_HAND` $\to$ `🟡 IN_USE` $\to$ `🔴 EXHAUSTED`*).
*   **Supplier Directory**: Curated vendor contacts (*Rewind Photo Lab, Vanbar, Ikigai, Digital Camera Warehouse*) with active "In Use" filters.
*   **Maintenance Screen**: Full-screen LOV management for Storage Locations (*Chemical Shelf, Darkroom Fridge, Paper Safe*), Manufacturers, Categories, and Units.

### 3. 🔬 STATION (Studio Research, Analytics & Reflection)
*   **Digital Contact Sheets**: Interactive grid views organized by film roll / digital shoot session with 1-to-1 rebate frame-gap alignment.
*   **Performance Logs & Knowledge Base**: Track frame quality (*Keepers vs. Rejects*) and classify errors between **System Errors** (*Expired Chemistry, Fogged Paper, Light Leak*) and **Photographer Learning** (*Framing, Exposure, Focus*).
*   **Printable PDF Proof Sheets**: A4 PDF proof sheet generator ready for printing or binder archiving.

### 4. 🏛️ ARCHIVE (Lifetime Preservation & Master Vault)
*   **Canonical Native JSON Vault**: Single-source-of-truth backup files (`backup_YYYYMMDD_HHMM.json`) containing the full relational data graph.
*   **Non-Destructive Merge Import**: Re-import historical `Roll.Frame ID` records from Google Drive / Base Camp archives without thumbnail disk bloat.
*   **ISO 16684 (XMP) Export**: Generates industry-standard sidecars with 3-Axis Tag hierarchies (`Subject`, `Content`, `Intent`) for desktop DAMs (digiKam, Lightroom, darktable).

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
*   **100% Local-First**: No accounts, no cloud syncing, and no external tracking. Your data never leaves your device.
*   **User Sovereignty**: All shooting data, locations, notes, and stock records belong to the user.

---

## 🧪 Quality Assurance

Fotologue is built to professional engineering standards.
*   **Unit Tests**: 64 core logic unit tests covering calibration math, session management, camera-gear cross-referencing, macro copy preset formatting, PDF proof sheet generation, Room migrations v29 to v48, and JSON merge imports.
*   **Verification**: Field-tested on Pixel devices and modern system emulators.
