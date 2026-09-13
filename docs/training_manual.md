# Fotologue Master Training Manual 📖📸

**Version:** 1.0.17 (Build 18) | **Last Updated:** September 2026 | **Audience:** Analog Photographers, Workshop Instructors & Beta Testers

**A Comprehensive Field & Archival Guide for Analog Photographers**

---

## Table of Contents

1. [Core Philosophy & Onboarding](#1-core-philosophy--onboarding)
2. [Gear Library & Equipment Pairing](#2-gear-library--equipment-pairing)
3. [The 3-Axis Tag Taxonomy (Subject • Content • Intent)](#3-the-3-axis-tag-taxonomy-subject--content--intent)
4. [Smart Face-Aware Detection vs. Named People Tags](#4-smart-face-aware-detection-vs-named-people-tags)
5. [Smart Camera Kits](#5-smart-camera-kits)
6. [Field Workflow & Metering Calibration](#6-field-workflow--metering-calibration)
7. [Digital Contact Sheet & In-Grid Gap Adjuster](#7-digital-contact-sheet--in-grid-gap-adjuster)
8. [Camera Macro Copying & Scanner Matching](#8-camera-macro-copying--scanner-matching)
9. [Printable PDF Proof Sheets & Archiving](#9-printable-pdf-proof-sheets--archiving)
10. [Privacy, Backups & Portability](#10-privacy-backups--portability)

---

## 1. Core Philosophy & Onboarding

* **Zero Cloud Dependence**: No user accounts, no logins, no cloud tracking. All databases, preferences, and thumbnails stay stored strictly on your device.
* **First-Run Welcome Screen**: Guides you through adding your first **Camera**, **Lens**, and **Film Stock**. Quick settings allow configuring automatic thumbnails, on-device face detection, and export resolutions up front.

---

## 2. Gear Library & Equipment Pairing

The **Gear Library** organises your equipment and taxonomy into two distinct sections:

```
⚙️ EQUIPMENT & KITS              🏷️ TAXONOMY & TAGS
 ├── Camera Kits                  ├── Subjects (Who / What domain?)
 ├── Cameras                      ├── Content (Specific details?)
 ├── Lenses                       └── Intent (Purpose / Project?)
 ├── Film Stocks
 └── Accessories (TCs, Filters, Flashes, Viewfinders)
```

### Camera Cross-Referencing & Fixed Lenses
* **Camera Associations**: Link interchangeable lenses, teleconverters, filters, flashes, and viewfinders to specific camera bodies.
* **Fixed-Lens Cameras & TLRs**: Adding a TLR (e.g. *Yashica Mat-124G* or *Rolleiflex 2.8F*) or compact camera automatically creates and pairs its fixed lens (*Yashinon 80mm f/3.5*) in your database.
* **Half-Frame Support**: Selecting *35mm Half Frame* on cameras (e.g., Pentax 17) automatically doubles expected exposure counts (e.g. 36 exp → 72 exp).

---

## 3. The 3-Axis Tag Taxonomy (Subject • Content • Intent)

Fotologue uses a 3-axis taxonomy to classify exposures:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                             3-AXIS TAXONOMY                                 │
├──────────────────────┬──────────────────────┬───────────────────────────────┤
│ 1. SUBJECT           │ 2. CONTENT           │ 3. INTENT                     │
│ (Broad Domain)       │ (Specific Details)   │ (Purpose & Project)           │
├──────────────────────┼──────────────────────┼───────────────────────────────┤
│ • Urban / Built      │ • Architecture       │ • Study (Process/Inquiry)     │
│ • Nature             │ • Street             │ • Series (Curated Body)       │
│ • Events             │ • Wildlife           │ • Documentation (Journal)     │
│ • People             │ • Portrait           │ • Commission (Client Assignment)│
└──────────────────────┴──────────────────────┴───────────────────────────────┘
```

**Building a Book or Exhibition**: Create a custom **Intent Sub-Tag** under **Series** (e.g. `Series > Urban Decay` or `Series > Monoliths`). Attaching this tag exports directly into Adobe Lightroom or Capture One as `Intent|Series|Urban Decay`, allowing instant smart collection grouping for book layouts!

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

## 5. Smart Camera Kits

* **One-Tap Quick Fill**: Pre-configure favourite equipment combinations (e.g. *Leica M6 + 35mm Summicron + Tri-X 400*).
* **Field Speed**: Selecting a Kit on the **Add Log Screen** fills Camera, Lens, Film, ISO, and accessories in a single tap.

---

## 6. Field Workflow & Metering Calibration

### The Pro Calibration Wizard
Transform your phone into a precision light meter via **Settings > EV Calibration**:
1. **Stage 1 (The Void)**: Measure the sensor "Noise Floor" with a covered lens to eliminate internal electronic noise.
2. **Stage 2 (The Balance)**: Calibrate signal gain against an 18% grey card or neutral wall.

### Live Metering & Pre-Visualization
* **Metering Modes**: Toggle between Spot and Centre-Weighted metering.
* **Film Look Simulations**: Pre-visualise scene contrast in live-view with **B&W**, **C-41 (Colour Negative)**, or **E-6 (Slide/Reversal)** colour profiles.
* **Scene Thumbnails**: Capturing a scene thumbnail creates a visual fingerprint attached to the log.

---

## 7. Digital Contact Sheet & In-Grid Gap Adjuster

Open the **Digital Contact Sheet** via the top bar icon on the Timeline.

```
┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐
│ #01 [IMG] │ │ #02 [IMG] │ │ #03 [IMG] │ │ #04 [GAP] │ ◄─ Tap to shift +1 / -1
├───────────┤ ├───────────┤ ├───────────┤ ├───────────┤
│ 1/125 f/8 │ │ 1/250 f/4 │ │ 1/60 f/11 │ │ Skipped   │
└───────────┘ └───────────┘ └───────────┘ └───────────┘
```

### In-Grid Gap Adjuster
If a frame was skipped on film or unlogged:
* **Skipped Frame Placeholder**: Displayed as `#04 Skipped / Gap`.
* **Gap Actions**: Tap any skipped slot or active frame card to:
  1. *"Insert Additional Gap (+1 Offset)"*: Shifts subsequent frame numbers forward by $+1$.
  2. *"Remove Gap / Pull Frames Back (-1 Offset)"*: Shifts subsequent frame numbers back by $-1$ to fill the gap.
  3. *"Log Exposure for Frame #N"*: Opens `AddLog` pre-filled for that exact slot.

---

## 8. Camera Macro Copying & Scanner Matching

When photographing negatives with a DSLR/mirrorless macro setup or flatbed scanner:

To automatically pair XMP sidecars in Adobe Lightroom or Capture One, the XMP sidecar filename **must match the RAW/TIF file name** (e.g. `_DSF1024.xmp` for `_DSF1024.RAF`).

### Setting Up Macro Copying Options
In the Export Sheet or Settings:
1. **Enable Macro Copy Filename Matching**.
2. Select or save a **Camera Preset**:
   * *Fujifilm RAW*: `_DSF` prefix, 4 digits (`_DSF1024.xmp`).
   * *Canon RAW*: `IMG_` prefix, 4 digits (`IMG_4821.xmp`).
   * *Nikon RAW*: `DSC_` prefix, 4 digits (`DSC_0050.xmp`).
   * *Sony RAW*: `_DSC` prefix, 5 digits (`_DSC00050.xmp`).
3. Set **Start File Number** (e.g. `1024`).
4. **Sequence Direction**: Choose *Forward* or *Reverse* (for inverted film strip feeding).

---

## 9. Printable PDF Proof Sheets & Archiving

* **Export as Printable PDF Proof Sheet**:
  Generates an A4 PDF contact proof sheet complete with:
  * **Header**: Camera, Film Stock, Roll Number, Exposure Count, Date, Creator & Copyright notices.
  * **Darkroom Grid**: Format-adaptive layout (6 columns for 35mm, 3 columns for 120 format).
  * **Frame Cards**: High-res scene thumbnails, frame rebate badges (`#01`, `#02`), and exposure metadata (`1/125 @ f/8`, ISO).
  * **Usage**: Print physical proof sheets for negative storage sleeves or client presentation binders.

---

## 10. Privacy, Backups & Portability

* **100% On-Device Storage**: No telemetry, analytics, or external trackers.
* **On-Device Face Detection**: Biometric data is processed in-memory via Google ML Kit and never saved or transmitted.
* **SAF Backup & Restore**: Create full JSON backups or restore complete gear and exposure databases via standard Android Storage Access Framework (SAF) in Settings.
