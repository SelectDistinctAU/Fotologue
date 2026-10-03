# Bug Registry & Regression Checklist

This document serves as the permanent **Bug Registry & Regression Checklist** for Fotologue, recording reported issues, root causes, resolution steps, and regression test cases across **Fotologue Mobile** and **Fotologue Desktop**.

---

## 🐛 Bug Registry

### BUG-001: Mobile Contacts Portal Card Non-Responsive
- **Platform**: Fotologue Mobile (`app` / `HubScreen.kt`)
- **Reported**: March 2026
- **Symptom**: Tapping the `CONTACTS DIRECTORY` portal card on the main Application Staging Portal screen (`HubScreen.kt`) was non-responsive and did not navigate.
- **Root Cause**: `onNavigateToContacts` parameter in `HubScreen.kt` was initialized with an empty lambda `{}` and not wired in `AppNavHost.kt`.
- **Resolution**: Wired `onNavigateToContacts` in `AppNavHost.kt` to launch `NavRoutes.Lab.Contacts` directly.
- **Regression Test Case**: Open main Staging Portal $\to$ tap `CONTACTS DIRECTORY` $\to$ verify Contacts Directory screen opens immediately.

---

### BUG-002: Residual Contacts Directory Listing inside Mobile LAB Menu
- **Platform**: Fotologue Mobile (`feature:lab` / `LabHomeScreen.kt`)
- **Reported**: March 2026
- **Symptom**: `Contacts Directory` was still listed inside `LAB`'s internal menu list under `Lab • Suppliers`.
- **Root Cause**: Legacy `nav_suppliers` menu item was not removed from `LabHomeScreen.kt` after `Contacts Directory` was elevated to the top-level Staging Portal.
- **Resolution**: Removed `Contacts Directory` from `LabHomeScreen.kt` menu list completely.
- **Regression Test Case**: Open `LAB` portal on Mobile $\to$ verify `Contacts Directory` is absent from internal `LAB` menu list.

---

