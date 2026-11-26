# Fotologue Architecture & Decision Log

*This document serves as a persistent record of architectural discussions, decisions, and system designs to ensure context is never lost.*

## Data Architecture & Modularisation Strategy (September 2026)

### Context & Goals
The project is expanding to v3.0 to include a "Lab" component (developing, darkroom, printing) and a "Catalogue" component (archiving, exporting XMP to digiKam). The goal is to modularise the codebase to protect the highly volatile, existing field-capture data (GPS, exposure settings) while building out the new features.

### Naming Conventions & Philosophy
*   **Philosophy:** "Built by a photographer, for photographers, shared freely."
*   **Language:** Australian English norms (`analogue`, `colour`, `catalogue`) are strictly maintained for authenticity.
*   **Module Naming:** We are adopting a professional, action/workflow-oriented naming convention. The term "Stash" was rejected due to Australian cultural connotations (meaning stolen loot).

**Target Module Structure:**
1.  `:feature:stock` (or `:feature:inventory`) - Physical custodianship of unexposed materials.
2.  `:feature:capture` - The act of shooting in the field (Currently residing in the `:app` module).
3.  `:feature:lab` - The wet process (developing, mixing chemistry, printing).
4.  `:feature:catalogue` - The exit point (digitisation, XMP export, archiving).

### Current Data State
*   The application currently uses **Room** (SQLite abstraction).
*   The schema is highly normalised with tables for `LogEntry` (Frames), `Film`, `Camera`, `Lens`, etc.
*   Data is highly volatile (stored in the private app sandbox). JSON export is currently acting as the primary backup mechanism.

### The Execution Plan: The "Strangler Fig" Approach
To protect the existing data and UI, we will not rewrite the `:app` module immediately. Instead:

1.  **Phase 1: Create Shared Infrastructure [COMPLETED]:**
    *   Created `:core:model` module for pure Kotlin domain data classes.
    *   Created `:core:data` module.
2.  **Phase 2: Database Extraction [COMPLETED]:**
    *   Extracted 34 Room Database files, Entities, Repositories, and DAOs from `com.fotologue.app.data` into `com.fotologue.core.data`.
    *   Preserved existing database schema without modifications to avoid user data loss/resets.
    *   Updated `:app` module to depend on `:core:data` and updated all UI, ViewModel, and Test references across the app.
    *   Verified build status: `./gradlew assembleDebug` PASSED (0 errors), `./gradlew test` PASSED (58 unit tests passed).
3.  **Phase 3: Schema Expansion (Append-Only mindset) [COMPLETED]:**
    *   Bumped Room database to Version 37 and enabled schema exports (`exportSchema = true`).
    *   Created `MIGRATION_36_37` to safely create new tables for the Lab without touching existing field data: `lab_equipment`, `lab_chemicals`, `lab_process_events`, `print_recipes`, `lab_storage_locations`, `print_outcomes`.
    *   Added corresponding Room Entities and DAOs under `com.fotologue.core.data.lab`.
    *   Wired new DAOs and migration into `DatabaseModule`.
    *   Verified build status: `./gradlew assembleDebug` PASSED, `./gradlew test` PASSED (58 unit tests passed).
4.  **Phase 4: Build New Features [IN PROGRESS]:**
    *   Created Pick Lists (`LabPresets.kt`) with curated defaults for Equipment (e.g. *Intrepid Compact Enlarger 35mm/120*, *Beseler*, *Paterson Tanks*, *Rodenstock / Schneider / Nikkor lenses*), Chemistry (Ilford ID-11, Multigrade Dev, Rapid Fixer, Kodak D-76/Dektol), and Paper Stocks (Ilford FB/RC, Foma, Adox).
    *   Spun up the isolated **`:feature:lab`** module containing `LabHomeScreen` and `LabHomeViewModel`.
    *   Implemented **Darkroom Red Safelight Mode** toggle (`#FF0000` text/icons on `#000000` background) and oversized touch targets ($\ge 64\text{ dp}$) for darkroom gloves.
    *   Verified build status: `./gradlew assembleDebug` PASSED, `./gradlew test` PASSED (58 unit tests passed).

