# Video Tutorial Script & Walkthrough Outline 🎬📹

**Title:** Fotologue — A Photographic Catalogue Suite (Overview & Module Deep Dive)  
**Target Duration:** 5-Minute Main Overview + 4-Part Module Walkthroughs  
**Presenter:** Fotologue Lead Architect / Instructor  

---

## 🎬 Part 1: Main Application Overview (Duration: 5 Minutes)

### [0:00 - 0:45] Intro & The Core Philosophy
*   **Visual**: Presenter on screen in studio / darkroom holding a film camera and smartphone.
*   **Script**:
    > *"Welcome to Fotologue — Foto plus Catalogue. As analogue and hybrid photographers, we know the pain of paper shot lists and darkroom notebooks. Fotologue is a high-precision, privacy-first mobile application built around the 4-Stage Division of Labour: Field, Lab, Station, and Archive. It gives analogue photographers freedom and digital photographers discipline."*

### [0:45 - 1:45] The Application Staging Portal
*   **Visual**: Screen recording of the **Application Staging Portal (`HubScreen`)** showing the 4 module cards: `🟢 FIELD`, `🧪 LAB`, `🔬 STATION`, `🏛️ ARCHIVE`.
*   **Script**:
    > *"When you launch Fotologue, you are greeted by the Application Staging Portal. Depending on what stage of work you're doing today, you choose where to land: logging exposures in the Field, processing chemistry in the Lab, reviewing contact sheets at the Station, or managing master backups in the Archive."*

### [1:45 - 2:45] Field & Lab Highlights
*   **Visual**: Cut to Field Light Metering preview, then switch to Fotologue Lab Inventory & Stock Lots.
*   **Script**:
    > *"In the Field, calibrate your light meter against an 18% grey card, select a Smart Kit, and log your exposure in seconds. In the Lab, manage your wet darkroom inventory, track stock purchases from vendor order to darkroom usage, and log chemistry capacity."*

### [2:45 - 3:45] Station & Archive Highlights
*   **Visual**: Cut to Digital Contact Sheet grid with 1-to-1 rebate gap adjustment, then show JSON Backup Vault.
*   **Script**:
    > *"At the Station, review digital contact sheets and adjust frame gaps to match physical film rebates. In the Archive, your database is protected in a canonical native JSON vault, with non-destructive merge imports and XMP sidecar exports for Lightroom and digiKam."*

### [3:45 - 5:00] Outro & Getting Started
*   **Visual**: On-screen link to `docs/user_guide.md` and `docs/dp_and_digital_photographers_guide.md`.

---

## 🎬 Part 2: Module Deep Dive Scripts

### Walkthrough 1: FIELD Module (5 Mins)
1. **Light Meter Calibration**: 2-stage Noise Floor & Signal Gain tuning against 18% grey card.
2. **Relational Gear Library**: Cameras, Fixed Lenses (TLRs), Accessories, and Smart Kits.
3. **1-Tap Exposure Logging**: GPS location, aperture, shutter speed, ISO, and 3-Axis Tags (`Subject • Content • Intent`).

### Walkthrough 2: LAB Module (6 Mins)
1. **Master Item Catalogue & Stock In-Flow**: Adding Item Specifications (*Major/Minor Units: 1 Box = 25 sheets*); tracking stock status (`🚚 IN_TRANSIT` $\to$ `🟢 ON_HAND` $\to$ `🟡 IN_USE`).
2. **Active Suppliers & Issue Tracker**: Google Maps store location pin; logging vendor calls & order back-issues.
3. **Film Processing Session**: Selecting developer chemistry lot, setting temp/dilution, and running the live development countdown timer.

### Walkthrough 3: STATION Module (4 Mins)
1. **Digital Contact Sheet Navigation**: Filtering by film roll session; inserting/removing frame gaps (+1 / -1 offset).
2. **Enlarger Printing Session**: Logging paper stock lot, enlarger lens/aperture, exposure time, grade filter, and dodge/burn map notes.
3. **Reflection & Error Classification**: Classifying rejects between *System Errors* and *Photographer Learning*.

### Walkthrough 4: ARCHIVE Module (4 Mins)
1. **Canonical Native JSON Vault**: Exporting full-fidelity database backup files.
2. **Non-Destructive Merge Import**: Re-importing historical `Roll.Frame ID` records from cloud archives without thumbnail bloat.
3. **XMP Sidecar Exports**: Matching exported `.xmp` files to digital camera RAW sequences (*Fujifilm `_DSF`, Canon `IMG_`, Nikon `DSC_`*).
