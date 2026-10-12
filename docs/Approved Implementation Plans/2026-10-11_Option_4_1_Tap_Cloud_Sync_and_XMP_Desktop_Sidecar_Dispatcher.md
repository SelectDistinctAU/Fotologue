# Option 4: 1-Tap Cloud Sync & XMP Desktop Sidecar Dispatcher Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 05:00 AM UTC

---

This plan finalizes **Option 4 (1-Tap Cloud Sync & XMP Desktop Sidecar Dispatcher)** across Mobile and Desktop by validating Adobe & digiKam compliant `.xmp` sidecar exports, camera macro-copying scan file matching rules, and GitHub Master Preset Cloud Sync.

---

## 1. Technical Specifications for XMP Sidecar Dispatcher & Cloud Sync

```
┌─────────────────────────────────────────────────────────────┐
│ MOBILE FIELD CAPTURE ➔ 1-TAP ARCHIVE & XMP SIDE CAR DISPATCH │
│ 📄 _DSF1001.xmp (Adobe XMP Core 5.6 compliant)              │
│  ├── EXIF: f/5.6 • 1/60s • ISO 400 • 80mm • GPS 37.42, -122 │
│  ├── IPTC: Creator • Copyright • Contact Info               │
│  └── 3-Axis Taxonomy: Subject|Nature|Landscape • App|Fotologue│
├─────────────────────────────────────────────────────────────┤
│ DESKTOP DAM INGESTION (digiKam / Lightroom / DxO PhotoLab)  │
│ 🛠️ Stage 3 ExifTool Dispatcher writes metadata into RAWs   │
└─────────────────────────────────────────────────────────────┘
```

### Key Functional Features:
1. **Automatic Adobe & digiKam Compliant XMP Sidecars (`LogEntryXmpExporter.kt`)**:
   - Generates `.xmp` sidecar metadata containing:
     - **EXIF Exposure**: Aperture (`exif:FNumber`), Shutter Speed (`exif:ExposureTime`), ISO (`exif:ISOSpeedRatings`), Focal Length (`exif:FocalLength`), Lens (`aux:Lens`), Camera Make (`tiff:Make`).
     - **Geotagging**: Latitude & Longitude with Direction References (`exif:GPSLatitude`, `exif:GPSLongitude`).
     - **IPTC & Rights**: Creator Name (`dc:creator`), Notes (`dc:description`), Copyright (`dc:rights`), Contact Info (`Iptc4xmpCore:CreatorContactInfo`).
     - **3-Axis Hierarchical Tags**: Lightroom/digiKam 3-level taxonomy paths (`lr:hierarchicalSubject` e.g. `Subject|Nature|Landscape`, `Film|Kodak Portra 400`).
2. **Camera Macro-Copying File Prefix Matching**:
   - Matches camera prefixes (`_DSF1001.xmp`, `IMG_1001.xmp`, `DSC_1001.xmp`) to pair XMP sidecars seamlessly with digital camera RAW/TIF scan files.
3. **1-Tap Cloud Sync & Backup Vault Packaging (`LogEntryZipExporter.kt`)**:
   - Packages `.xmp` sidecars, `metadata.json`, and digital contact sheets into a single ZIP backup vault for cloud syncing and Desktop DAM ingestion.

---

## 2. Proposed Code Changes

- **[MODIFY]** `app/src/main/java/com/fotologue/app/util/LogEntryXmpExporter.kt`
  - Ensure 100% compliance with Adobe XMP Core 5.6 specification.
- **[MODIFY]** `app/src/main/java/com/fotologue/app/ui/screens/ArchiveScreen.kt` & `ContactSheetScreen.kt`
  - Verify 1-tap XMP export and ZIP backup packaging workflows.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `ARCHIVE` $\to$ tap **`Export XMP Sidecars`**.
3. Verify generated `.xmp` file contains valid XML with EXIF exposure, GPS coordinates, creator contact info, and `lr:hierarchicalSubject` tag paths.
4. Launch Desktop App $\to$ open **Stage 3 ExifTool Geotagging & XMP Dispatcher** $\to$ verify XMP sidecars ingest cleanly!