### UI Consistency & Navigation Alignment (September 2026)
*   **Action Buttons Alignment:** Aligned action buttons across screens to use explicit, clear labels (`+ Equipment`, `+ Chemical`) instead of generic Floating Action Buttons (`+`).
*   **Separated Sub-Pages (TabRow & Counts):** `LabHomeScreen` now mirrors `GearLibraryScreen` by using a `TabRow` to separate **Darkroom Equipment (X)** and **Processing Chemistry (Y)** into dedicated sub-pages with live count badges.
*   **Return to Capture Navigation Icon:** Added an `ic_photo_camera` TopAppBar navigation icon in `LabHomeScreen` allowing one-tap return to Fotologue Capture. In Safelight mode, this icon is tinted strictly with `#FF0000` to prevent light pollution.
*   **Philosophy:** Focus on immediate, practical inventory needs for the field device (Pixel 8a): What is in stock, how much volume is on hand, do I need to reorder anything, who did I get it from last time, and how much did it cost?
*   **Volume Unit Selection:** Added Filter Chips for `L`, `mL`, `fl oz`, `gal`, `pt`.
*   **Australian & Global Supplier Pick List:** Integrated reputable supplier presets (`Rewind Photo Lab`, `Vanbar Photos`, `Ikigai Camera`, `Walkens`, `B&H Photo`, `Freestyle`) into `LabPresets.kt`.
*   **Reorder Alert Banner:** Added automatic Reorder Alert Cards at the top of the Chemistry section when stock is low ($\le 1$ container or capacity used $\ge 80\%$).
*   **Future Base Camp Alignment:** Local-first Room + JSON export architecture remains 100% ready for future Desktop/Tablet Base Camp sync via cloud/NAS.

### Merge Import & Base Camp Archive Architecture (September 2026)
*   **Single Source of Truth:** Established native JSON (`backup_YYYYMMDD_HHMM.json`) as the canonical, full-fidelity database backup. XMP sidecars and CSVs are explicitly designated as downstream exports for desktop DAMs (digiKam, Lightroom).
*   **Merge Import Feature (`mergeImportConfig`):** Added a non-destructive merge function in `ConfigurationManager` to safely re-import historical `Roll.Frame ID` records from Google Drive / Base Camp archives into the phone's Room database without wiping current data or requiring thumbnail images.
*   **Verification:** `./gradlew assembleDebug` PASSED, `./gradlew test` PASSED (64/64 unit tests passed).

### Child Item NavigationDrawerItem & Icon Inheritance (September 2026)
*   **NavigationDrawerItem Row Styling:** Converted child items across all sub-detail maintenance views (`Storage Locations`, `Categories`, `Measurement Units`, `Manufacturers`) from heavy rectangular cards to clean **`NavigationDrawerItem`** rows.
*   **Parent Icon Inheritance:** Each child item inherits its parent group's vector icon on the left (`ic_handyman` for Locations & Manufacturers, `ic_photo_camera` for Categories, `ic_science` for Units).
*   **Verification:** `./gradlew assembleDebug` PASSED, `./gradlew test` PASSED (64/64 unit tests passed).
*   **Exact NavigationDrawerItem Alignment:** Refactored `LabMaintenanceScreen` to use the exact `NavigationDrawerItem` rows, vector icons, circular `Badge` components, rounded pill shapes, and section headers (`DARKROOM & INVENTORY DIMENSIONS`) from `GearLibraryScreen`.
*   **Verification:** `./gradlew assembleDebug` PASSED, `./gradlew test` PASSED (64/64 unit tests passed).

---

## Desktop Base Camp & Data Ingestion Architecture (September 2026)

