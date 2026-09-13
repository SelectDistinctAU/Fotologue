# Fotologue v2.0 Architecture & Strategy Whitepaper 📄⚙️

**Version:** 1.0.17 (Build 18) | **Last Updated:** September 2026 | **Audience:** Product Strategy & Core Engineering Team

---

> [!IMPORTANT]
> **Product Scope & Priority Strategy**:
> * **v2.0 Primary Goal (P0 / Mandatory)**: Analogue Film Cameras (35mm, 120 Medium Format, Half-Frame, Large Format, TLRs, and **Instant Film / Polaroids**) paired with the **Fotologue Trigger** (Hotshoe & PC-Sync BLE Flash Hardware).
> * **v2.0 Secondary / v3.0 Scope**: Digital Camera Cataloguing & Burst Auto-Batching. Included in v2.0 as a secondary module if lightweight; otherwise deferred to v3.0.
> * **Centralized Taxonomy Principle**: A single, centralized **3-Axis Tag Taxonomy (Subject • Content • Intent)** serves all film stocks, instant films, and cameras in one unified database.

---

## Executive Summary

Fotologue v2.0 introduces hardware-triggered automated logging via the **Fotologue Trigger** (Bluetooth Low Energy hotshoe/PC-sync flash hardware) for analogue film cameras, instant film packs (Polaroid / Instax), and custom camera backs.

---

## 1. Analogue & Instant Film Priority (v2.0 Primary Scope)

### A. Supported Analogue Formats & Film Packs
* **35mm Standard & Half-Frame**: 36 / 72 exposures per roll.
* **120 Medium Format**: 12 / 16 exposures per roll depending on frame geometry (6x6, 6x4.5, 6x7, 6x9).
* **Large Format / Sheet Film**: Individual sheet holders (4x5, 8x10).
* **Instant Film / Polaroids**:
  * **Polaroid i-Type / 600 / SX-70**: 8 exposures per film pack.
  * **Fujifilm Instax (Mini / Square / Wide)**: 10 exposures per film pack.
  * **Modular Instant Backs**: Hasselblad, Mamiya, and 4x5 Polaroid/Instax film backs.

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
1. **1-to-1 Trigger-to-Camera Pairing**:
   * Each physical trigger broadcasts a unique BLE Device ID (`triggerDeviceId`).
   * Assigning `Trigger_01` to a *Leica M6* and `Trigger_02` to a *Polaroid SX-70* automatically routes incoming trigger signals to their respective camera timelines in real-time.
2. **Millisecond GPS Location Acquisition**:
   * On flash trigger (or PC-sync flash fire), Fotologue captures exact GPS coordinates at the precise millisecond of exposure.
3. **Onboard Hardware EEPROM Event Buffer (Offline Queue)**:
   * Buffers up to 100 flash event timestamps onboard if Bluetooth connection drops or phone screen is locked.
4. **Pulse Debouncing & Strobe Filtering**:
   * Configurable 100ms–500ms software debounce window filters out strobe chatter or electrical bounce pulses.

---

## 3. Digital Camera Scope & Strategy (v2.0 Secondary / v3.0)

Digital cameras (DSLRs & Mirrorless) record EXIF exposure settings automatically, but lack Fotologue's **3-Axis Tag Taxonomy (Subject • Content • Intent)** and field notes.

### Digital Integration Scope:
* **v2.0 Inclusion**: Added as a lightweight optional module if digital file sequence matching is low-friction.
* **v3.0 Deferred Scope**: Advanced high-speed burst auto-batching (1.5s time-window grouping for 10–30 fps bursts) and automated RAW timestamp matching.

---

## 4. Phased Implementation Roadmap

### Phase 1: Hardware Driver & BLE Connectivity (v2.0 Primary)
- Integrate BLE scanner and GATT notifications in `BleManager.kt`.
- Support 1-to-1 mapping between `triggerDeviceId` and Analogue/Instant Camera entities.

### Phase 2: Instant Film & Analogue Roll Enhancements (v2.0 Primary)
- Add 8-exposure (Polaroid) and 10-exposure (Instax) pack defaults in `Film.kt`.
- Support PC-sync flash triggering for vintage Polaroids and modular instant backs.

### Phase 3: Digital Camera Module (v2.0 Secondary / v3.0)
- Evaluate lightweight digital card file counter (`_DSF1024`) vs. full v3.0 digital burst batcher.