### BUG-003: Desktop Lens Form Losing Connection to Associated Cameras on Edit
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt` & `DatabaseRepository.kt`)
- **Reported**: March 2026
- **Symptom**: Clicking `Edit Lens` on Desktop opened the lens form with `selectedAssociatedCameraIds` empty, unchecking its associated camera chips.
- **Root Cause**: Camera-lens cross-reference relationships were stored on Mobile Room DB, but Desktop's `Main.kt` was not initializing `selectedAssociatedCameraIds` on Edit or persisting them to `FoundationCameraLensesTable` on Save.
- **Resolution**: Created `FoundationCameraLensesTable` in Exposed SQLite, added `loadCameraIdsForLens` and `saveCameraLensAssociations` in `DatabaseRepository.kt`, and initialized `selectedAssociatedCameraIds` when editing a lens in `Main.kt`.
- **Regression Test Case**: Open Desktop `Lenses` tab $\to$ edit *Canon FD 50mm f/1.4* $\to$ verify *Canon AE-1 Program* camera chip is selected $\to$ toggle camera chip $\to$ save $\to$ re-edit and verify camera chip selections persist.

---

### BUG-005: Mobile Contacts Back-Navigation Target Discrepancy
- **Platform**: Fotologue Mobile (`app` / `MainActivity.kt`)
- **Reported**: March 2026
- **Symptom**: Tapping `←` on Contacts Directory navigated to `LabHomeScreen` instead of returning to the Staging Portal (`HubScreen` / `"Home"`).
- **Root Cause**: `handleBackNavigation()` in `LabHomeScreen.kt` was falling through to `else -> currentView = LabView.MAIN_MENU` when `currentView == LabView.SUPPLIERS`, taking the user to `Fotologue Lab` instead of calling `onBack()` to return to `HubScreen`.
- **Resolution**: Updated `handleBackNavigation()` in `LabHomeScreen.kt` so when `currentView == LabView.SUPPLIERS` and `initialTab == 1`, it executes `onBack()` directly, returning the user straight to `HubScreen` (`"Home"` / Staging Portal).
- **Regression Test Case**: Tap `CONTACTS DIRECTORY` on Staging Portal $\to$ tap `←` $\to$ verify user returns to `HubScreen`.

---

### BUG-006: Mobile Contacts Header Icon Discrepancy
- **Platform**: Fotologue Mobile (`feature:lab` / `LabHomeScreen.kt`)
- **Reported**: March 2026
- **Symptom**: Mobile Contacts header displayed chemistry flask icon `⚗️` (`experiment_24px`).
- **Root Cause**: Header icon defaulted to `R.drawable.experiment_24px` for all sub-views.
- **Resolution**: Replaced icon with `R.drawable.warehouse_24px`.
- **Regression Test Case**: Open Contacts Directory $\to$ verify TopBar icon is `warehouse_24px`.

---

### BUG-007: Mobile Storage Locations Double Header
- **Platform**: Fotologue Mobile (`feature:lab` / `StorageLocationsScreen.kt`)
- **Reported**: March 2026
- **Symptom**: `StorageLocationsScreen` rendered two stacked header bars (`Lab • Storage Locations`).
- **Root Cause**: `StorageLocationsScreen.kt` had an internal `Scaffold` and `TopAppBar` while `LabHomeScreen.kt` already provided an outer `Scaffold` and `TopAppBar`.
- **Resolution**: Removed the inner `Scaffold` and `TopAppBar` from `StorageLocationsScreen.kt`.
- **Regression Test Case**: Open `Storage Locations` on Mobile $\to$ verify a single header bar renders.

---

### BUG-008: Storage Location Form Parity Mismatch
- **Platform**: Mobile & Desktop (`StorageLocationsScreen.kt` / `Main.kt`)
- **Reported**: March 2026
- **Symptom**: Field names differed between Mobile and Desktop; Desktop used a raw text box for Type instead of a dropdown.
- **Root Cause**: Mismatched Composable field labels and missing dropdown controls on Desktop.
- **Resolution**: Unified field names and controls 1-to-1 across Mobile and Desktop (`Location Name`, `Storage Type` Dropdown, `Location Notes / Details`).
- **Regression Test Case**: Open `Add Storage Location` on Desktop & Mobile $\to$ verify fields and dropdowns match 1-to-1.

---

### BUG-009: Raw Text ENUM Inputs for Orders & Custody Forms
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: March 2026
- **Symptom**: Desktop `Add Pre-Arrival Order` and `Add Custody Interaction` used raw text fields for `Type` (`PURCHASE_ORDER`) and `Status` (`DELIVERED`).
- **Root Cause**: Missing controlled dropdown selectors on Desktop form fields.
- **Resolution**: Converted `Type` and `Status` into **Controlled Dropdown Selectors** (`Purchase Order`, `Loan Out`, `Rental In`, `Service / Repair` & `Waitlisted`, `Packaging`, `In Transit`, `Delivered`).
- **Regression Test Case**: Open `Add Pre-Arrival Order` on Desktop $\to$ verify `Type` and `Status` are dropdown selectors.

---

### BUG-010: Mobile Log Entry Creation Process Crash on Save
- **Platform**: Fotologue Mobile (`app` / `AddLogViewModel.kt`)
- **Reported**: March 2026
- **Symptom**: Mobile app crashed when attempting to save a newly created Exposure Log Entry.
- **Root Cause**: `onLensSelected` in `AddLogViewModel.kt` attempted to filter apertures using `lens.minAperture..lens.maxAperture` (e.g. `22.0..1.4`). Because `minAperture` (f/22) is numerically greater than `maxAperture` (f/1.4), Kotlin produced an empty inverted float range, clearing `availableApertures` and crashing upon index access.
- **Resolution**: Updated `AddLogViewModel.kt` to use `minOf(minAperture, maxAperture)..maxOf(minAperture, maxAperture)` to guarantee valid ranges, and added safe foreign key validation in `saveLog()`.
- **Regression Test Case**: Open `Add Exposure Log` on Mobile $\to$ select a lens $\to$ save log $\to$ verify entry saves cleanly without crashing.

---

### BUG-011: Desktop Empty Data Grid After Reference Sync Import
- **Platform**: Fotologue Desktop (`desktop` / `DatabaseRepository.kt` & `Main.kt`)
- **Reported**: March 2026
- **Symptom**: Desktop left rail showed "Cameras (4)" and "Lenses (2)", but the right pane rendered "No reference cameras logged yet".
- **Root Cause**: Mobile Room JSON exported `shutterSpeeds` as `List<String>`, whereas Desktop's JSON decoder expected `String?`, causing a silent `SerializationException`. Furthermore, `Main.kt` was missing a `refreshAllData()` call after import.
- **Resolution**: Replaced strict class decoding with an element-by-element JSON parser in `DatabaseRepository.kt` and added an immediate `refreshAllData()` call on Desktop.
- **Regression Test Case**: Click `Import from Mobile` on Desktop $\to$ select manifest $\to$ verify cameras and lenses render immediately in the data grid.

---

### BUG-012: TLR & Fixed-Lens Cameras Missing Integrated Taking Lens
- **Platform**: Fotologue Mobile (`app` / `AddEditCameraViewModel.kt`)
- **Reported**: March 2026
- **Symptom**: Saving a new TLR (e.g., Yashica Mat-124G) created the camera body but failed to auto-setup its integrated taking lens.
- **Root Cause**: `AddEditCameraViewModel.kt` only created a fixed lens if a preset defined `fixedLens` attributes, but did not auto-generate a taking lens for non-interchangeable camera types.
- **Resolution**: Updated `AddEditCameraViewModel.kt` to auto-construct and link a taking lens (`Lens`) upon saving any TLR or fixed-mount camera.
- **Regression Test Case**: Add a TLR camera (*Yashica Mat-124G*) $\to$ verify its taking lens automatically appears in the user's Lenses list.

---

### BUG-013: Missing TLR Taking Lenses in Preset Catalog
- **Platform**: Core Data (`core/data` / `master_preset_catalog.json`)
- **Reported**: March 2026
- **Symptom**: Searching for "Yashinon" or "Yashica" in Lens Presets returned "No lens presets found".
- **Root Cause**: `master_preset_catalog.json` lacked taking lens entries for TLR bodies.
- **Resolution**: Added *Yashinon 80mm f/3.5*, *Zeiss Planar 80mm f/2.8*, and *Olympus Zuiko 35mm f/2.8* to `master_preset_catalog.json`.
- **Regression Test Case**: Open `Select Lens from Presets` modal $\to$ search "Yashinon" $\to$ verify *Yashinon 80mm f/3.5* appears.

---

### BUG-014: Mobile Lens Form Duplicate "Manual Entry" Header
- **Platform**: Fotologue Mobile (`app` / `AddEditLensScreen.kt`)
- **Reported**: March 2026
- **Symptom**: `AddEditLensScreen.kt` rendered the "Manual Entry" header text twice in stacked succession.
- **Root Cause**: Duplicate `HorizontalDivider()` and `Text("Manual Entry")` Composables in `AddEditLensScreen.kt`.
- **Resolution**: Removed the duplicate header block.
- **Regression Test Case**: Open `Add Lens` on Mobile $\to$ verify only one "Manual Entry" header displays.

---

### BUG-015: Desktop FoundationAdminTab JVM MethodTooLargeException
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: March 2026
- **Symptom**: Desktop Kotlin compiler failed with `MethodTooLargeException: Method too large: FoundationAdminTab$lambda$168`.
- **Root Cause**: `FoundationAdminTab` held 35+ inline `remember { mutableStateOf(...) }` declarations and a massive inline `when (selectedCategory)` block, exceeding the 64KB JVM method bytecode limit.
- **Resolution**: Refactored `FoundationAdminTab` by encapsulating form states into `FoundationAdminFormState` and extracting the data grid into `AdminDataGridContent` and left rail into `FoundationAdminLeftRail`.
- **Regression Test Case**: Run `./gradlew :desktop:compileKotlin` $\to$ verify build completes cleanly without bytecode size errors.

---

### BUG-016: Non-Atomic Destructive Manifest Sync Import (Skullinator Protocol Violation)
- **Platform**: Fotologue Desktop (`desktop` / `DatabaseRepository.kt`)
- **Reported**: March 2026
- **Symptom**: If an imported JSON manifest contained malformed data, Desktop SQLite tables were wiped *before* JSON parsing succeeded, leaving the local database empty upon failure.
- **Root Cause**: Table deletion (`Entity.all().forEach { it.delete() }`) was performed prior to JSON decoding and validation.
- **Resolution**: Refactored `importReferenceSyncPackageJson()` to validate and parse the JSON payload *first*, and perform table replacements safely inside a transaction block.
- **Regression Test Case**: Attempt to import a malformed JSON file on Desktop $\to$ verify existing database tables remain untouched.

---

### BUG-017: Unambiguous Icon Governance Violation on Contacts Screen
- **Platform**: Fotologue Mobile (`feature:lab` / `LabHomeScreen.kt` & `SupplierIssueLogScreen.kt`)
- **Reported**: March 2026
- **Symptom**: The "Handyman" icon (`ic_handyman`) was used for both Darkroom Equipment Maintenance AND the Vendor Issue & Call Tracker on the same screen, violating the single unambiguous icon principle.
- **Root Cause**: Dual reuse of `ic_handyman` across unrelated domain actions.
- **Resolution**: Created `ic_call_log.xml` (official Material Symbol phone handset + 3 list bars) for Vendor Issue & Call Tracker, reserving `ic_handyman` exclusively for Darkroom Equipment Maintenance.
- **Regression Test Case**: Open Contacts Directory on Mobile $\to$ verify action button uses the `📞☰` Call Log icon.

---

## 📋 Comprehensive Regression Test Checklist

### 1. Mobile Regression Test Checklist
- [ ] **Portal Navigation**: Tap `FIELD`, `CONTACTS DIRECTORY`, `LAB`, `STATION`, `ARCHIVE` cards on `HubScreen.kt` and verify each opens its respective top-level module.
- [ ] **Contacts Import**: Open `Contacts Directory` $\to$ tap `[ ➕ Add Contact ]` $\to$ tap `[ 👤 Import from Phone Contacts ]` $\to$ verify native Android contact picker launches cleanly.
- [ ] **Camera Creation**: Open `Add Camera` $\to$ tap `[ ⚙️ Select from Presets ]` $\to$ select *Pentax LX* $\to$ verify fields populate (*Brand*, *Model*, *Type*, *Format*, *Mount*, *Shutter Speeds*) and `Serial Number` is the last field.

### 2. Desktop Regression Test Checklist
- [ ] **Stage 4 Navigation Rail**: Verify rail width is `210.dp` and all 13 items (*Teleconverters*, *Storage Locations*, *Pre-Arrival Orders*, *Custody Ledger*) render on 1 line with zero text overflow.
- [ ] **Lens-Camera Association**: Edit a lens $\to$ verify associated camera chips are checked $\to$ save $\to$ re-edit and verify associations persist.
- [ ] **Preset Modal Selection**: Click `[ ⚙️ Select from Presets ]` on Camera, Lens, Film, and Equipment forms $\to$ verify vector icons display without emoji text and category filter dropdown works cleanly.

---

*Bug Registry & Regression Checklist maintained for Fotologue v3.0 Quality Assurance.*