### 1. Reverse Chronological Export Handling & Frame Indexing
*   **Problem Identified:** The Android mobile app exports exposure logs in reverse chronological order (`Newest First` in `metadata.json`), while `thumbnails/` inside the ZIP are saved sequentially (`_F01.jpg`, `_F02.jpg`).
*   **Root Cause:** Mapping frame numbers (`frameNumber = idx + 1`) *before* sorting DTOs caused Frame #1 to take on Frame #2's photo and vice versa when imported into Desktop.
*   **Architectural Standard:** Desktop JSON parsers MUST sort DTO entries chronologically (`Oldest First`) **before** mapping frame numbers and matching thumbnails:
    ```kotlin
    val sortedEntries = entries.sortedBy { it.timestamp }
    val parsedFrames = sortedEntries.mapIndexed { idx, dto ->
        val derivedFrameNum = dto.filmRoll?.substringAfter(".")?.toIntOrNull() ?: (idx + 1)
        DesktopFrameDetail(frameNumber = derivedFrameNum, timestampMs = dto.timestamp, ...)
    }
    ```

### 2. 3-Tier Multi-Image Thumbnail Resolver
To ensure thumbnails inside batch ZIP archives are mapped with 100% precision to their underlying exposure frames:
*   **Tier 1 (Exact Timestamp Match):** Match thumbnail filename against `f.timestampMs.toString()`.
*   **Tier 2 (Frame Code Match):** Match thumbnail filename against frame serial codes (`_F01`, `_F02`, `_F1`, `frame_1`).
*   **Tier 3 (Positional Index Match):** Fallback to `thumbnailsList[fIdx]` matching DTO index $i$ to Thumbnail index $i$.

### 3. Exposure Deduplication Engine (`timestampMs` as Canonical Key)
*   **Canonical Key:** Millisecond timestamp (`timestampMs`) is the single immutable unique identifier for an exposure event.
*   **Mutable Fields Excluded:** Deduplication ignores GPS coordinates, notes, tags, and camera names—allowing users to correct GPS or standardise camera names on Desktop without causing duplicate frame imports when re-syncing mobile ZIP archives.

### 4. Data Grid & `RollStatus` State Machine
*   **Data Grid:** Replaced mobile-style cards with a dense 4-column Data Grid: `[ Camera ] | [ Film Stock ] | [ Serial ] | [ Frames ]`.
*   **Header Controls:** In-line Excel-style header menus for sorting (`Serial`, `Frames`) and filtering (`Camera`, `Film Stock`).
*   **State Machine (`RollStatus`):**
    *   `ORPHAN`: Imported legacy/partial rolls with generic serials (*e.g., "Roll 1"*).
    *   `ACTIVE`: In-camera rolls ready to accept new matching orphan frames.
    *   `FINISHED`: Full/completed rolls locked for ExifTool XMP sidecar generation.
*   **Auto-Homing Engine:** Selecting an `ORPHAN` row automatically scans the `ACTIVE` inventory for matching Camera + Film and presents a single-click CTA button: `[ 🏠 Auto-Home to Active Roll ]`.

*   **Item Master Catalogue (`InventoryMaster`):** Created `lab_inventory_masters` (`MIGRATION_42_43`) storing the abstract item specification (category, name, manufacturer, container size, default storage location). Removed ambiguous `type` field.
*   **Physical Stock Lots (`StockLot`):** Created `lab_stock_lots` (`MIGRATION_42_43`) storing physical purchases linked to `InventoryMaster` and `LabSupplier` with batch number, purchase cost, expiry date, opened date, and status (`ON_HAND`, `IN_USE`, `IN_TRANSIT`).
*   **Supplier Contacts Clean UI:** Removed promotional text hints on supplier forms, enlarged Email, Phone, and Address fields, and pre-populated default contacts from Australian suppliers list (`Rewind Photo Lab`, `Vanbar`, `Ikigai`, `Digital Camera Warehouse`, etc.).
*   **Form Architecture:** Spun up `AddEditInventoryMasterDialog`, `AddStockLotDialog`, `AddEditSupplierDialog`, and `LogPerformanceDialog` using single-choice Exposed Dropdowns.
*   **Verification:** `./gradlew assembleDebug` PASSED, `./gradlew test` PASSED (64/64 unit tests passed).

