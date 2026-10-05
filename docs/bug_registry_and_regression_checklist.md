# Bug Registry & Regression Checklist

This document serves as the permanent **Bug Registry & Regression Checklist** for Fotologue, recording reported issues, root causes, resolution steps, and regression test cases across **Fotologue Mobile** and **Fotologue Desktop**.

---

## 🐛 Bug Registry

### BUG-001: Mobile Contacts Portal Card Non-Responsive
- **Platform**: Fotologue Mobile (`app` / `HubScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Tapping the `CONTACTS DIRECTORY` portal card on the main Application Staging Portal screen (`HubScreen.kt`) was non-responsive and did not navigate.
- **Root Cause**: `onNavigateToContacts` parameter in `HubScreen.kt` was initialized with an empty lambda `{}` and not wired in `AppNavHost.kt`.
- **Resolution**: Wired `onNavigateToContacts` in `AppNavHost.kt` to launch `NavRoutes.Lab.Contacts` directly.
- **Regression Test Case**: Open main Staging Portal $\to$ tap `CONTACTS DIRECTORY` $\to$ verify Contacts Directory screen opens immediately.

---

### BUG-002: Residual Contacts Directory Listing inside Mobile LAB Menu
- **Platform**: Fotologue Mobile (`feature:lab` / `LabHomeScreen.kt`)
- **Reported**: October 2026
- **Symptom**: `Contacts Directory` was still listed inside `LAB`'s internal menu list under `Lab • Suppliers`.
- **Root Cause**: Legacy `nav_suppliers` menu item was not removed from `LabHomeScreen.kt` after `Contacts Directory` was elevated to the top-level Staging Portal.
- **Resolution**: Removed `Contacts Directory` from `LabHomeScreen.kt` menu list completely.
- **Regression Test Case**: Open `LAB` portal on Mobile $\to$ verify `Contacts Directory` is absent from internal `LAB` menu list.

---

### BUG-003: Desktop Lens Form Losing Connection to Associated Cameras on Edit
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt` & `DatabaseRepository.kt`)
- **Reported**: October 2026
- **Symptom**: Clicking `Edit Lens` on Desktop opened the lens form with `selectedAssociatedCameraIds` empty, unchecking its associated camera chips.
- **Root Cause**: Camera-lens cross-reference relationships were stored on Mobile Room DB, but Desktop's `Main.kt` was not initializing `selectedAssociatedCameraIds` on Edit or persisting them to `FoundationCameraLensesTable` on Save.
- **Resolution**: Created `FoundationCameraLensesTable` in Exposed SQLite, added `loadCameraIdsForLens` and `saveCameraLensAssociations` in `DatabaseRepository.kt`, and initialized `selectedAssociatedCameraIds` when editing a lens in `Main.kt`.
- **Regression Test Case**: Open Desktop `Lenses` tab $\to$ edit *Canon FD 50mm f/1.4* $\to$ verify *Canon AE-1 Program* camera chip is selected $\to$ toggle camera chip $\to$ save $\to$ re-edit and verify camera chip selections persist.

---

### BUG-005: Mobile Contacts Back-Navigation Target Discrepancy
- **Platform**: Fotologue Mobile (`app` / `MainActivity.kt`)
- **Reported**: October 2026
- **Symptom**: Tapping `←` on Contacts Directory navigated to `LabHomeScreen` instead of returning to the Staging Portal (`HubScreen` / `"Home"`).
- **Root Cause**: `handleBackNavigation()` in `LabHomeScreen.kt` was falling through to `else -> currentView = LabView.MAIN_MENU` when `currentView == LabView.SUPPLIERS`, taking the user to `Fotologue Lab` instead of calling `onBack()` to return to `HubScreen`.
- **Resolution**: Updated `handleBackNavigation()` in `LabHomeScreen.kt` so when `currentView == LabView.SUPPLIERS` and `initialTab == 1`, it executes `onBack()` directly, returning the user straight to `HubScreen` (`"Home"` / Staging Portal).
- **Regression Test Case**: Tap `CONTACTS DIRECTORY` on Staging Portal $\to$ tap `←` $\to$ verify user returns to `HubScreen`.

---

