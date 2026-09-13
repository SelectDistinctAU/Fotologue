# Fotologue User Guide 📖📸

**Version:** 1.0.17 (Build 18) | **Last Updated:** September 2026 | **Audience:** All Fotologue App Users

Welcome to the **Fotologue User Guide**. This guide covers day-to-day operations, feature walkthroughs, light metering, taxonomy organization, and desktop archival exports.

---

## Table of Contents

1. [Quick Orientation & Onboarding](#1-quick-orientation--onboarding)
2. [Gear Library & Equipment Associations](#2-gear-library--equipment-associations)
3. [The 3-Axis Tag Taxonomy (Subject • Content • Intent)](#3-the-3-axis-tag-taxonomy-subject--content--intent)
4. [Smart Face-Aware Detection vs. Named People Tags](#4-smart-face-aware-detection-vs-named-people-tags)
5. [Light Metering & Pro Calibration Wizard](#5-light-metering--pro-calibration-wizard)
6. [Digital Contact Sheet & Frame Gap Adjuster](#6-digital-contact-sheet--frame-gap-adjuster)
7. [Camera Macro Copying & Scanner Matching](#7-camera-macro-copying--scanner-matching)
8. [Printable PDF Proof Sheets](#8-printable-pdf-proof-sheets)
9. [Backups & Privacy](#9-backups--privacy)
10. [Release Focus & Future Roadmap (v2.0)](#10-release-focus--future-roadmap-v20)

---

## 1. Quick Orientation & Onboarding

Fotologue is a **100% local-first** exposure log and light meter companion. No cloud accounts or logins are required.
* **First Launch**: The Welcome screen guides you through setting up your first Camera, Lens, and Film Stock.
* **Quick Settings**: Easily toggle automatic thumbnails, on-device face detection, and picture export sizes directly from the Welcome or Settings screens.

---

## 2. Gear Library & Equipment Associations

Manage your entire arsenal in the **Gear Library** (`GearLibraryScreen`).

* **Equipment & Kits**: Manage Cameras, Lenses, Films, Camera Kits, and Accessories (Teleconverters, Filters, Flashes, Viewfinders).
* **Fixed-Lens & TLR Auto-Creation**: Adding a TLR (e.g. *Yashica Mat-124G* or *Rolleiflex 2.8F*) automatically creates and links its fixed lens (*Yashinon 80mm f/3.5*) in your database.
* **Smart Camera Kits**: Pre-configure camera, lens, film, and accessory combinations (e.g. *Leica M6 + 35mm Summicron + Tri-X 400*) for one-tap setup during fieldwork.

---

## 3. The 3-Axis Tag Taxonomy (Subject • Content • Intent)

Organise your exposures across 3 distinct axes:

```
1. SUBJECT (Domain)      2. CONTENT (Detail)      3. INTENT (Purpose & Project)
 • Urban / Built          • Architecture           • Study (Exploration / Process)
 • Nature                 • Street                 • Series (Curated Body of Work)
 • Events                 • Wildlife               • Documentation (Journal)
 • People                 • Portrait               • Commission (Client Assignment)
```

### Organizing Book Projects or Exhibitions
Create custom project sub-tags under **Series** (e.g., `Series > Urban Decay` or `Series > London Monoliths`). When exported, these appear in Lightroom or Capture One as `Intent|Series|Urban Decay` for instant smart collection filtering.

---

## 4. Smart Face-Aware Detection vs. Named People Tags

Understanding how Fotologue handles human subjects:

### Face-Aware Detection (On-Device ML)
* **What It Does**: Scans live camera viewfinder frames in real-time using Google ML Kit to detect whether a **human face is present** in the shot.
* **How It Helps**: When a face is detected:
  1. The light meter adjusts exposure suggestions to favor skin tones.
  2. The log creation screen automatically highlights **People** category tags.
* **Privacy & Biometrics**: Face detection is **100% ephemeral and local**. Fotologue stores **no biometric face hashes, templates, or facial recognition profiles**.

### Named People Tags
* **How to Tag Specific Individuals**:
  1. In the **Gear Library**, navigate to **Subjects > People** (or **Taxonomy & Tags**).
  2. Add custom sub-tags for specific individuals (e.g. `People > John`, `People > Sarah`).
  3. When logging a shot detected with a human, select your custom name chip (*John* or *Sarah*) in one tap.

---

## 5. Light Metering & Pro Calibration Wizard

### Metering Modes & Viewfinder
* **Spot & Centre-Weighted**: Toggle between Spot metering for specific highlights/shadows or Centre-Weighted for general scenes.
* **Film Look Pre-Visualization**: Live-view pre-visualises contrast using **B&W**, **C-41 (Colour Negative)**, or **E-6 (Slide)** profiles.

### Pro Calibration Wizard (`Settings > EV Calibration`)
1. **Stage 1 (The Void)**: Measure sensor "Noise Floor" with a covered lens.
2. **Stage 2 (The Balance)**: Calibrate sensitivity against an 18% grey card.

---

## 6. Digital Contact Sheet & Frame Gap Adjuster

Open the **Digital Contact Sheet** via the top bar icon on the Timeline.

* **Format-Adaptive Grid**: 6 columns for 35mm, 3 columns for 120 format.
* **Visual Scene Fingerprints**: Scene thumbnails attached to logs let you easily match digital scan files to field logs.
* **In-Grid Gap Adjuster**: Tap any skipped slot (`#04 Skipped / Gap`) or active frame to insert or remove frame gaps (+1 / -1 offset), keeping your digital contact sheet 1-to-1 aligned with physical film rebate numbers.

---

## 7. Camera Macro Copying & Scanner Matching

When photographing film negatives with a DSLR/mirrorless macro setup or flatbed scanner:

### Camera Presets
Auto-name exported `.xmp` sidecar files (e.g. `_DSF1024.xmp`) to match RAW camera scan file sequences:
* **Fujifilm RAW**: `_DSF` prefix, 4 digits (`_DSF1024.xmp`).
* **Canon RAW**: `IMG_` prefix, 4 digits (`IMG_4821.xmp`).
* **Nikon RAW**: `DSC_` prefix, 4 digits (`DSC_0050.xmp`).
* **Sony RAW**: `_DSC` prefix, 5 digits (`_DSC00050.xmp`).

Save custom macro copy presets in **Settings > Camera Macro Copying Presets** for one-tap loading during exports.

---

## 8. Printable PDF Proof Sheets

Export styled A4 PDF contact proof sheets complete with:
* **Header**: Camera, Film Stock, Roll Number, Exposure Count, Date, Creator Name, and Copyright notice.
* **Darkroom Grid**: Scene thumbnails, frame rebate badges (`#01`, `#02`), and exposure settings (`1/125 @ f/8`).
* **Usage**: Print physical proof sheets for negative storage sleeves or client archival binders.

---

## 9. Backups & Privacy

* **100% Local Storage**: No accounts, analytics, or remote telemetry.
* **Full SAF Backup & Restore**: Create full JSON backups or restore databases via standard Android Storage Access Framework (SAF) in **Settings**.

---

## 10. Release Focus & Future Roadmap (v2.0)

### Current Release Focus (v1.0.x)
The feature set for this major release is **finalized and feature-frozen**. The primary focus for v1.0.x is comprehensive QA testing, community verification, and validating that all metering, cataloguing, cross-referencing, and XMP export features operate with 100% precision.

### Next Major Release Roadmap (v2.0)

#### 1. Bluetooth Hotshoe Flash Trigger Hardware (`Fotologue Trigger`)
We are actively developing a custom camera-top hardware device that connects to your Android phone via Bluetooth Low Energy (BLE):
* **Hotshoe Flash Signal Capture**: Mounts to your camera's hotshoe or PC-sync port. When a photo is taken, the flash signal triggers an instant baseline log in Fotologue.
* **Instant GPS Precision**: Captures exact GPS location coordinates at the precise millisecond of exposure.
* **Smart Auto-Increment**: Automatically increments the roll frame counter using your last log setup as a baseline.
* **Multi-Camera Field Timelines**: When operating multiple cameras in the field, each physical trigger device broadcasts its own unique camera ID, maintaining independent, parallel log timelines for each camera body in real-time.

#### 2. Digital Camera Cataloguing & Taxonomy Integration
While modern digital cameras automatically capture EXIF exposure settings (aperture, shutter speed, ISO), they lack Fotologue's rich **3-Axis Tag Taxonomy (Subject • Content • Intent)** and field notes.
* **Digital Camera Integration**: v2.0 will extend cataloguing and Bluetooth hotshoe triggering to digital cameras, bridging digital shooting workflows with Fotologue's fine-art taxonomy (*Series*, *Study*, *Book Projects*) and location history.
