# Fotologue 📸🎞️

**Version:** 3.0.0 | **Last Updated:** October 2026 | **Audience:** Photographers, Directors of Photography (DPs), Darkroom Printers, Maintainers & Developers

**A Photographic Catalogue Suite — Equally Empowering All Painting-With-Light Artists.**

Fotologue (*Foto* + *Catalogue*) is a high-precision, privacy-first cross-platform application designed to bridge physical film shooting, wet darkroom craftsmanship, and lifetime digital archiving. Built around a **Modular Operational Architecture**, Fotologue replaces paper shot lists and darkroom notebooks with a robust, metadata-rich catalogue ecosystem across Mobile and Desktop.

---

## 🌟 Core Project Guiding Principles

> **"We have no users yet, no sacred cows. This is greenfields and we do not have to incur technology debt. We need progress over perfections, form follows function and the user experience is important. It must be intuitive and simple - which is harder, but ultimately more rewarding for us."**

---

## 🏛️ Modular Application Architecture

Fotologue provides an integrated, non-linear workflow tailored to whatever photographic task you are performing:

```
                          FOTOLOGUE APPLICATION HUB
                                      │
     ┌────────────────────────────────┴────────────────────────────────┐
     ▼                                                                 ▼
1. MOBILE STAGING PORTAL (7 OPERATIONAL MODULES)            2. DESKTOP COMMAND CENTER (4 STAGES)
• Module 1: CAMERA GEAR (Cameras, Lenses, Kits)              • Stage 1: Ingestion & Vault Inflow
• Module 2: TAXONOMY & TAGS (Subject, Content, Intent)       • Stage 2: Master QA & Detail Workbench
• Module 3: FIELD (In-situ Exposure & Zone System)           • Stage 3: Multi-Frame Image Tagging
• Module 4: CONTACTS DIRECTORY (People & Vendors)            • Stage 4: Reference Data Administration
• Module 5: LAB (Wet Processing & Enlarger Recipes)            (14-Category Sortable Data Grid)
• Module 6: STATION (Contact Sheets & Analytics)
• Module 7: ARCHIVE (JSON Vault & XMP Sidecar Exports)
```

---

## 📱 Mobile Staging Portal (7 Specialized Operational Modules)

### 1. ⚙️ CAMERA GEAR
* **82 Master Cameras & 26 Master Lenses**: Pre-loaded catalog covering SLR, Rangefinder, TLR, Medium Format, and Large Format bodies from Leica, Hasselblad, Nikon, Canon, Pentax, Olympus, Yashica, Rolleiflex, and Mamiya.
* **Integrated Fixed Taking Lenses**: Normalized taking lens specs (*Yashinon 80mm f/3.5*, *Canonet 40mm f/1.7*) for TLRs and fixed-lens rangefinders.
* **Complete Accessories Suite**: Teleconverters, Filters with exposure compensation factors (*Hoya ND8* +3 stops, *CPL* +1.5 stops, *Yellow K2* +1 stop), Flashes, Viewfinders, and Kit Bags.

### 2. 🏷️ TAXONOMY & TAGS
* **3-Axis Hierarchical Indexing**: Categorize every exposure across **Subject** (Who/What), **Content** (Scene elements), and **Intent** (Creative purpose).
* **Parent Category Grouping**: Organize Content under parent Subjects (*Urban/built environment*, *Nature*, *People & Portraits*) and Intent under parent Intent Groups (*Study*, *Series*, *Portfolio*, *Commercial*, *Experimental*).
* **Custom Tag Curation**: Create, edit, and reorganize custom tags directly in app.

### 3. 🥾 FIELD (In-situ Exposure Logging & Zone System Spot Metering)
* **Ansel Adams 10-Zone System Viewfinder**: Tap any spot on the live camera viewfinder feed to inspect its exact Ansel Adams Zone (**Zone 0 Deep Shadow $\to$ Zone V 18% Mid-Gray $\to$ Zone X Specular Highlight**).
* **Dynamic Range Contrast Calculator**: Measures total scene contrast range (in EV/Stops) and calculates $N \pm 1$ development recommendations (**$N-1$ Pull** for high contrast $>10$ stops; **$N+1$ Push** for flat contrast $<5$ stops).
* **Real-Time 11-Zone Histogram & Zebra Clipping Overlays**: Live 11-zone histogram graph with animated **Red Zebra Stripes** on blown highlights ($\ge 250$) and **Blue Zebra Stripes** on crushed shadows ($\le 5$).
* **Pro Calibration Wizard (`brightness_auto_24px`)**: 2-stage hardware sensor tuning (*The Void* noise floor & *The Balance* 18% grey card signal gain multiplier).

### 4. 🎴 CONTACTS DIRECTORY (People & Vendor Management)
* **Pillar 2 Directory**: Manage Suppliers, Processing Labs, Repair Technicians, Friends, and Gear Borrowers.
* **Native Phone Contact Import**: 1-tap contact picker imports contacts directly from your phone address book without address book harvesting.