### Child Item NavigationDrawerItem & Icon Inheritance (September 2026)
*   **NavigationDrawerItem Row Styling:** Converted child items across all sub-detail maintenance views (`Storage Locations`, `Categories`, `Measurement Units`, `Manufacturers`) from heavy rectangular cards to clean **`NavigationDrawerItem`** rows.
*   **Parent Icon Inheritance:** Each child item inherits its parent group's vector icon on the left (`ic_handyman` for Locations & Manufacturers, `ic_photo_camera` for Categories, `ic_science` for Units).
*   **Verification:** `./gradlew assembleDebug` PASSED, `./gradlew test` PASSED (64/64 unit tests passed).
*   **Exact NavigationDrawerItem Alignment:** Refactored `LabMaintenanceScreen` to use the exact `NavigationDrawerItem` rows, vector icons, circular `Badge` components, rounded pill shapes, and section headers (`DARKROOM & INVENTORY DIMENSIONS`) from `GearLibraryScreen`.
*   **Verification:** `./gradlew assembleDebug` PASSED, `./gradlew test` PASSED (64/64 unit tests passed).

---

## Desktop Base Camp & Data Ingestion Architecture (September 2026)

### 1. Reverse Chronological Export Handling & Frame Indexing
*   **Problem Identified:** The Android mobile app exports exposure logs in reverse chronological order (`Newest First` in `metadata.json`), while `thumbnails/` inside the ZIP are saved sequentially (`_F01.jpg`, `_F02.jpg`).
*   **Root Cause:** Mapping frame numbers (`frameNumber = idx + 1`) *before* sorting DTOs caused Frame #1 to take on Frame #2's photo and vice versa when imported into Desktop.
*   **Architectural Standard:** Desktop JSON parsers MUST sort DTO entries chronologically (`Oldest First`) **before** mapping frame numbers and matching thumbnails:
    ```kotlin
    val sortedEntries = entries.sortedBy { it.timestamp }
    val parsedFrames = sortedEntries.mapIndexed { idx, dto ->
        val derivedFrameNum = dto.filmRoll?.substringAfter(".")?.toIntOrNull() ?: (idx + 1)
        DesktopFrameDetail(frameNumber = derivedFrameNum, timestampMs = dto.timestamp, ...)
    }
    ```

### 2. 3-Tier Multi-Image Thumbnail Resolver
To ensure thumbnails inside batch ZIP archives are mapped with 100% precision to their underlying exposure frames:
*   **Tier 1 (Exact Timestamp Match):** Match thumbnail filename against `f.timestampMs.toString()`.
*   **Tier 2 (Frame Code Match):** Match thumbnail filename against frame serial codes (`_F01`, `_F02`, `_F1`, `frame_1`).
*   **Tier 3 (Positional Index Match):** Fallback to `thumbnailsList[fIdx]` matching DTO index $i$ to Thumbnail index $i$.

### 3. Exposure Deduplication Engine (`timestampMs` as Canonical Key)
*   **Canonical Key:** Millisecond timestamp (`timestampMs`) is the single immutable unique identifier for an exposure event.
*   **Mutable Fields Excluded:** Deduplication ignores GPS coordinates, notes, tags, and camera names—allowing users to correct GPS or standardise camera names on Desktop without causing duplicate frame imports when re-syncing mobile ZIP archives.

### 4. Data Grid & `RollStatus` State Machine
*   **Data Grid:** Replaced mobile-style cards with a dense 4-column Data Grid: `[ Camera ] | [ Film Stock ] | [ Serial ] | [ Frames ]`.
*   **Header Controls:** In-line Excel-style header menus for sorting (`Serial`, `Frames`) and filtering (`Camera`, `Film Stock`).
*   **State Machine (`RollStatus`):**
    *   `ORPHAN`: Imported legacy/partial rolls with generic serials (*e.g., "Roll 1"*).
    *   `ACTIVE`: In-camera rolls ready to accept new matching orphan frames.
    *   `FINISHED`: Full/completed rolls locked for ExifTool XMP sidecar generation.
*   **Auto-Homing Engine:** Selecting an `ORPHAN` row automatically scans the `ACTIVE` inventory for matching Camera + Film and presents a single-click CTA button: `[ 🏠 Auto-Home to Active Roll ]`.