### BUG-006: Mobile Contacts Header Icon Discrepancy
- **Platform**: Fotologue Mobile (`feature:lab` / `LabHomeScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Mobile Contacts header displayed chemistry flask icon `⚗️` (`experiment_24px`).
- **Root Cause**: Header icon defaulted to `R.drawable.experiment_24px` for all sub-views.
- **Resolution**: Replaced icon with `R.drawable.warehouse_24px`.
- **Regression Test Case**: Open Contacts Directory $\to$ verify TopBar icon is `warehouse_24px`.

---

### BUG-007: Mobile Storage Locations Double Header
- **Platform**: Fotologue Mobile (`feature:lab` / `StorageLocationsScreen.kt`)
- **Reported**: October 2026
- **Symptom**: `StorageLocationsScreen` rendered two stacked header bars (`Lab • Storage Locations`).
- **Root Cause**: `StorageLocationsScreen.kt` had an internal `Scaffold` and `TopAppBar` while `LabHomeScreen.kt` already provided an outer `Scaffold` and `TopAppBar`.
- **Resolution**: Removed the inner `Scaffold` and `TopAppBar` from `StorageLocationsScreen.kt`.
- **Regression Test Case**: Open `Storage Locations` on Mobile $\to$ verify a single header bar renders.

---

### BUG-008: Storage Location Form Parity Mismatch
- **Platform**: Mobile & Desktop (`StorageLocationsScreen.kt` / `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Field names differed between Mobile and Desktop; Desktop used a raw text box for Type instead of a dropdown.
- **Root Cause**: Mismatched Composable field labels and missing dropdown controls on Desktop.
- **Resolution**: Unified field names and controls 1-to-1 across Mobile and Desktop (`Location Name`, `Storage Type` Dropdown, `Location Notes / Details`).
- **Regression Test Case**: Open `Add Storage Location` on Desktop & Mobile $\to$ verify fields and dropdowns match 1-to-1.

---

### BUG-009: Raw Text ENUM Inputs for Orders & Custody Forms
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Desktop `Add Pre-Arrival Order` and `Add Custody Interaction` used raw text fields for `Type` (`PURCHASE_ORDER`) and `Status` (`DELIVERED`).
- **Root Cause**: Missing controlled dropdown selectors on Desktop form fields.
- **Resolution**: Converted `Type` and `Status` into **Controlled Dropdown Selectors** (`Purchase Order`, `Loan Out`, `Rental In`, `Service / Repair` & `Waitlisted`, `Packaging`, `In Transit`, `Delivered`).
- **Regression Test Case**: Open `Add Pre-Arrival Order` on Desktop $\to$ verify `Type` and `Status` are dropdown selectors.

---

### BUG-010: Mobile Log Entry Creation Process Crash on Save
- **Platform**: Fotologue Mobile (`app` / `AddLogViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: Mobile app crashed when attempting to save a newly created Exposure Log Entry.
- **Root Cause**: `onLensSelected` in `AddLogViewModel.kt` attempted to filter apertures using `lens.minAperture..lens.maxAperture` (e.g. `22.0..1.4`). Because `minAperture` (f/22) is numerically greater than `maxAperture` (f/1.4), Kotlin produced an empty inverted float range, clearing `availableApertures` and crashing upon index access.
- **Resolution**: Updated `AddLogViewModel.kt` to use `minOf(minAperture, maxAperture)..maxOf(minAperture, maxAperture)` to guarantee valid ranges, and added safe foreign key validation in `saveLog()`.
- **Regression Test Case**: Open `Add Exposure Log` on Mobile $\to$ select a lens $\to$ save log $\to$ verify entry saves cleanly without crashing.

---

### BUG-011: Desktop Empty Data Grid After Reference Sync Import
- **Platform**: Fotologue Desktop (`desktop` / `DatabaseRepository.kt` & `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Desktop left rail showed "Cameras (4)" and "Lenses (2)", but the right pane rendered "No reference cameras logged yet".
- **Root Cause**: Mobile Room JSON exported `shutterSpeeds` as `List<String>`, whereas Desktop's JSON decoder expected `String?`, causing a silent `SerializationException`. Furthermore, `Main.kt` was missing a `refreshAllData()` call after import.
- **Resolution**: Replaced strict class decoding with an element-by-element JSON parser in `DatabaseRepository.kt` and added an immediate `refreshAllData()` call on Desktop.
- **Regression Test Case**: Click `Import from Mobile` on Desktop $\to$ select manifest $\to$ verify cameras and lenses render immediately in the data grid.

---

### BUG-012: TLR & Fixed-Lens Cameras Missing Integrated Taking Lens
- **Platform**: Fotologue Mobile (`app` / `AddEditCameraViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: Saving a new TLR (e.g., Yashica Mat-124G) created the camera body but failed to auto-setup its integrated taking lens.
- **Root Cause**: `AddEditCameraViewModel.kt` only created a fixed lens if a preset defined `fixedLens` attributes, but did not auto-generate a taking lens for non-interchangeable camera types.
- **Resolution**: Updated `AddEditCameraViewModel.kt` to auto-construct and link a taking lens (`Lens`) upon saving any TLR or fixed-mount camera.
- **Regression Test Case**: Add a TLR camera (*Yashica Mat-124G*) $\to$ verify its taking lens automatically appears in the user's Lenses list.

---

### BUG-013: Missing TLR Taking Lenses in Preset Catalog
- **Platform**: Core Data (`core/data` / `master_preset_catalog.json`)
- **Reported**: October 2026
- **Symptom**: Searching for "Yashinon" or "Yashica" in Lens Presets returned "No lens presets found".
- **Root Cause**: `master_preset_catalog.json` lacked taking lens entries for TLR bodies.
- **Resolution**: Added *Yashinon 80mm f/3.5*, *Zeiss Planar 80mm f/2.8*, and *Olympus Zuiko 35mm f/2.8* to `master_preset_catalog.json`.
- **Regression Test Case**: Open `Select Lens from Presets` modal $\to$ search "Yashinon" $\to$ verify *Yashinon 80mm f/3.5* appears.

---

### BUG-014: Mobile Lens Form Duplicate "Manual Entry" Header
- **Platform**: Fotologue Mobile (`app` / `AddEditLensScreen.kt`)
- **Reported**: October 2026
- **Symptom**: `AddEditLensScreen.kt` rendered the "Manual Entry" header text twice in stacked succession.
- **Root Cause**: Duplicate `HorizontalDivider()` and `Text("Manual Entry")` Composables in `AddEditLensScreen.kt`.
- **Resolution**: Removed the duplicate header block.
- **Regression Test Case**: Open `Add Lens` on Mobile $\to$ verify only one "Manual Entry" header displays.

---

### BUG-015: Desktop FoundationAdminTab JVM MethodTooLargeException
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Desktop Kotlin compiler failed with `MethodTooLargeException: Method too large: FoundationAdminTab$lambda$168`.
- **Root Cause**: `FoundationAdminTab` held 35+ inline `remember { mutableStateOf(...) }` declarations and a massive inline `when (selectedCategory)` block, exceeding the 64KB JVM method bytecode limit.
- **Resolution**: Refactored `FoundationAdminTab` by encapsulating form states into `FoundationAdminFormState` and extracting the data grid into `AdminDataGridContent` and left rail into `FoundationAdminLeftRail`.
- **Regression Test Case**: Run `./gradlew :desktop:compileKotlin` $\to$ verify build completes cleanly without bytecode size errors.

---

### BUG-016: Non-Atomic Destructive Manifest Sync Import (Skullinator Protocol Violation)
- **Platform**: Fotologue Desktop (`desktop` / `DatabaseRepository.kt`)
- **Reported**: October 2026
- **Symptom**: If an imported JSON manifest contained malformed data, Desktop SQLite tables were wiped *before* JSON parsing succeeded, leaving the local database empty upon failure.
- **Root Cause**: Table deletion (`Entity.all().forEach { it.delete() }`) was performed prior to JSON decoding and validation.
- **Resolution**: Refactored `importReferenceSyncPackageJson()` to validate and parse the JSON payload *first*, and perform table replacements safely inside a transaction block.
- **Regression Test Case**: Attempt to import a malformed JSON file on Desktop $\to$ verify existing database tables remain untouched.

---

### BUG-017: Unambiguous Icon Governance Violation on Contacts Screen
- **Platform**: Fotologue Mobile (`feature:lab` / `LabHomeScreen.kt` & `SupplierIssueLogScreen.kt`)
- **Reported**: October 2026
- **Symptom**: The "Handyman" icon (`ic_handyman`) was used for both Darkroom Equipment Maintenance AND the Vendor Issue & Call Tracker on the same screen, violating the single unambiguous icon principle.
- **Root Cause**: Dual reuse of `ic_handyman` across unrelated domain actions.
- **Resolution**: Created `ic_call_log.xml` (official Material Symbol phone handset + 3 list bars) for Vendor Issue & Call Tracker, reserving `ic_handyman` exclusively for Darkroom Equipment Maintenance.
- **Regression Test Case**: Open Contacts Directory on Mobile $\to$ verify action button uses the `📞☰` Call Log icon.

---

### BUG-018: Desktop SQLite Constraint Exception on Duplicate Camera Body Creation
- **Platform**: Fotologue Desktop (`desktop` / `Database.kt` & `DatabaseRepository.kt`)
- **Reported**: October 2026
- **Symptom**: Attempting to add a second camera body of the same model (*Pentax LX*) on Desktop crashed with `org.sqlite.SQLiteException: [SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (foundation_cameras.name)`.
- **Root Cause**: `FoundationCamerasTable.name` had a `.uniqueIndex()` constraint in Desktop SQLite, preventing photographers from owning multiple camera bodies of the same model.
- **Resolution**: Removed `.uniqueIndex()` on `name` in `Database.kt` across Desktop reference tables, aligned `saveFoundationCamera()` in `DatabaseRepository.kt` to auto-index duplicate names (*"Pentax LX (2)"*), and wrapped save actions in `try-catch` blocks.
- **Regression Test Case**: Add a *Pentax LX* camera $\to$ add a second *Pentax LX* camera $\to$ verify the second camera saves cleanly as *Pentax LX (2)*.

---

### BUG-019: Invalid Parent Equipment Relationships in Lab Equipment Forms
- **Platform**: Mobile & Desktop (`AddEquipmentScreen.kt` & `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Editing an *Enlarger Lens* (e.g. *El-Nikkor 50mm f/2.8 N*) showed a `Parent Equipment` dropdown containing other enlarger lenses (e.g. *Rodenstock Rodagon 80mm f/4*), allowing physically invalid parent-child relationships.
- **Root Cause**: `Parent Equipment` dropdowns lacked domain category filtering and permitted any equipment item to be selected as a parent.
- **Resolution**: Implemented smart category filtering on `Parent Equipment` dropdowns across Mobile and Desktop: *Enlarger Lenses* can **only** select top-level *Enlarger* bodies as parents, and top-level *Enlargers* default to `None (Top-Level Parent Unit)`. Added missing `Scanner / Digitiser` preset filter chip on Mobile.
- **Regression Test Case**: Edit an *Enlarger Lens* on Desktop or Mobile $\to$ open `Parent Equipment` dropdown $\to$ verify it **only** lists *Enlarger* body units.

---

### BUG-020: Raw Text Entry for Desktop Storage Location Type Input (Skullinator Protocol Violation)
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Desktop `Add Storage Location` dialog used a raw text input for `Type`, permitting string drift and violating the zero raw text entry rule.
- **Root Cause**: `Type` input field was implemented as an unconstrained `OutlinedTextField`.
- **Resolution**: Converted Desktop `Storage Location Type` field to a **Controlled Dropdown Selector** populated from `STORAGE_TYPE` LOVs (`Cabinet`, `Shelf`, `Bag`, `Studio`, `Paper Safe`, `Drawer`, `Vault`).
- **Regression Test Case**: Open `Add Storage Location` on Desktop $\to$ verify `Storage Type` is a controlled dropdown selector listing canonical storage types.

---

### BUG-021: Non-Standardized Brand Field Labels Across Mobile & Desktop Forms
- **Platform**: Mobile & Desktop (`strings.xml`, `AddEditCameraScreen.kt`, `AddEditLensScreen.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Field labels for brand/manufacturer varied inconsistency (`"Make"`, `"Make / Brand"`, `"Manufacturer"`, `"Brand / Manufacturer"`), causing visual and domain confusion.
- **Root Cause**: Uncoordinated label strings across form composables.
- **Resolution**: Unified all forms across Mobile and Desktop to use the single canonical Lexicon term **`"Brand"`**, backed by an alphabetically sorted controlled dropdown selector (*Alpa*, *Arca-Swiss*, *Arax*, ..., *Yashica*, + *"Other..."*).
- **Regression Test Case**: Inspect `Add Camera` and `Add Lens` forms on Mobile and Desktop $\to$ verify the field label is consistently **`"Brand"`** and displays an alphabetical dropdown.

---

### BUG-022: Missing Dropdown Indicator on Desktop Camera Form Brand Field
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Desktop `Add Camera` / `Edit Camera` dialog rendered `Brand` as an unassisted text field without a trailing dropdown arrow (`▼`) icon.
- **Root Cause**: `Brand` field in `Main.kt` was missing its `trailingIcon` and `DropdownMenu` wrapper.
- **Resolution**: Wrapped Desktop Camera `Brand` input in a `Box` with an `IconButton(Icons.Default.ArrowDropDown)` and connected an alphabetical `DropdownMenu` selector (*Alpa*, *Arca-Swiss*, *Arax*, ..., *Yashica*, + *"Other..."*).
- **Regression Test Case**: Open `Add Camera` on Desktop $\to$ verify `Brand` displays a trailing `▼` dropdown arrow and opens an alphabetical brand menu on click.

---

### BUG-023: Mobile Film Preset Dialog Design Discrepancy & Lexicon Mismatch
- **Platform**: Mobile & Desktop (`AddEditFilmScreen.kt` & `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Mobile's film preset dialog used a basic popup list titled `"Common Films"` with manufacturer text buttons, lacking visual and functional parity with Desktop's rich Material 3 `"Select Film Stock from Presets"` dialog.
- **Root Cause**: `FilmPresetsDialog` on Mobile was never upgraded to match Desktop's M3 card list and dropdown filter design.
- **Resolution**: Upgraded `FilmPresetsDialog` on Mobile 1-to-1 with Desktop: added **`"Select Film Stock from Presets"`** header, **`"Filter by Film Brand"`** dropdown with alphabetically sorted film brands, search field, and preset cards with **`[ Select ]`** buttons.
- **Regression Test Case**: Open `Add Film` on Mobile $\to$ tap `[ ⚙️ Select from Presets ]` $\to$ verify the M3 dialog displays with **`Filter by Film Brand`** dropdown and preset cards.

---

### BUG-024: Desktop Camera Kit Form Parity Mismatch
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Desktop `Add/Edit Camera Kit` dialog used comma-separated text strings for lenses, whereas Mobile Kit form used controlled single-choice dropdown selectors.
- **Root Cause**: Unaligned form layout on Desktop Kit Builder.
- **Resolution**: Re-architected Desktop Kit form 1-to-1 with Mobile: converted `Camera`, `Lens`, and `Default Film` into single-choice controlled dropdown selectors (`Select Camera`, `Select Lens`, `Select Film`).
- **Regression Test Case**: Open `Add Kit` on Desktop $\to$ verify `Camera`, `Lens`, and `Film` are controlled dropdown selectors matching Mobile.

---

### BUG-025: Invalid Camera Listing in Viewfinder Association Form
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: October 2026
- **Symptom**: `Add Viewfinder` listed fixed-prism 35mm SLRs (*Canon AE-1 Program*, *Nikon FM2*, *Olympus OM-1*) despite the prompt restricting viewfinders to removable or coldshoe finder bodies.
- **Root Cause**: `interchangeableFinderCameras` filter logic evaluated `type != "TLR"` to `true` for all 35mm SLRs.
- **Resolution**: Updated `interchangeableFinderCameras` filter logic: strictly excluded fixed-prism SLRs and restricted viewfinders to bodies with removable finders (*Pentax LX*, *Nikon F3*, *Hasselblad 500C/M*) or coldshoe finders (*Rangefinders*, *TLRs*, *Large Format*).
- **Regression Test Case**: Open `Add Viewfinder` on Desktop $\to$ verify fixed-prism SLRs (*AE-1*, *FM2*, *OM-1*) are absent from associated camera chips.

---

### BUG-026: Horizontal Text Clipping on Desktop Associated Camera Chips
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Associated camera chips in Desktop forms overflowed horizontally off the right edge of the card, clipping text.
- **Root Cause**: Chips were placed inside a single-line `Row` with `horizontalScroll`.
- **Resolution**: Replaced single-line `Row`s with responsive multi-line `FlowRow`s across Filters, Lenses, Teleconverters, Viewfinders, and Flashes.
- **Regression Test Case**: Open `Add Filter` on Desktop $\to$ verify camera chips wrap onto multiple lines without text clipping.

---

### BUG-027: Fixed-Hood TLR Listed in Viewfinder Association Form
- **Platform**: Fotologue Desktop (`desktop` / `Main.kt`)
- **Reported**: October 2026
- **Symptom**: `Add Viewfinder` on Desktop listed the *Yashica Mat-124G* TLR body, which has an integrated, permanently attached waist-level hood.
- **Root Cause**: Viewfinder filter logic on Desktop grouped all TLRs together, failing to distinguish between removable-finder TLRs (*Mamiya C330*) and fixed-hood TLRs (*Yashica Mat-124G*).
- **Resolution**: Refined `interchangeableFinderCameras` filter logic in `Main.kt` (Case 7) to explicitly exclude fixed-hood TLR models (*Yashica Mat*, *Rolleicord*), bringing Desktop into 1-to-1 domain parity with Mobile.
- **Regression Test Case**: Open `Add Viewfinder` on Desktop $\to$ verify *Yashica Mat-124G* is absent from associated camera chips.

---

### BUG-028: Reduced Camera Presets Catalog on Mobile & Static Hardcoded List
- **Platform**: Fotologue Mobile (`app` / `AddEditCameraScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Mobile's `Select Camera Body from Presets` dialog displayed a small 11-camera hardcoded list, lacking full catalog parity with Desktop's master preset engine.
- **Root Cause**: `AddEditCameraScreen.kt` relied on a static local list instead of querying `MasterPresetEngine`.
- **Resolution**: Connected Mobile's `AddEditCameraScreen.kt` directly to `MasterPresetEngine.getPresetsByCategory("CAMERA")`, restoring 1-to-1 master catalog search parity across Mobile and Desktop.
- **Regression Test Case**: Open `Add Camera` on Mobile $\to$ tap `[ ⚙️ Select from Presets ]` $\to$ verify master camera list loads with Canon, Nikon, Pentax, Leica, Hasselblad, Yashica, etc.

---

### BUG-029: Preset Selection Dialog Lacked Brand-First Cascading
- **Platform**: Mobile & Desktop (`AddEditCameraScreen.kt` & `Main.kt`)
- **Reported**: October 2026
- **Symptom**: `Select Camera Body from Presets` dialog filtered by `Camera Type` as the top dropdown instead of filtering by `Brand` first, forcing users to browse un-grouped camera models.
- **Root Cause**: Preset modal filtering workflow was not structured brand-first.
- **Resolution**: Re-architected preset dialogs across Mobile and Desktop: added **`Filter by Camera Brand`** as top dropdown, dynamically restricted **`Filter by Camera Type`** options to those made by the selected brand, and added automatic pre-selection when a brand produces only 1 camera type (*Hasselblad* $\to$ *Medium Format SLR*).
- **Regression Test Case**: Open `Select Camera Body from Presets` on Mobile or Desktop $\to$ select *Hasselblad* $\to$ verify `Filter by Camera Type` automatically pre-selects *Medium Format SLR*.

---

### BUG-030: Mobile Presets Uninitialized & Missing Fallback Pre-Fill Action
- **Platform**: Mobile & Desktop (`MasterPresetEngine.kt`, `AddEditCameraScreen.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Mobile camera preset dialog returned `"No camera presets found."` when initialized because `MasterPresetEngine` was uninitialized. Additionally, selecting a Brand in the preset modal did not offer a 1-click way to pre-fill custom models into the main form.
- **Root Cause**: `MasterPresetEngine` lacked stream auto-loading logic when empty.
- **Resolution**: Updated `MasterPresetEngine` to auto-load `master_preset_catalog.json` from resource streams. Added prominent **`[ ➕ Add New {SelectedBrand} Camera ]`** fallback action buttons in preset modals across Mobile and Desktop, pre-filling Brand and Type into the main form upon click.
- **Regression Test Case**: Open preset modal $\to$ select *Hasselblad* $\to$ tap `[ ➕ Add New Hasselblad Camera ]` $\to$ verify modal closes and main form has Brand (*Hasselblad*) and Type (*Medium Format SLR*) pre-filled.

---

### BUG-031: Mobile Lens Preset Dialog Empty & Missing Brand-First Cascade
- **Platform**: Mobile & Desktop (`AddEditLensScreen.kt` & `Main.kt`)
- **Reported**: October 2026
- **Symptom**: `Select Lens from Presets` on Mobile returned `"No lens presets found."` upon opening because it read an uninitialized search state.
- **Root Cause**: `AddEditLensScreen.kt` read `uiState.searchResults` which initialized to `emptyList()`.
- **Resolution**: Connected Mobile's `AddEditLensScreen.kt` directly to `MasterPresetEngine.getPresetsByCategory("LENS")`. Re-architected Lens preset dialogs across Mobile and Desktop with **`Filter by Brand`** top dropdown, **`Filter by Lens Mount`** second dropdown, and **`[ ➕ Add New {SelectedBrand} Lens ]`** fallback pre-fill action button.
- **Regression Test Case**: Open `Add Lens` on Mobile $\to$ tap `[ ⚙️ Select from Presets ]` $\to$ verify lens presets load immediately with Canon FD, Nikon F, Pentax K, Zeiss, etc.

---

### BUG-032: Cold Launch Location Permission Prompt & Secondary Preset Navigation Target
- **Platform**: Fotologue Mobile (`app` / `WelcomeScreen.kt` & `AddEditCameraScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Cold launch of the app immediately interrupted the user with an unwanted location permission prompt. Additionally, `Add Camera` opened to a manual entry form pre-filled with `"Pentax"` rather than promoting preset selection as the primary target.
- **Root Cause**: `WelcomeScreen.kt` triggered `launchPermissionRequest()` on `Unit` cold launch, and `AddEditCameraScreen.kt` defaulted to a manual form view with `make = "Pentax"`.
- **Resolution**: Removed cold-launch location prompts (deferring location permissions until active in-situ exposure logging). Re-architected `Add Camera` to open the **Preset Catalog Browser as the primary full-screen view**, cleared hardcoded `"Pentax"` defaults, added `"➕ Add new Brand..."` dropdown options, and provided a `[ ➕ Can't find model? Add Custom Camera ]` fallback button.
- **Regression Test Case**: Clean launch Mobile app $\to$ verify no location prompt appears $\to$ tap `Add your first Camera` $\to$ verify Preset Catalog Browser opens as the primary full-screen view.

---

### BUG-033: Camera Gear Buried Under Field Module on Mobile Portal
- **Platform**: Mobile & Desktop (`HubScreen.kt`, `GearLibraryScreen.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: On Mobile, camera gear management was buried inside `FIELD`, preventing users from setting up their gear before shooting. Terminology varied ("Gear Collection" vs "Gear Library").
- **Root Cause**: Unaligned Information Architecture on Mobile Staging Portal.
- **Resolution**: Elevated **`CAMERA GEAR`** to Module #1 at the top of Mobile Staging Portal (`HubScreen.kt`) above `FIELD`, with 1-click navigation to `GearLibraryScreen`. Created `settings_photo_camera_24px.xml` M3 vector icon and standardized terminology to **`CAMERA GEAR`** across Mobile and Desktop.
- **Regression Test Case**: Open Mobile Staging Portal $\to$ verify **CAMERA GEAR** is Module #1 at the top with `settings_photo_camera_24px` icon and opens `GearLibraryScreen` directly.

---

### BUG-034: US English Resource Override Displaying Legacy 'Gear Collection' Title & Crossed Tools Top-Bar Icon
- **Platform**: Fotologue Mobile (`app` / `strings.xml`, `TimelineScreen.kt`)
- **Reported**: October 2026
- **Symptom**: On US English devices (`en-rUS`), the top-bar title of `GearLibraryScreen` still displayed `"Gear Collection"`. Additionally, the top-right action icon in `FIELD` (`TimelineScreen.kt`) displayed the crossed-tools icon (`🛠️` / `ic_handyman`).
- **Root Cause**: `res/values-en-rUS/strings.xml` contained an un-updated override `<string name="gear_library_title">Gear Collection</string>`, and `TimelineScreen.kt` referenced `R.drawable.ic_handyman`.
- **Resolution**: Updated `gear_library_title` in `res/values-en-rUS/strings.xml` to `"Camera Gear"`, and replaced the action button icon in `TimelineScreen.kt` with `R.drawable.settings_photo_camera_24px`.
- **Regression Test Case**: Open `Field` screen on a US English device $\to$ verify top-right action icon is `settings_photo_camera_24px` $\to$ tap icon $\to$ verify top-bar title reads **`Camera Gear`**.

---

### BUG-035: Illogical Category Order for Kits & Legacy 'Kits' Naming
- **Platform**: Mobile & Desktop (`GearLibraryScreen.kt`, `Main.kt`, `strings.xml`)
- **Reported**: October 2026
- **Symptom**: `Kits` was positioned as the #1 category item under `CAMERA GEAR`, which was logically backwards because a kit bundle requires individual cameras, lenses, and films to be configured first.
- **Root Cause**: Unaligned category ordering in `GearLibraryScreen.kt` and `FoundationAdminLeftRail`.
- **Resolution**: Renamed "Kits" to **`Kit Bags`** across Mobile and Desktop, and moved `Kit Bags` to position #8 (last item under `CAMERA GEAR`) so photographers configure their individual cameras, lenses, films, and accessories before bundling them into kit bags.
- **Regression Test Case**: Open `Camera Gear` on Mobile or Desktop $\to$ verify category order starts with **Cameras**, **Lenses**, **Films** and ends with **Kit Bags** at position #8.

---

### BUG-036: Outdated 'Active Roll Counter' Reference in FIELD Subtitle
- **Platform**: Fotologue Mobile (`app` / `HubScreen.kt`)
- **Reported**: October 2026
- **Symptom**: `FIELD` card description on the Mobile Staging Portal (`HubScreen.kt`) still listed `"Active Roll Counter"`, which is no longer part of that module.
- **Root Cause**: Un-updated string description in `HubModuleCard`.
- **Resolution**: Updated `FIELD` card description to **`"Exposure Logging • Light Meter"`**, eliminating the outdated subtitle text.
- **Regression Test Case**: Open Mobile Staging Portal $\to$ verify `FIELD` description reads `"Exposure Logging • Light Meter"`.

---

### BUG-037: Legacy Subtitle Text '(Pillar 2)' & 'Borrowers' in Contacts Directory Portal Card
- **Platform**: Mobile & Desktop (`HubScreen.kt` & `Main.kt`)
- **Reported**: October 2026
- **Symptom**: `CONTACTS DIRECTORY` card on Mobile Staging Portal included legacy text `"(Pillar 2)"` in the subtitle and listed `"Borrowers"` in the description.
- **Root Cause**: Un-updated strings in `HubModuleCard`.
- **Resolution**: Created `patient_list_24px.xml` M3 vector icon, updated subtitle to **`"People & Vendor Management"`**, and description to **`"Suppliers • Labs • Repair Techs • Friends"`**.
- **Regression Test Case**: Open Mobile Staging Portal $\to$ verify `CONTACTS DIRECTORY` card displays `patient_list_24px` icon, subtitle `"People & Vendor Management"`, and description `"Suppliers • Labs • Repair Techs • Friends"`.

---

### BUG-038: Hidden Save Actions and Unhandled SecurityException in Mobile Exposure Logging
- **Platform**: Fotologue Mobile (`app` / `AddLogScreen.kt`, `LightMeterViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: In `Add Exposure Log`, the primary `Save Log` button was hidden at the absolute bottom of a long scrollable column, causing UX friction. Tapping `Measure Exposure` crashed the app with a `SecurityException` due to unrequested camera permissions.
- **Root Cause**: Missing FAB architecture on `AddLogScreen`. Camera permission was deferred from cold-launch but not dynamically requested before entering the `LightMeterScreen`.
- **Resolution**: Re-architected `AddLogScreen` with an always-visible `FloatingActionButton` containing safe-save logic (prompts for location if missing before saving). Added dynamic `cameraPermissionState` check directly to the `Measure Exposure` button to request hardware access gracefully.
- **Regression Test Case**: Open `Add Exposure Log` $\to$ verify floating save button is visible without scrolling $\to$ revoke camera permissions $\to$ tap `Measure Exposure` $\to$ verify app requests permission gracefully instead of crashing.

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
