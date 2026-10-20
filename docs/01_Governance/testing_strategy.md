# Fotologue: Professional QA Test Plan & Strategy

**Version:** 1.0.17 (Build 18) | **Last Updated:** September 2026 | **Audience:** Testers, QA Engineers & Reviewers | **Test Suite:** 58 Automated Unit Tests Passing

---

## 1. App Objective
**Fotologue** is a specialized digital companion and archival suite for analog (film) photographers. In the analog world, physical cameras do not record EXIF metadata. Fotologue bridges this gap by enabling photographers to log every shot's technical parameters—aperture, shutter speed, lens, film stock, accessories, GPS coordinates, and artistic intent—producing industry-standard metadata sidecars for development and desktop archiving (Lightroom, Capture One, Darktable).

### Design Intent
- **Speed**: Field interactions take seconds between shots.
- **Privacy**: No cloud. No accounts. 100% local-first architecture.
- **Precision**: Professional light metering calibrated to hardware standards.
- **Archival Integrity**: Full 3-axis taxonomy (Subject • Content • Intent) and 1-to-1 scan-to-log matching.

---

## 2. Core Feature Map
1. **The Gear Library & Taxonomy**:
   * **⚙️ Equipment & Kits**: Cameras, Lenses, Films, Accessories (Viewfinders, TCs, Filters, Flashes), and Camera Kits.
   * **🏷️ Taxonomy & Tags**: 3-Axis classification (**Subject • Content • Intent**).
2. **The Light Meter**: Live spot/centre-weighted metering with B&W, C-41, E-6 previews.
3. **The Timeline**: Chronological shot log with interactive maps and export badges (`[Z]`, `[C]`, `[J]`, `[X]`).
4. **Digital Contact Sheet View**: Interactive A4 contact sheet grid with in-grid gap insertion/removal (+1 / -1 offset).
5. **Printable PDF Proof Sheet Exporter**: High-resolution A4 darkroom proof sheet generation.
6. **Camera Macro Copy Presets**: Auto-names `.xmp` sidecars (`_DSF1024.xmp`) to match RAW camera scan sequences.
7. **Pro Calibration Wizard**: 2-stage hardware tuning (*The Void* & *The Balance*).

---

## 3. Test Scripts

### Script A: First-Run, Welcome & Quick Settings
**Steps:**
1. Clear App Data or perform a fresh install.
2. Launch the app. Observe the "Welcome" screen.
3. Verify top bar contains the **Settings** icon.
4. Verify "Quick Settings" card contains "View All Settings" link.
5. Grant Location and Camera permissions when prompted.
6. Complete Equipment Setup (Add Camera, Lens, Film).

**Expected Behaviour:**
- App grants permissions without crashing.
- Equipment Setup steps mark as "Complete".
- "Let's Go!" button enables upon completing setup.

---

### Script B: TLR & Fixed-Lens Auto-Creation
**Steps:**
1. Go to **Gear Library > Cameras > Add Camera**.
2. Search or select a TLR camera (e.g., **Yashica Mat-124G** or **Rolleiflex 2.8F**).
3. Observe Camera Type (`TLR (Twin Lens Reflex)`).
4. Save the camera.
5. Navigate to the **Lenses** tab in Gear Library.

**Expected Behaviour:**
- Camera is saved with format `120 (Medium Format)`.
- A matching fixed lens (**Yashinon 80mm f/3.5**) is automatically created and linked to the camera.

---

### Script C: Missing Film Prompting
**Steps:**
1. Select a 120 Medium Format camera (e.g. *Yashica Mat-124G*).
2. Ensure no 120 film stock exists in your library.
3. Tap **Add Log** (+).
4. Select the 120 camera. Observe the **Film** field.

**Expected Behaviour:**
- OutlinedTextField displays *"No matching film in library"*.
- A warning card appears below: *"No film stock matching this camera format in your library. Tap here to add a film."*
- Tapping the card opens **Add Film Screen** to add 120 film stock.

---