### 5. 🧪 LAB (Wet Darkroom Processing & Enlarger Print Recipes)
* **Film Processing Timer Session**: Multi-stage development wizard (*Developer $\to$ Stop Bath $\to$ Fixer $\to$ Wash*) with auto-recommended times (*HP5+ ID-11* 11m, *Tri-X D-76* 9m45s, *C-41* 3m15s, *E-6* 6m00s) and haptic/audio agitation alerts (**"🔔 AGITATE NOW: 10s"**).
* **Enlarger Print Recipe Engine**: Track paper stock, contrast grade filters (0–5 in half-stop steps), test strip increments, dodge/burn notes, **✓ KEEPER** vs **✕ REJECT** outcomes, controlled **Darkroom Defect Causes** (*Dust/Scratch*, *Exhausted Chemistry*, *Paper Fogged*), and multi-select learning tags with custom tag creation.
* **3-Pillar Custody, Loan & Repair Ledger**: Track gear loans, rentals in, service/repair tickets, pre-arrival orders (*In Transit* $\to$ *Mark Delivered*), and physical storage locations (*Cabinet*, *Shelf*, *Dry Box*, *Vault*).
* **Darkroom Red Shift & White Light Sensor Guard**: 100% zero-white red shift mode with `DarkroomLightGuard` (`Sensor.TYPE_LIGHT`) white light intrusion alerts during active darkroom sessions.

### 6. 🔬 STATION (Studio Analysis & Reflection)
* **Digital Contact Sheets**: Interactive grid views organized by film roll / shoot session with 1-to-1 rebate frame-gap alignment.
* **Printable PDF Proof Sheets**: High-resolution A4/Letter PDF contact sheets ready for printing or binder archiving.

### 7. 🏛️ ARCHIVE (Master Vault & XMP Sidecar Exports)
* **Canonical JSON Vault**: Single-source-of-truth backup files (`backup_YYYYMMDD_HHMM.json`) containing the full relational data graph.
* **Non-Destructive Merge Import**: Re-import historical `Roll.Frame ID` records without thumbnail disk bloat.
* **Adobe XMP Core 5.6 Export**: Generates compliant `.xmp` sidecars with EXIF exposure, GPS coordinates, IPTC creator/copyright, and Lightroom/digiKam 3-level taxonomy paths (`lr:hierarchicalSubject`).

---

## 🖥️ Desktop Command Center (4 Processing Stages)

* **Stage 1: Ingestion & Vault Inflow**: Import ZIP backup archives, process Job Lots, and extract scan thumbnails.
* **Stage 2: Master QA & Detail Workbench**: Full-fidelity Master Rolls, frame inspection with **ISO 3664 Neutral Photo Review Canvas (#505050 Mid-Gray)**, ExifTool geotagging, and XMP sidecar dispatcher.
* **Stage 3: Multi-Frame Image Tagging**: Batch 3-axis taxonomy tagging across multiple frames.
* **Stage 4: Reference Data Administration**: 14-Category Sortable Data Grid for Cameras, Lenses, Films, Kit Bags, Teleconverters, Filters, Flashes, Viewfinders, Contacts, Consumables, Lab Equipment, Storage Locations, Pre-Arrival Orders, and Custody Ledger.

---

## 🎨 Theme Palette Suite & ISO 3664 Photo Canvas

Customizable across Mobile and Desktop with 1-tap live recoloring:
- **System Default**: Dynamic Material You / M3 Dark.
- **Daylight Clean (Light)**: High-contrast bright daylight interface (`#FFFFFF` background, `#1976D2` blue accent, `#1C1B1F` pitch black text).
- **Flat Darcula (IntelliJ)**: Canonical Darcula IDE (`#2B2B2B` surface, `#82AAFF` ice blue accent, `#FFFFFF` crisp text).
- **Flat Mac Dark (macOS)**: Canonical macOS Dark (`#1E1F22` surface, `#64B5F6` luminous sky blue, `#FFFFFF` crisp text).
- **Flat Nord (Arctic Cyan)**: Canonical Nord (`#2E3440` Polar Night, `#88C0D0` Frost cyan, `#ECEFF4` Nord snow white text).
- **Silver Gelatin (B&W)**: High-contrast platinum silver darkroom print (`#141414` charcoal, `#FFFFFF` platinum white text).
- **Warm Sepia (Toned)**: Gentle warm sepia gold (`#D4A373`) on deep dark espresso (`#191512`) with soft warm ivory text (`#F5E6D3`).
- **Red Shift (Night Vision - Mobile Only)**: 100% zero-white crimson red (`#FF0000` / `#330000` on `#000000` pitch black) for astrophotography and wet darkrooms.

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

## 🔒 Privacy & Security

**Privacy is the core of Fotologue's architecture.** See our full [Privacy Policy](PRIVACY_POLICY.md) for complete details.
* **100% Local-First**: No required user accounts, no cloud database tracking, and zero user telemetry.
* **Privacy-First Contact Picker**: Leverages Android's native `PickContact()` contract (zero address book harvesting).
* **Optional HTTPS Cloud Refresh**: Optional 1-click cloud refresh fetches public read-only catalog JSON files directly from GitHub with zero user data transmitted.

---

## 🧪 Quality Assurance

Fotologue is built to professional engineering standards.
* **Unit Tests**: 62 core logic unit tests covering light meter calibration math, Ansel Adams zone system calculations, session management, camera-gear cross-referencing, macro copy preset formatting, PDF proof sheet generation, Room migrations v29 to v50, and JSON merge imports.
* **Verification**: Field-tested on Pixel devices, emulators, and Windows 11 Desktop builds.
