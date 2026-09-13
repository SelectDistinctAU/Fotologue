# Fotologue v2.0 Architecture & Strategy Whitepaper 📄⚙️

**Version:** 1.0.17 (Build 18) | **Last Updated:** September 2026 | **Audience:** Product Strategy & Core Engineering Team

---

> [!IMPORTANT]
> **Centralized Taxonomy Principle**: Fotologue v2.0 will **maintain a unified single-app architecture**. Hybrid photographers shooting both film and digital retain a **single, centralized 3-Axis Tag Taxonomy (Subject • Content • Intent)** across all camera bodies and workflows.

---

## Executive Summary

Fotologue v2.0 introduces hardware-triggered automated logging via the **Fotologue Trigger** (Bluetooth Low Energy hotshoe/PC-sync flash hardware) and extends cataloguing to **Digital Cameras (DSLRs & Mirrorless)**. 

To handle the fundamental differences between film rolls and digital file sequences—without fragmenting the product into separate apps—v2.0 implements a **Unified Dual-Engine Architecture**.

---

## 1. The Unified Dual-Engine Architecture

```
                               ┌───────────────────────────────┐
                               │  UNIFIED FOTOLOGUE DATABASE   │
                               │  • Centralized 3-Axis Taxonomy│
                               │  • Shared Gear Library        │
                               │  • Unified SAF Backups        │
                               └───────────────┬───────────────┘
                                               │
                 ┌─────────────────────────────┴─────────────────────────────┐
                 ▼                                                           ▼
    ANALOGUE CAMERA ENGINE                                     DIGITAL CAMERA ENGINE
    (Film Photography Mode)                                    (DSLR / Mirrorless Mode)
    ───────────────────────                                    ───────────────────────
    • Film Roll.Frame Indexing (`3.12`)                        • Continuous File Counter (`_DSF1024`)
    • Active EV Light Meter + Film Looks                       • Pass-through (Camera records EXIF)
    • Contact Sheet Grid + Gap Adjuster                        • Burst Auto-Batcher (1.5s window)
    • Format Constraints (35mm / 120 / Half)                   • RAW Timestamp XMP Matcher
```

### Why a Single App Wins
* **Centralized Taxonomy**: A photographer working on a project (e.g., `Intent > Series > Urban Decay`) can shoot film on a Leica M6 and digital on a Fujifilm X-T5 during the same session. Both cameras tag into the exact same taxonomy tree.
* **Unified Gear Database**: Lenses, filters, teleconverters, and creator copyright settings remain shared across all gear.

---

## 2. Hardware Specification: `Fotologue Trigger`

The **Fotologue Trigger** is a custom hardware device mounted on the camera hotshoe or connected via PC-sync flash cable.

```
┌────────────────────────┐      Bluetooth LE      ┌────────────────────────┐
│   CAMERA (HOTSHOE /    │  ───────────────────►  │     FOTOLOGUE APP      │
│   PC-SYNC FLASH PORT)  │                        │  • GPS Millisecond Fix │
│   • Pulse Signal       │   Broadcasting BLE     │  • Auto-Route Timeline │
│   • Hardware ID: Cam02 │   Device ID: Cam02     │  • Auto-Increment Frame│
└────────────────────────┘                        └────────────────────────┘
```

### Key Technical Capabilities:
1. **1-to-1 Hotshoe-to-Camera Pairing**:
   * Each physical trigger broadcasts a unique BLE Device ID (`triggerDeviceId`).
   * In the Gear Library, assigning `Cam_02` to a *Hasselblad 500C/M* causes incoming trigger signals to **automatically route to the Hasselblad timeline** without manual camera switching in the app.
2. **Millisecond GPS Location Acquisition**:
   * On flash trigger, Fotologue captures current GPS coordinates at the exact moment of exposure.
3. **Onboard Hardware EEPROM Event Buffer (Offline Queue)**:
   * If the phone screen is locked or Bluetooth connection drops, the trigger buffers up to 100 flash event timestamps onboard.
   * Reconnecting syncs missed flash events with background GPS location logs.
4. **Pulse Debouncing & Strobe Filtering**:
   * A configurable 100ms–500ms software debounce window filters out rapid electrical bounce pulses or strobe flashes.

---

## 3. High-Speed Digital Burst & Batching Engine

Digital cameras can fire at 10 to 30+ frames per second. Logging 30 individual database records for a 1-second burst creates unnecessary clutter.

### Burst Window Auto-Batching
Unless GPS location moves rapidly (e.g. shooting from an airplane), hotshoe triggers occurring within a **1.5-second time window** are automatically grouped into a single **Burst Batch Log**.

```
Trigger Pulses:  [Pulse 1] ── (0.1s) ── [Pulse 2] ── (0.1s) ── [Pulse 3] ── (2.0s Pause) ── [Pulse 4]
                     │                                             │                           │
App Log Created:     └─────────────── BURST BATCH 1 ───────────────┘                       SINGLE SHOT
                     "_DSF1024 – _DSF1026 (3-frame Burst)"                                 "_DSF1027"
```

* **App UI**: Displays as a single card: `_DSF1024–_DSF1026 (3 Shots)` with 1 GPS location and attached 3-Axis Taxonomy tags.
* **XMP Sidecar Export**: Generates matching sidecars (`_DSF1024.xmp`, `_DSF1025.xmp`, `_DSF1026.xmp`) for all RAW files in the burst simultaneously.

---

## 4. Digital File Counter Synchronization

* **Analogue Mode**: Tracks finite rolls (`Roll 3, Frame 12`).
* **Digital Mode**: Tracks continuous card file counters (`_DSF1024` $\to$ `_DSF1025`).
* **Auto-Sync**: When starting a digital session, entering the starting card file number (e.g. `1024`) initializes the continuous counter. Every trigger pulse or burst increments the file counter automatically.

---

## 5. Phased Implementation Roadmap for v2.0

### Phase 1: Hardware Driver & BLE Connectivity
- Integrate BLE scanner and GATT notifications in `BleManager.kt`.
- Support 1-to-1 mapping between `triggerDeviceId` and Camera entities.

### Phase 2: Dual-Engine UI & Centralized Taxonomy
- Add `CameraEngineMode` (`ANALOGUE` vs `DIGITAL`) in `Camera.kt`.
- Adapt `AddLogScreen` and `TimelineScreen` UI based on active camera mode.

### Phase 3: Burst Auto-Batcher & RAW Timestamp Matcher
- Implement time-window burst batching logic in `AddLogViewModel`.
- Implement RAW timestamp matching exporter for digital sidecars.