### Script D: 3-Axis Taxonomy & Intent Groups
**Steps:**
1. Go to **Gear Library**.
2. Observe drawer list is split into two headers: **⚙️ EQUIPMENT & KITS** and **🏷️ TAXONOMY & TAGS**.
3. Select the **Intent** tab. Verify header reads **"Intent Groups"**.
4. Tap **+** and create a new project tag under **Series** (e.g. `Urban Decay`).
5. Open **Add Log Screen** and expand Intent.
6. Select **Series** from the dropdown, then select **Urban Decay** chip.
7. Save the log.

**Expected Behaviour:**
- Tag `Urban Decay` is stored as `category = Intent` under parent `Series`.
- XMP export contains `Intent|Series|Urban Decay` in `lr:hierarchicalSubject`.

---

### Script E: Digital Contact Sheet & In-Grid Gap Insertion
**Steps:**
1. Open the Timeline and tap the **Digital Contact Sheet** icon (`gallery_thumbnail_24px`) in the top bar.
2. Select an active film roll. Observe the grid layout (6 columns for 35mm, 3 for 120 format).
3. Locate a skipped frame slot (e.g., `#04 Skipped / Gap`).
4. Tap the skipped card `#04`.
5. Select **"Insert Additional Gap (+1 Offset)"**.
6. Observe subsequent frames (#05–36) shift right by 1 column.
7. Tap the card again and select **"Remove Gap / Pull Frames Back (-1 Offset)"**.

**Expected Behaviour:**
- The grid updates dynamically, maintaining 1-to-1 alignment with physical film rebate numbers.
- Tapping *"Log Exposure for Frame #N"* opens `AddLog` pre-filled for that slot.

---

### Script F: Camera Macro Copy Presets & XMP Export
**Steps:**
1. Open **Settings > Camera Macro Copying Presets**.
2. Verify default presets (*Fujifilm _DSF*, *Canon IMG_*, *Nikon DSC_*, *Sony _DSC*) are present.
3. Add a custom preset (e.g. *"Leica M Macro"*, prefix `L1`, 5 digits).
4. Go to **Digital Contact Sheet > Export (Share)**.
5. Enable **Macro Copy Filename Matching**.
6. Select *"Fujifilm RAW (_DSF)"* from the Load Preset dropdown.
7. Set Start No. to `1024` and export ZIP.

**Expected Behaviour:**
- Exported ZIP contains sidecars named `_DSF1024.xmp`, `_DSF1025.xmp`, etc., matching camera RAW scan file sequences.

---

### Script G: Printable A4 PDF Proof Sheet Exporter
**Steps:**
1. In the Digital Contact Sheet, tap **Export (Share)**.
2. Select **"Export as Printable PDF Proof Sheet"**.
3. Choose a destination and save the `.pdf` file.
4. Open the resulting PDF.

**Expected Behaviour:**
- Generates an A4 PDF proof sheet with darkroom styling.
- Header displays Camera Name, Film Stock, Roll #, Date, Creator, and Copyright.
- Grid renders scene thumbnails, frame rebate badges (`#01`), and exposure metadata (`1/125 @ f/8`).

---

### Script H: Pro Calibration Wizard
**Steps:**
1. Go to **Settings > EV Calibration > Launch Pro Calibration Wizard**.
2. **Stage 1 (The Void)**: Cover the camera lens completely. Tap "Measure Black Point."
3. **Stage 2 (The Balance)**: Point camera at a neutral 18% grey card or neutral wall. Tap "Calibrate Sensitivity."
4. Complete wizard.

**Expected Behaviour:**
- EV Calibration updates with non-zero Noise Floor and Signal Gain multipliers.

---

## 4. System Red Flags
File a high-priority bug report if any of the following occur:
- **IME / Keyboard Overlap**: Keyboard obscures active input fields.
- **Race Condition**: Light Meter flickers or freezes during live metering.
- **Data Leak**: Prompts for accounts, logins, or remote server connections (app is 100% local-first).
- **Locale / Translation Failure**: Missing string resources or raw keys appearing in non-English locales.
