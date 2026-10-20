# Fotologue Desktop & Full Image Lifetime Archival Strategy Whitepaper 📄🖥️

**Version:** 1.0.17 (Build 18) | **Last Updated:** September 2026 | **Audience:** Product Strategy & Core Engineering Team

---

> [!IMPORTANT]
> **Privacy-Preserving User-Owned Sync Principle**: To maintain Fotologue's core commitment to privacy and data sovereignty, cloud backup/sync must be **100% user-owned** (via user-configured Google Drive, WebDAV, Dropbox, or local LAN sync). No proprietary user accounts, tracking, or vendor lock-in servers.

---

## Executive Summary

Fotologue began as a mobile field-logging tool. Expanding into a **Desktop Application ("Fotologue Studio")** and **Private Cloud Sync** unlocks the ability to track an image across its **Full Lifetime Pipeline**: from field exposure, to darkroom printing, to gallery exhibition, print editioning, and archival sales.

---

## 1. The 4 Pillars of the Full Image Lifetime Pipeline

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    THE FULL IMAGE LIFETIME PIPELINE                         │
├─────────────────┬───────────────────┬───────────────────┬───────────────────┤
│ 1. CREATION     │ 2. DIGITISATION   │ 3. PRINTING       │ 4. PROVENANCE     │
│ (Mobile Field)  │ (Scan & XMP)      │ (Darkroom / Lab)  │ (Exhibition/Sale) │
├─────────────────┼───────────────────┼───────────────────┼───────────────────┤
│ • Exposure Log  │ • Macro Presets   │ • Enlarger Setup  │ • Print Editions  │
│ • GPS Location  │ • XMP Sidecars    │ • Paper & Grade   │ • Gallery Log     │
│ • Light Meter   │ • Digital Contact │ • Exposure Secs   │ • Sales & Price   │
│ • 3-Axis Tags   │   Sheets          │ • Dodge/Burn Map  │ • Archival Sleeve │
└─────────────────┴───────────────────┴───────────────────┴───────────────────┘
```

### Pillar 1: Creation (Mobile App Field Capture)
* **Role**: Mobile app acts as the ultra-fast field capture tool (light metering, GPS location, hotshoe triggers, 3-Axis Taxonomy).

### Pillar 2: Digitisation & Contact Sheets
* **Role**: Scan-to-log matching, camera macro copy presets (`_DSF1024.xmp`), in-grid gap adjustment, and PDF proof sheet generation.

### Pillar 3: Darkroom Printing ("Print Recipes")
* **Role**: Track exact darkroom enlarging parameters for fine-art silver-gelatin prints:
  * **Enlarger & Lens**: Enlarger head height, lens aperture (e.g. *Rodagon 80mm @ f/11*).
  * **Paper Stock**: Brand, size, surface (*Ilford Multigrade FB Glossy 11x14"*).
  * **Multigrade Filter / Exposure**: Base exposure seconds, split-grade filter values (Grade 00 + Grade 5).
  * **Dodge & Burn Maps**: Interactive diagram/notes recording dodging and burning zones.

### Pillar 4: Provenance, Editions & Sales Tracking
* **Role**: Manage the physical prints after production:
  * **Edition Numbers**: Track limited editions (*Edition 02/10* + 2 Artist Proofs AP1, AP2).
  * **Exhibition History**: Log gallery exhibitions, framing specs, and display locations.
  * **Sales & Collector Records**: Record collector sales, invoice references, prices, and archival certificate of authenticity.

---

## 2. Private User-Owned Cloud Sync Architecture

To enable syncing between the mobile app and desktop application without compromising privacy:

```
┌────────────────────────┐                             ┌────────────────────────┐
│  FOTOLOGUE MOBILE APP  │                             │   FOTOLOGUE DESKTOP    │
│  (Field Capture Tool)  │                             │   ("Fotologue Studio") │
└───────────┬────────────┘                             └───────────┬────────────┘
            │                                                      │
            │          ┌────────────────────────────────┐          │
            └────────► │  USER'S PRIVATE CLOUD / LAN    │ ◄────────┘
                       │  • Google Drive / iCloud / SAF │
                       │  • WebDAV / Self-Hosted S3     │
                       │  • Encrypted SQLite Database   │
                       └────────────────────────────────┘
```

* **Encrypted Database Sync**: The Room/SQLite database is encrypted and synced directly to the user's private storage (Google Drive, iCloud, WebDAV, or local Wi-Fi LAN sync).
* **Zero Vendor Lock-In**: The user owns the database file and can access it anytime offline on both mobile and desktop.

---

## 3. Desktop Application Architecture ("Fotologue Studio")

### Framework Options:
1. **Compose Multiplatform (Desktop)**:
   * Uses Kotlin and Jetpack Compose for Desktop (macOS, Windows, Linux).
   * **90%+ Code Reuse**: Shares the exact same Room database entities, DAOs, repository logic, and ViewModels with the Android mobile app!
2. **Electron / Web App**:
   * HTML/JS/CSS frontend connected to local WebDAV/SQLite database.

### Key Desktop Views in "Fotologue Studio":
* **Lightroom / Capture One Sidecar Inserter**: Drag-and-drop RAW scan folders to automatically inject Fotologue XMP sidecars.
* **Full-Screen Digital Contact Sheet**: High-res multi-monitor contact sheet view with lightbox comparison.
* **Darkroom Print Recipe Editor**: Visual dodging/burning map editor for darkroom prints.
* **Edition & Sales Ledger**: Portfolio manager for limited edition prints, gallery exhibitions, and archival binder tracking.
