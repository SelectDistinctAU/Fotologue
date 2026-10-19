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

### BUG-039: Missing Mobile Lens Presets and Legacy Brand Mismatch
- **Platform**: Mobile & Desktop (`AddEditLensScreen.kt`, `Main.kt`, `AddEditLensViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: Mobile's `Select Lens from Presets` returned `"No lens presets found."` upon opening due to an uninitialized search state. Custom desktop-synced lenses were also missing from the Mobile presets dialog.
- **Root Cause**: `AddEditLensScreen.kt` relied on an empty `searchResults` flow. The preset lists between Mobile and Desktop were architecturally disconnected, and Desktop `Main.kt` used inconsistent `Make / Brand` dropdowns.
- **Resolution**: Implemented the Unification Bridge in `AddEditLensViewModel.kt` to merge `MasterPresetEngine` items with Desktop-synced local Foundation lenses. Re-architected lens preset dialogs across both platforms to use Brand-First cascading filters (`Filter by Brand` $\to$ `Filter by Mount`) and added a `[ ➕ Add New {Brand} Lens ]` fallback pre-fill action button.
- **Regression Test Case**: Sync a custom lens from Desktop to Mobile $\to$ open `Add Lens` on Mobile $\to$ tap `[ ⚙️ Select from Presets ]` $\to$ verify the unified catalog loads immediately including the synced custom lens.

---

### BUG-040: Unregistered Desktop Build Task for Maintainer Tooling
- **Platform**: Fotologue Desktop (`desktop/build.gradle.kts`)
- **Reported**: October 2026
- **Symptom**: Attempting to execute `./gradlew :desktop:runMaintainer` failed with `Cannot locate tasks that match ':desktop:runMaintainer'`, preventing maintainers from accessing the visual preset authoring GUI.
- **Root Cause**: The `MaintainerApp.kt` file existed with a `main()` function, but the custom `JavaExec` build task was not registered in `desktop/build.gradle.kts`.
- **Resolution**: Appended a `tasks.register<JavaExec>("runMaintainer")` block to `desktop/build.gradle.kts` targeting the `com.fotologue.desktop.MaintainerAppKt` main class with the required native JVM access arguments.
- **Regression Test Case**: Run `./gradlew :desktop:runMaintainer` $\to$ verify build succeeds and Fotologue Master Preset Authoring Tool window opens.

---

### BUG-041: Maintainer Tool Displayed Empty Preset List Due to Missing Assembly Task
- **Platform**: Fotologue Maintainer Tool (`MaintainerApp.kt` & `core/data/build.gradle.kts`)
- **Reported**: October 2026
- **Symptom**: Launching the Maintainer Tool displayed `"No CAMERA presets found in JSON file."` because the required modular JSON files did not exist.
- **Root Cause**: The modular preset architecture (`cameras.json`, `films.json`, `lenses.json`) defined in `maintainer_preset_workflow_architecture.md` was never implemented. The system still relied on hardcoded Kotlin objects, and the `assembleMasterCatalog` Gradle task was missing.
- **Resolution**: Hand-seeded the initial modular JSON files (`cameras.json`, `films.json`, `lenses.json`) into `core/data/src/main/resources/presets/` and implemented the `assembleMasterCatalog` task in `core/data/build.gradle.kts` to compile them into the release artifact.
- **Regression Test Case**: Run `./gradlew :desktop:runMaintainer` $\to$ verify the tool immediately lists the seeded Camera, Lens, and Film presets.

---

### BUG-042: Maintainer Tool Empty State Due to Working Directory Path Resolution Failure
- **Platform**: Fotologue Maintainer Tool (`MaintainerApp.kt`, `DatabaseRepository.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: After seeding modular JSON files, the Maintainer Tool still displayed an empty state.
- **Root Cause**: `MaintainerApp.kt` relied on `System.getProperty("user.dir")` which resolved to the `:desktop` module directory during the `JavaExec` build phase, causing the tool to look for files in `desktop/core/data/...` (which did not exist).
- **Resolution**: Implemented a robust upwards-traversing path resolution algorithm in `MaintainerApp.kt` to locate the true project root (`settings.gradle.kts`) before appending `core/data/src/main/resources/presets`. Also cleaned up compiler warnings in `DatabaseRepository.kt` (unnecessary non-null assertion) and `Main.kt` (redundant Json object creation).
- **Regression Test Case**: Run `./gradlew :desktop:runMaintainer` $\to$ verify the tool successfully resolves the path and loads JSON presets.

---

### BUG-043: Desktop AddEditCameraScreen Unification Bridge Failure
- **Platform**: Fotologue Desktop (`Main.kt`)
- **Reported**: October 2026
- **Symptom**: After successfully unifying the preset dialogs on Mobile, clicking `Select from Presets` on Desktop failed to show newly merged items.
- **Root Cause**: Desktop's `Main.kt` was missing the `Unification Bridge` logic that merges local custom foundation entries with the `MasterPresetEngine` catalog.
- **Resolution**: Ported the Unification Bridge logic into Desktop's `Main.kt`. The preset dialogs for Cameras, Lenses, and Films now seamlessly merge custom local Database repository entries with the Master Developer catalog.
- **Regression Test Case**: Add a custom camera on Desktop $\to$ tap `[ ⚙️ Select from Presets ]` $\to$ verify custom camera appears in the dropdown list.

---

### BUG-044: Floating Action Button Misrouted on Mobile Gear Library
- **Platform**: Fotologue Mobile (`app` / `GearLibraryScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Tapping the `[ + ]` FAB on the `Cameras` tab accidentally navigated to `Add Kit Bag`, tapping it on `Lenses` went to `Add Camera`, etc.
- **Root Cause**: The `when` block evaluating `uiState.selectedCategoryIndex` inside the FloatingActionButton `onClick` handler was not updated to reflect the new category index order when `Kit Bags` was moved to the bottom (index 7).
- **Resolution**: Re-aligned the FAB `onClick` switch statement to perfectly match the current Lexicon category order (`0`=Cameras, `1`=Lenses, `2`=Films... `7`=Kit Bags).
- **Regression Test Case**: Open Mobile `CAMERA GEAR` $\to$ tap `Cameras` tab $\to$ tap `[ + ]` FAB $\to$ verify it correctly navigates to the `Add Camera` screen.

---

### BUG-045: Ambiguous Action Labels in Maintainer App
- **Platform**: Fotologue Maintainer Tool (`desktop` / `MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Action buttons in the Maintainer Tool simply read `[ + Add CAMERA Preset ]`, which was confused with creating user gear rather than authoring JSON presets.
- **Root Cause**: Poorly descriptive button labels in the maintainer GUI.
- **Resolution**: Updated labels to clearly denote their architectural function: `[ + Add New CAMERA Preset Entry ]`.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ verify action button reads `[ + Add New CAMERA Preset Entry ]`.

---

### BUG-046: Maintainer Tool Naive Pluralization File Mapping Failure
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Selecting the `LENS`, `FLASH`, or `EQUIPMENT` tabs in the Maintainer Tool displayed empty states (`"No LENS presets found in JSON file."`).
- **Root Cause**: `MaintainerApp.kt` constructed JSON filenames using naive string appending (`"${cat.lowercase()}s.json"`), attempting to read `lenss.json`, `flashs.json`, and `equipments.json` instead of `lenses.json`, `flashes.json`, and `lab_equipment.json`.
- **Resolution**: Replaced naive string appending with an explicit category-to-filename routing map (`"LENS" -> "lenses.json"`, `"EQUIPMENT" -> "lab_equipment.json"`, etc.) and seeded default JSON arrays for all 10 categories in `core/data/src/main/resources/presets/`.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ click `LENS`, `CONSUMABLE`, `EQUIPMENT` tabs $\to$ verify presets load cleanly without file routing errors.

---

### BUG-047: Missing Edit Feature & Raw Text Entry in Maintainer Tool
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Maintainer Tool preset cards only offered a delete action without any way to edit existing JSON entries. Additionally, the `Brand` field was a raw text box rather than a controlled LOV dropdown.
- **Root Cause**: `MaintainerApp.kt` lacked an edit dialog state and relied on raw `OutlinedTextField` inputs for brand data.
- **Resolution**: Added an `Edit` action button (`IconButton(Icons.Default.Edit)`) to all preset cards opening a pre-filled `Edit {Category} Preset Entry` dialog. Replaced raw text brand entry with a **Controlled Brand Dropdown Selector** populated from canonical and active JSON category brands.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ click `Edit` on a preset card $\to$ verify pre-filled edit dialog opens with a controlled Brand dropdown.

---

### BUG-048: Raw Text Entry in Maintainer Tool Attribute Value Fields
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: `Attribute 1 Value` and `Attribute 2 Value` in the Maintainer Tool dialogs relied on unconstrained text fields, permitting string drift (e.g., typing `"film slr"` or `"pentax-k"`). Unused attribute fields also cluttered single-attribute categories like `FILM` and `FILTER`.
- **Root Cause**: Un-tailored attribute form dialogs in `MaintainerApp.kt`.
- **Resolution**: Tailored the Add/Edit dialog per category (hiding unused attributes for `FILM`, `VIEWFINDER`, `TELECONVERTER`, `FILTER`, `FLASH`). Converted `Attribute 1 Value` and `Attribute 2 Value` into **Controlled Dropdown Selectors** backed by canonical LOV sets (`cameraTypes`, `lensTypes`, `filmTypes`, `viewfinderTypes`, `mountOptions`).
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ edit a Camera preset $\to$ verify `type` and `mount` values are controlled dropdown selectors.

---

### BUG-049: Missing Filter Exposure Factor Compensation in Preset Authoring & Metering
- **Platform**: Maintainer Tool & Mobile App (`MaintainerApp.kt`, `filters.json`, `ExposureSuggester.kt`, `LightMeterViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: `FILTER` presets did not include exposure compensation factors (`compensationStops`), forcing photographers using ND filters or contrast filters to manually calculate stop adjustments outside the app.
- **Root Cause**: Missing `compensationStops` attribute in Maintainer Tool `FILTER` form and missing seeded data in `filters.json`.
- **Resolution**: Enabled Attribute 1 Key = `"compensationStops"` for `FILTER` in `MaintainerApp.kt` with a Controlled Dropdown Selector (*0.0 Stops*, *+1.0 Stop*, *+1.5 Stops*, *+2.0 Stops*, *+3.0 Stops*, *+6.0 Stops*, *+10.0 Stops*). Seeded `filters.json` with Hoya ND8 (+3.0 stops), CPL (+1.5 stops), and Yellow K2 (+1.0 stop), automatically wired into `ExposureSuggester` for automatic light meter shutter speed/aperture compensation.
- **Regression Test Case**: Open `Add Exposure Log` $\to$ measure EV 15 $\to$ select *Hoya ND8* filter $\to$ verify recommended shutter speed slows down by 3 stops automatically.

---

### BUG-050: Hardcoded Fixed Lens Strings and Missing Maintainer CRUD Controls
- **Platform**: Maintainer Tool & Desktop App (`MaintainerApp.kt`, `cameras.json`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Fixed taking lenses for TLRs (*Yashica Mat-124G*) and compact cameras were hardcoded in Kotlin string fallbacks (`"${preset.name} Taking Lens"`, `"80mm"`, `"f/3.5"`), preventing maintainers from authoring or editing taking lens specs in the preset catalog.
- **Root Cause**: Unrepresented taking lens attributes in `cameras.json` and missing UI controls in `MaintainerApp.kt`.
- **Resolution**: Defined explicit fixed lens attributes (`fixedLensName`, `fixedLensFocalLength`, `fixedLensMaxAperture`, `fixedLensFilterSize`) in `cameras.json`. Added explicit **Integrated Fixed Lens CRUD controls** in `MaintainerApp.kt` when `mount == "Fixed"` or `type` is in (`TLR`, `Compact`, `Point & Shoot`), and updated `Main.kt` to read taking lens attributes dynamically from the catalog.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ edit *Yashica Mat-124G* $\to$ verify taking lens fields display pre-filled and can be edited and saved back to `cameras.json`.

---

### BUG-051: Un-Normalized Fixed Lens Specifications in Camera Catalog
- **Platform**: Maintainer Tool, Mobile & Desktop (`cameras.json`, `lenses.json`, `MaintainerApp.kt`, `AddEditCameraViewModel.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Fixed-lens properties (focal length, max aperture, filter size) were duplicated inside `cameras.json` rather than referencing the single source of truth in `lenses.json`.
- **Root Cause**: Un-normalized preset data schema.
- **Resolution**: Refactored taking lenses so full specs live in `lenses.json` (e.g. *Yashinon 80mm f/3.5 Taking Lens*). In `cameras.json`, fixed-lens bodies reference them via Attribute 3 Key = `"fixedLens"`. Updated Maintainer Tool to render **Attribute 3 Value** as a **Controlled Dropdown Selector** populated from `lenses.json`, and updated Mobile & Desktop client viewmodels to resolve taking lens properties dynamically.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ edit *Yashica Mat-124G* $\to$ verify Attribute 3 Key is `fixedLens` and Attribute 3 Value is a controlled dropdown containing `Yashinon 80mm f/3.5 Taking Lens`.

---

### BUG-052: Unfiltered Referenced Lens Dropdown in Maintainer Tool
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Editing a fixed-lens camera (*Yashica Mat-124G*) in the Maintainer Tool displayed all interchangeable SLR and Rangefinder lenses in the `Attribute 3 Value (Referenced Lens)` dropdown.
- **Root Cause**: `lensOptions` list in `MaintainerApp.kt` was loaded from `lenses.json` without filtering by `mount == "Fixed"`.
- **Resolution**: Filtered `lensOptions` to strictly require `attributes["mount"].equals("Fixed", ignoreCase = true)`. Interchangeable SLR/Rangefinder lenses (*Canon FD*, *Nikon F*, *Leica M*) are now hidden, leaving only eligible fixed taking lenses in the dropdown.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ edit *Yashica Mat-124G* $\to$ open `Attribute 3 Value` dropdown $\to$ verify Canon FD 50mm f/1.4 is absent and Yashinon 80mm f/3.5 Taking Lens is displayed.

---

### BUG-053: Missing Category Top Dropdown Filters in Maintainer Tool
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Maintainer Tool list view only offered a search bar, forcing maintainers to browse through un-filtered preset entries across large categories.
- **Root Cause**: Missing category-specific top dropdown filter bar in `MaintainerApp.kt`.
- **Resolution**: Added category-specific top dropdown filters (`Filter by Brand`, `Filter by Type`, `Filter by Mount`) above the preset list in `MaintainerApp.kt`, applying dynamic multi-criteria filtering on the active JSON dataset.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ select `CAMERA` tab $\to$ verify top dropdown filters for Brand, Type, and Mount render and filter the preset cards list immediately.

---

### BUG-054: Bloated Full-Width Top Dropdown Filters in Maintainer Tool
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Top dropdown filter boxes (`Filter by Brand`, `Filter by Type`, `Filter by Mount`) stretched across the entire width of the window, creating an overly wide, bloated layout.
- **Root Cause**: `Modifier.weight(1f)` was used on the filter container `Box`es.
- **Resolution**: Replaced full-width `weight(1f)` with compact, content-tailored fixed widths (`200.dp` – `220.dp`), keeping the filter bar neatly aligned on the left.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ verify top filter dropdowns render as compact `200.dp` – `220.dp` boxes.

---

### BUG-055: Camera Catalog Bulk Expansion From Luma & Leo AI List
- **Platform**: Fotologue Preset Catalog (`cameras.json`, `master_preset_catalog.json`)
- **Reported**: October 2026
- **Symptom**: The camera preset catalog only contained 11 base cameras, lacking popular 1960–2000 film SLRs, rangefinders, and medium format bodies.
- **Root Cause**: Unpopulated preset catalog dataset.
- **Resolution**: Parsed and ingested 36 iconic, reliable 1960–2000 camera models from Luma and Leo AI lists into `cameras.json` (Nikon F/F2/FE/FM/FA/F4/F5/F90X, Canon New F-1/AE-1/A-1/FTb/AV-1/T90/EOS-1N/EOS 3, Pentax Spotmatic/MX/67, Olympus OM-1/2/10/4Ti, Minolta SRT-101/X-700/XD-11/Maxxum 7000/9, Leicaflex SL2, Leica R6, Contax RTS/RX, Hasselblad 503CX, Mamiya RZ67 Pro, Yashica FX-3, Bronica SQ-A). Compiled master release artifact via `./gradlew :core:data:assembleMasterCatalog`.
- **Regression Test Case**: Launch Maintainer Tool `./gradlew :desktop:runMaintainer` $\to$ select `CAMERA` tab $\to$ verify total camera count displays 47 cameras.

---

### BUG-056: Maintainer Tool Preset List Lacked Sortable Data Grid Architecture
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Maintainer Tool rendered preset entries as vertical cards with duplicate brand string formatting (*"Canon Canon AE-1 Program"*) and lacked column sorting.
- **Root Cause**: Primitive vertical card list layout in `MaintainerApp.kt`.
- **Resolution**: Re-architected Maintainer Tool list into a **Sortable Data Grid** matching Desktop Master Rolls design. Added sortable column headers (`Brand`, `Preset Name`, `Type`, `Mount`, `ID`) with 1-click direction toggles (`▲` / `▼`), and separated `Brand` and `Name` into distinct columns to eliminate string duplication.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ verify Data Grid header renders with sortable columns and duplicate brand labels are eliminated (*Brand: Canon | Name: AE-1 Program*).

---

### BUG-057: Unintegrated Top Filter Boxes in Maintainer Data Grid
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Standalone filter boxes sat awkwardly above the Maintainer Data Grid instead of being integrated directly into the column header cells.
- **Root Cause**: Separate top filter bar design in `MaintainerApp.kt`.
- **Resolution**: Removed standalone top filter text boxes. Integrated category LOV filtering and sorting controls directly into column header cells (`BRAND ▾`, `TYPE ▾`, `MOUNT ▾`) via clickable dropdown menus matching Desktop Master Rolls layout. Streamlined top row to house Category Chips alongside action buttons.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ click `BRAND ▾` column header cell $\to$ verify dropdown menu opens with sorting options and category brand LOV items.

---

### BUG-058: Ingestion of Corrected 1960–2000 TLRs, Rangefinders & Fixed Compacts
- **Platform**: Fotologue Preset Catalog (`cameras.json`, `lenses.json`, `master_preset_catalog.json`)
- **Reported**: October 2026
- **Symptom**: The preset catalog was missing classic 1960–2000 TLRs, rangefinders, and premium fixed compacts (Rolleiflex 2.8F, Canonet QL17 G-III, Konica Auto S2/S3, Olympus 35 SP, Rollei 35S, Leica M2/M4/M5, Contax G1/G2).
- **Root Cause**: Unpopulated TLR and Rangefinder preset dataset.
- **Resolution**: Ingested 35 new camera presets from the Corrected Master List into `cameras.json` and 20 normalized fixed taking lenses into `lenses.json`. Total camera presets expanded from 47 to **82 master camera bodies**, and total lens presets expanded to **26 master lenses**.
- **Regression Test Case**: Launch Maintainer Tool `./gradlew :desktop:runMaintainer` $\to$ verify total camera count displays 82 cameras and lens count displays 26 lenses.

---

### BUG-059: Missing Remote Cloud Preset Refreshing Engine
- **Platform**: Core, Mobile & Desktop (`MasterPresetEngine.kt`, `DesktopMasterPresetEngine.kt`, `SettingsScreen.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Photographers had no way to fetch live catalog updates or new camera presets without waiting for a full app store release.
- **Root Cause**: Missing remote cloud fetch mechanism and disk caching layer in `MasterPresetEngine`.
- **Resolution**: Implemented `refreshFromCloud()` in `MasterPresetEngine.kt` (Mobile) and `DesktopMasterPresetEngine.kt` (Desktop) to fetch the raw `master_preset_catalog.json` directly from GitHub via coroutines. Updated `assembleMasterCatalog` to write to both `src/main/assets/` and `src/main/resources/`. Added a **`[ 🔄 Refresh Master Presets from Cloud ]`** button to Mobile Settings and Desktop Reference Admin.
- **Regression Test Case**: Open Mobile Settings or Desktop Reference Admin $\to$ tap `Refresh Master Presets from Cloud` $\to$ verify live status feedback confirms `✅ Master presets updated successfully from cloud!`.

---

### BUG-060: Missing Active Preset File Path Display in Maintainer Tool
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Maintainers had no visual indication of which exact disk file path was being edited in the Maintainer Tool interface.
- **Root Cause**: Missing active file path status label in `MaintainerApp.kt`.
- **Resolution**: Added `📂 Active Preset File Path: ${getCategoryFile(selectedCategory).absolutePath}` to the main header area in `MaintainerApp.kt`, dynamically updating when switching category tabs.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ verify header displays the active file path (e.g. `.../resources/presets/cameras.json`).

---

### BUG-061: Enthusiast Lens Catalog Expansion across 8 Lens Mounts
- **Platform**: Fotologue Preset Catalog (`lenses.json`, `master_preset_catalog.json`)
- **Reported**: October 2026
- **Symptom**: The lens preset catalog only contained 26 lenses, lacking classic enthusiast prime and zoom optics for major SLR and Rangefinder mounts.
- **Root Cause**: Unpopulated lens catalog dataset.
- **Resolution**: Ingested 45 iconic enthusiast lenses across Canon FD/EF, Nikkor Ai-S, Pentax SMC/M42, Olympus Zuiko, Minolta MD/A, Leica M, and Zeiss C/Y mounts into `lenses.json`. Total lens presets expanded from 26 to **71 master lenses**.
- **Regression Test Case**: Launch Maintainer Tool `./gradlew :desktop:runMaintainer` $\to$ select `LENS` tab $\to$ verify total lens count displays 71 lenses.

---

### BUG-062: Ingestion of Flagship L-Series & Pro Optics into Lens Catalog
- **Platform**: Fotologue Preset Catalog (`lenses.json`, `master_preset_catalog.json`)
- **Reported**: October 2026
- **Symptom**: The lens preset catalog lacked flagship professional L-series optics (Canon EF 100mm f/2.8L Macro, EF 100-400mm f/4.5-5.6L IS), Nikkor ED Gold Ring lenses, Pentax Star `*` glass, and Zeiss Makro-Planar optics.
- **Root Cause**: Unpopulated flagship lens dataset.
- **Resolution**: Ingested 22 flagship L-Series and pro optics across Canon EF/FD, Nikkor Ai-S/AF-S, Pentax K, Minolta A/MD, Leica M, and Zeiss V mounts into `lenses.json`. Total lens presets expanded from 71 to **93 master lenses**.
- **Regression Test Case**: Launch Maintainer Tool `./gradlew :desktop:runMaintainer` $\to$ select `LENS` tab $\to$ verify total lens count displays 93 lenses including Canon EF 100mm f/2.8L Macro.

---

### BUG-063: System Gear Expansion for Teleconverters, Flashes & Viewfinders
- **Platform**: Fotologue Preset Catalog (`teleconverters.json`, `flashes.json`, `viewfinders.json`, `MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: The preset catalog was missing teleconverters, speedlights, pop-up flash types, and system viewfinders for the 82 camera bodies.
- **Root Cause**: Unpopulated system accessory datasets.
- **Resolution**: Enabled `Built-in / Pop-up Flash` LOV type in `MaintainerApp.kt`. Ingested 12 teleconverters (Canon FD/EF, Nikon F, Pentax K, Minolta MD/AF, Leica, Hasselblad), 14 flashes (including Canon Speedlite, Nikon SB, Vivitar 283, and Pop-up Flash), and 21 system viewfinders (including all 9 Pentax LX system finders).
- **Regression Test Case**: Launch Maintainer Tool `./gradlew :desktop:runMaintainer` $\to$ verify `FLASH` type dropdown contains `Built-in / Pop-up Flash` and `VIEWFINDER` lists all 9 Pentax LX system finders.

---

### BUG-064: Optical Filter Catalog Expansion across Hoya, B+W, Tiffen, and Bayonets
- **Platform**: Fotologue Preset Catalog (`filters.json`, `master_preset_catalog.json`)
- **Reported**: October 2026
- **Symptom**: The filter preset catalog only contained 3 items, lacking popular optical filters across major lens thread sizes and bayonet mounts.
- **Root Cause**: Unpopulated optical filter preset dataset.
- **Resolution**: Ingested 32 popular optical filters across Hoya, B+W/Schneider, Tiffen, Rollei Bayonet, and Hasselblad Bayonet mounts (UV, CPL, ND8, ND64, ND1000, Black Pro-Mist, Yellow K2, Orange 16, Red 25A). Total filter presets expanded from 3 to **35 master optical filters**.
- **Regression Test Case**: Launch Maintainer Tool `./gradlew :desktop:runMaintainer` $\to$ select `FILTER` tab $\to$ verify total filter count displays 35 filters.

---

### BUG-065: Unconstrained Consumable Brand LOV and Missing Controlled Type LOV
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Editing a Consumable preset in the Maintainer Tool displayed camera body manufacturers (*Alpa*, *Canon*, *Nikon*, *Pentax*) in the Brand dropdown, and `type` was an unconstrained text input.
- **Root Cause**: Un-tailored `CONSUMABLE` category options in `MaintainerApp.kt`.
- **Resolution**: Restricted Consumable Brand LOV to darkroom chemistry/paper manufacturers (*Ilford*, *Kodak*, *Agfa/Adox*, *Foma*, *Paterson*, *Cinestill*, *Fujifilm*, etc.). Converted `type` into a **Controlled Dropdown Selector** (`Developer (Film)`, `Developer (Paper)`, `Stop Bath`, `Fixer`, `Wetting Agent`, `Darkroom Paper`, etc.), and enabled `TYPE ▾` column header filtering in the Data Grid.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ select `CONSUMABLE` tab $\to$ click `Add New CONSUMABLE Preset Entry` $\to$ verify Brand dropdown lists darkroom manufacturers and `type` is a controlled dropdown.

---

### BUG-066: Unconstrained Equipment Brand LOV and Missing Controlled Type LOV
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Editing an Equipment preset in the Maintainer Tool displayed camera body manufacturers (*Alpa*, *Leica*, *Phase One*) in the Brand dropdown, and `type` was an unconstrained text input.
- **Root Cause**: Un-tailored `EQUIPMENT` category options in `MaintainerApp.kt`.
- **Resolution**: Restricted Equipment Brand LOV to darkroom hardware manufacturers (*Beseler*, *Durst*, *Paterson*, *Jobo*, *Omega/Saunders*, *Kaiser*, *LPL*, *Hama*, *Peak*). Converted `type` into a **Controlled Dropdown Selector** (`Enlarger`, `Dev Tank`, `Film Reel`, `Developing Tray`, `Darkroom Timer`, `Safelight`, `Thermometer`, etc.), and enabled `TYPE ▾` column header filtering in the Data Grid.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ select `EQUIPMENT` tab $\to$ click `Add New EQUIPMENT Preset Entry` $\to$ verify Brand dropdown lists darkroom hardware manufacturers and `type` is a controlled dropdown.

---

### BUG-067: Supplier Contact Preset Tailoring & Client Import Bridge
- **Platform**: Maintainer Tool, Mobile & Desktop (`suppliers.json`, `MaintainerApp.kt`, `DatabaseRepository.kt`, `Main.kt`, `AddEditSupplierScreen.kt`)
- **Reported**: October 2026
- **Symptom**: The Maintainer Tool displayed camera body brands (*Alpa*, *Canon*, *Nikon*) for `SUPPLIER` presets, and neither Mobile nor Desktop offered a 1-click way to import vendor contacts from presets.
- **Root Cause**: Un-tailored `SUPPLIER` category form in Maintainer Tool and missing preset import modals in client apps.
- **Resolution**: Tailored `SUPPLIER` in `MaintainerApp.kt` to hide Brand/Model fields, prompting for Vendor Name, Supplier Type, Region, and Website. Seeded starter vendor presets (*B&H Photo*, *Adorama*, *Analogue Wonderland*, *Vanbar*, *Rewind Photo Lab*) into `suppliers.json`. Added **`[ ⚙️ Select Supplier from Presets ]`** buttons to Desktop Contact form (Category 8) and Mobile `AddEditSupplierScreen.kt` for 1-click vendor contact ingestion.
- **Regression Test Case**: Open Add Contact on Mobile or Desktop $\to$ tap `Select Supplier from Presets` $\to$ select *B&H Photo Video* $\to$ verify Vendor Name, Website, Region, and `#Supplier` role auto-fill.

---

### BUG-068: Ingestion of Australian Suppliers, Processing Labs & WA Camera Stores
- **Platform**: Fotologue Preset Catalog (`suppliers.json`, `master_preset_catalog.json`)
- **Reported**: October 2026
- **Symptom**: The supplier preset catalog was missing major Australian film suppliers, processing labs, and Western Australian local camera stores.
- **Root Cause**: Unpopulated Australian supplier dataset.
- **Resolution**: Ingested 15 top Australian supplier, lab, and WA store presets (Camera Electronic Perth, Leederville Cameras, Fitzgerald Photo, Camera House, Georges, DigiDirect, Ted's Cameras, Walkens, Hillvale, FilmNeverDie, Do Film Lab, Halide Supply, Sydney Film Lab, Gold Street Studios). Total supplier presets expanded from 7 to **22 master vendor presets**.
- **Regression Test Case**: Open Add Contact on Mobile or Desktop $\to$ tap `Select Supplier from Presets` $\to$ verify Camera Electronic Perth and Fitzgerald Photo Imaging appear in dropdown list.

---

### BUG-069: Maintainer Data Grid Blank Column Display for Suppliers
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Viewing `SUPPLIER` presets in the Maintainer Data Grid rendered a blank `Preset Name` column and empty dash placeholders in `Mount / Spec`.
- **Root Cause**: Un-tailored data grid columns for business contacts in `MaintainerApp.kt`.
- **Resolution**: Re-architected Data Grid header and rows when `selectedCategory == "SUPPLIER"` to render **Vendor Name** (full name without string stripping), **Supplier Type**, **Region**, **Website**, and **Preset ID**.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ select `SUPPLIER` tab $\to$ verify Data Grid renders Vendor Name, Supplier Type, Region, and Website URL cleanly.

---

### BUG-070: Master Darkroom Consumables & Lab Hardware Catalog Expansion
- **Platform**: Fotologue Preset Catalog (`consumables.json`, `lab_equipment.json`, `master_preset_catalog.json`)
- **Reported**: October 2026
- **Symptom**: The consumables and equipment preset catalogs only contained 5 and 12 items respectively, lacking standard darkroom chemistry, papers, enlargers, and developing hardware.
- **Root Cause**: Unpopulated darkroom chemistry and lab hardware datasets.
- **Resolution**: Ingested 25 darkroom chemistry and paper presets into `consumables.json` (Kodak D-76, Xtol, HC-110, T-Max, Ilford ID-11, DD-X, Rapid Fixer, Ilfostop, Rodinal, Cinestill Cs41, Ilford Multigrade Papers) and 25 lab equipment presets into `lab_equipment.json` (Beseler 23C III, Durst M605/L1200, LPL 7700/4500II, Omega D5-XL, Paterson Super System 4, Jobo 1520/2520, GraLab 300, Peak Grain Scope). Consumables expanded to **30 master presets** and Lab Equipment expanded to **37 master presets**.
- **Regression Test Case**: Launch Maintainer Tool `./gradlew :desktop:runMaintainer` $\to$ select `CONSUMABLE` and `EQUIPMENT` tabs $\to$ verify total counts display 30 consumables and 37 equipment presets.

---

### BUG-071: Unnecessary Mount / Spec Column Rendered in Non-Mount Data Grids
- **Platform**: Fotologue Maintainer Tool (`desktop/src/main/kotlin/com/fotologue/desktop/MaintainerApp.kt`)
- **Reported**: October 2026
- **Symptom**: Viewing `CONSUMABLE`, `EQUIPMENT`, `FILM`, `FILTER`, `FLASH`, or `VIEWFINDER` presets in the Maintainer Data Grid displayed an unnecessary `Mount / Spec` column with empty dash placeholders (`—`).
- **Root Cause**: Unconditional rendering of `Mount / Spec` column in `MaintainerApp.kt`.
- **Resolution**: Conditionally hid `Mount / Spec` column when `showMountFilter == false`, re-allocating column weights so `Brand`, `Preset Name`, `Type`, and `Preset ID` fill the grid cleanly without empty placeholders.
- **Regression Test Case**: Launch `./gradlew :desktop:runMaintainer` $\to$ select `CONSUMABLE` or `EQUIPMENT` tab $\to$ verify `Mount / Spec` column is hidden and remaining columns expand smoothly.

---

### BUG-072: Comprehensive Master Documentation Suite Alignment for v3.0 Release
- **Platform**: Project Documentation (`README.md`, `user_guide.md`, `training_manual.md`, `master_preset_json_schema.md`, `store_listing_descriptions.md`, `PRIVACY_POLICY.md`, `lexicon.md`, `maintainer_preset_workflow_architecture.md`)
- **Reported**: October 2026
- **Symptom**: Documentation assets contained outdated v1.0 version numbers, lacked JSON schema specifications for all 10 categories, and omitted v3.0 features (Data Grid, Cloud Refresh, 82 Cameras, 93 Lenses, 35 Filters, 22 Suppliers).
- **Root Cause**: Un-updated documentation files following rapid v3.0 preset and Maintainer Tool architecture expansions.
- **Resolution**: Created `docs/master_preset_json_schema.md` with full JSON schema definitions for all 10 categories. Updated User Guide, Training Manual, README, Store Listings, Privacy Policy (documenting zero-harvesting native contact picker & read-only cloud refresh), Lexicon, and Maintainer Architecture.
- **Regression Test Case**: Review all `.md` files $\to$ verify version 3.0.0 alignment, zero broken markdown links, and accurate feature descriptions.

---

### BUG-073: Ingestion of Classic Pentax Zoom Lenses Reaching 100 Master Lenses Milestone
- **Platform**: Fotologue Preset Catalog (`lenses.json`, `master_preset_catalog.json`)
- **Reported**: October 2026
- **Symptom**: The lens preset catalog lacked classic Pentax zoom lenses (SMC Pentax-A 70-210mm f/4, SMC Pentax-M 80-200mm f/4.5, SMC Pentax-FA* 28-70mm f/2.8 AL, SMC Pentax-FA* 80-200mm f/2.8 ED).
- **Root Cause**: Unpopulated Pentax zoom lens dataset.
- **Resolution**: Ingested 7 classic Pentax zoom lenses into `lenses.json`. Total lens presets expanded from 93 to **100 master lenses**.
- **Regression Test Case**: Launch Maintainer Tool `./gradlew :desktop:runMaintainer` $\to$ select `LENS` tab $\to$ filter by Brand `Pentax` & Type `Zoom` $\to$ verify total lens count displays 100 lenses.

---

### BUG-074: 10-Bug Quality Sprint (Filter Lens Attachments, Preset Modals, Interchangeability Guards & Form Automations)
- **Platform**: Fotologue Mobile & Desktop (`AddEditFilterScreen.kt`, `AddEditFlashScreen.kt`, `AddEditViewfinderScreen.kt`, `AddEditCameraKitScreen.kt`, `AddLogScreen.kt`, `LabMaintenanceScreen.kt`, `AddEditInventoryMasterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: 10 UX and data-association bugs: Filter asking for Associated Cameras instead of Lenses, Flash and Viewfinder screens missing preset buttons, Canon AE-1 listed for viewfinders, Teleconverter dropdown showing "None" when empty, single Film/Lens not auto-selecting, Maintenance child inputs above parents, Manufacturers not renamed to Brands, "e.g." placeholders in LOVs, and new film stocks not refreshing in Add Exposure Log.
- **Root Cause**: Un-refined form flows, missing preset modals, and un-updated UI copy.
- **Resolution**:
  1. Updated `AddEditFilterScreen.kt` to associate filters with Lenses (via thread size/bayonet).
  2. Added `Select Flash from Presets` button to `AddEditFlashScreen.kt`.
  3. Filtered `AddEditViewfinderScreen.kt` cameras to exclude fixed-prism bodies (*Canon AE-1 Program*) and added `Select Viewfinder from Presets` button.
  4. Hid Teleconverter dropdown in Kit/Log forms when empty.
  5. Auto-selected single available Film and single Lens by default.
  6. Re-ordered Parent Category dropdown above Child input in Maintenance screens.
  7. Renamed `Maintenance • Manufacturers` to `Maintenance • Brands`.
  8. Removed `"e.g. ..."` placeholder text from LOV selectors.
  9. Dynamically refreshed film list and cleaned duplicate brand string prefixes (*"Canon Canon FD..."* $\to$ *"Canon FD 50mm f/1.8 nFD"*).
- **Regression Test Case**: Open Add Filter $\to$ verify Associated Lenses chips display; open Add Exposure Log with 1 film in library $\to$ verify film auto-selects and duplicate brand prefixes are stripped.

---

### BUG-075: Camera Permission Crash, Un-Aligned Preset Modals & 120 Format Frame Count Validation
- **Platform**: Fotologue Mobile & Desktop (`AddLogScreen.kt`, `LightMeterScreen.kt`, `AddEditFilterScreen.kt`, `AddEditFlashScreen.kt`, `AddEditViewfinderScreen.kt`, `AddEditFilmViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: Tapping Measure Exposure crashed with a SecurityException when camera permission was not granted; Add Filter still showed camera chips and duplicate brand prefixes; Flash and Viewfinder preset dialogs lacked brand-first filtering; 120 medium format film defaulted to 36 exposures.
- **Root Cause**: Un-guarded camera provider binding, un-aligned preset dialogs, and un-validated film format frame counts.
- **Resolution**:
  1. Wrapped Measure Exposure in `LightMeterScreen.kt` with a strict `rememberPermissionState(Manifest.permission.CAMERA)` guard to request permission before launching.
  2. Removed Camera chips from `AddFilterScreen.kt` and Desktop `Main.kt`, and applied `cleanLensName` to strip brand tautologies.
  3. Re-architected Flash and Viewfinder preset selection modals with Brand-First Cascading Filters.
  4. Enforced 12 exposure default for 120 medium format film in `AddEditFilmViewModel.kt`.
- **Regression Test Case**: Revoke Camera permission $\to$ tap `Measure Exposure` $\to$ verify permission request dialog launches safely without crashing; open 120 film creation $\to$ verify expected exposures defaults to 12.

---

### BUG-076: Default Film Stock Brand Filter Pre-Selection in Film Presets Dialog
- **Platform**: Mobile & Desktop (`AddEditFilmScreen.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Opening `Select Film Stock from Presets` defaulted the brand filter to `"Kodak"`, hiding non-Kodak film stocks (*Ilford*, *Fujifilm*, *Cinestill*, *Foma*) until manually cleared.
- **Root Cause**: Hardcoded initial `selectedBrandFilter` state value `"Kodak"`.
- **Resolution**: Changed default `selectedBrandFilter` in `AddEditFilmScreen.kt` and `selectedFilmManufacturer` in `Main.kt` from `"Kodak"` to **`"All"`**.
- **Regression Test Case**: Open `Select Film Stock from Presets` on Mobile or Desktop $\to$ verify brand filter defaults to `All` and all master film stocks display immediately.

---

### BUG-077: Skullinator Protocol Quality Sprint (Medium Format Camera Presets, Format Filters & CameraX Permission Guard)
- **Platform**: Fotologue Mobile & Desktop (`cameras.json`, `AddEditFilmScreen.kt`, `AddLogScreen.kt`, `AddLogViewModel.kt`, `LightMeterScreen.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Yashica Mat-124G TLR defaulted to 35mm format in presets causing film dropdown mismatches; Film Presets Dialog lacked a Film Format filter; lens dropdowns showed duplicate brand names; LightMeter crashed on fresh install before permission granted.
- **Root Cause**: Unrepresented format attribute in `cameras.json`, missing format filter in film preset dialog, and unguarded CameraX lifecycle binding.
- **Resolution**:
  1. Updated `cameras.json` to explicitly define `"format": "120 (Medium Format)"` for all TLR and Medium Format camera presets.
  2. Added **`Filter by Film Format`** dropdown (`All`, `35mm`, `120 (Medium Format)`, `Large Format / Sheet`) to `FilmPresetsDialog` on Mobile and Desktop.
  3. Strict format filtering enforced (`formatsMatch(camera.format, film.format)`) with 1-click **`[ ➕ Add a {CameraFormat} Film Stock... ]`** pre-filled button when no matching film stock exists.
  4. Updated `cleanLensName` to strip duplicate brand prefixes (*"Yashica Yashinon..."* $\to$ *"Yashinon 80mm f/3.5 Taking Lens"*).
  5. Added `rememberPermissionState` and `ContextCompat.checkSelfPermission` guards in `LightMeterScreen.kt` to display a full-screen permission request layout if ungranted.
- **Regression Test Case**: Clean install app $\to$ select *Yashica Mat-124G* TLR $\to$ verify format is `120 (Medium Format)` and 120 film stock appears in the Film dropdown; tap `Measure Exposure` with revoked permission $\to$ verify permission prompt renders without crashing.

---

### BUG-078: LocationProvider SecurityException Guard and Native Permission Launchers
- **Platform**: Fotologue Mobile (`LocationProvider.kt`, `AddLogScreen.kt`, `LightMeterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Calling Measure Exposure or initializing Light Meter crashed the app when GPS location or camera permissions were missing.
- **Root Cause**: `LocationProvider.kt` invoked `fusedLocationClient.getCurrentLocation()` without checking `ContextCompat.checkSelfPermission`, throwing an unhandled `SecurityException` when permission was missing.
- **Resolution**:
  1. Wrapped `LocationProvider.kt` with explicit `ContextCompat.checkSelfPermission` checks for `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`, returning `null` safely if permissions are not granted.
  2. Migrated `AddLogScreen.kt` and `LightMeterScreen.kt` to native Android `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())` with `ContextCompat` pre-checks.
  3. Made FloatingActionButton save action non-blocking.
- **Regression Test Case**: Revoke Location and Camera permissions in Android App Info $\to$ open `Add Exposure Log` $\to$ tap `Measure Exposure` $\to$ verify permission prompt displays and app does not crash.

---

### BUG-079: Filter-As-You-Type Dynamic Search Query Dropdowns
- **Platform**: Fotologue Mobile (`AddLogScreen.kt`, `AddEditCameraKitScreen.kt`, `AddEditInventoryMasterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Selecting a camera, lens, film, kit, or darkroom inventory item required scrolling through long static lists without real-time text search filtering.
- **Root Cause**: `ExposedDropdownMenuBox` text fields were set to `readOnly = true` with `MenuAnchorType.PrimaryNotEditable`.
- **Resolution**: Upgraded dropdown selectors to `readOnly = false` with `MenuAnchorType.PrimaryEditable`, filtering options dynamically in real-time as the user types.
- **Regression Test Case**: Open `Add Exposure Log` $\to$ tap `Camera` dropdown $\to$ type "Yash" $\to$ verify dropdown filters in real-time to *Yashica Mat-124G*.

---

### BUG-080: CameraX Surface Combination Concurrency Crash Guard in LightMeter
- **Platform**: Fotologue Mobile (`LightMeterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Tapping `Measure Exposure` crashed the app on certain hardware devices and emulators even when Camera and Location permissions were granted.
- **Root Cause**: `LightMeterScreen.kt` bound 3 concurrent CameraX use cases (`preview` + `imageAnalysis` + `imageCapture`) simultaneously without `unbindAll()`, exceeding max surface combination capability limits on limited hardware devices.
- **Resolution**:
  1. Added `cameraProvider.unbindAll()` prior to every CameraX binding call.
  2. Wrapped `bindToLifecycle` in a try-catch block to fall back to a 2-use-case binding (`preview` + `imageAnalysis`) if 3 concurrent streams are unsupported.
- **Regression Test Case**: Open `Add Exposure Log` $\to$ tap `Measure Exposure` on an emulator or limited hardware device $\to$ verify live metering view loads smoothly without crashing.

---

### BUG-081: Permanent Elimination of CameraX ImageProxy Memory Leaks and Buffer Starvation Crashes
- **Platform**: Fotologue Mobile (`LuminanceAnalyzer.kt`, `LightMeterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: LightMeter crashed during live video analysis on devices/emulators with unhandled ML Kit errors or frozen video feeds.
- **Root Cause**: Unhandled native `Throwable` errors during ML Kit async face processing prevented `image.close()` execution, leading to CameraX frame buffer starvation and fatal crashes.
- **Resolution**:
  1. Wrapped ML Kit face detection in a strict `try-catch (t: Throwable)` block with guaranteed `image.close()` execution in `finally`.
  2. Applied safe byte buffer bounds checking (`index in 0 until buffer.limit()`).
  3. Added `cameraProviderFuture.get()` `try-catch (t: Throwable)` exception handler in `LightMeterScreen.kt`.
- **Regression Test Case**: Open `Add Exposure Log` $\to$ tap `Measure Exposure` $\to$ verify live metering view operates continuously without frame drops or crashes.

---

### BUG-082: FilmPresetsDialog Format Selection Pre-Fill & CameraX Fallback Recovery
- **Platform**: Fotologue Mobile (`AddEditFilmScreen.kt`, `AddEditFilmViewModel.kt`, `LightMeterScreen.kt`, `ExposureSuggester.kt`)
- **Reported**: October 2026
- **Symptom**: Selecting `120 (Medium Format)` in `FilmPresetsDialog` evaluated to empty presets due to hardcoded `matchesFormat = All`; LightMeter crashed on 2-use-case retry.
- **Root Cause**: Unapplied `selectedFormatFilter` in `preset.filter` and missing `unbindAll()` in LightMeter CameraX fallback handler.
- **Resolution**:
  1. Updated `FilmPresetsDialog` in `AddEditFilmScreen.kt` & `AddEditFilmViewModel.kt` to apply `selectedFormatFilter` to form format and expected exposures (12 exp for 120).
  2. Added `cameraProvider.unbindAll()` inside LightMeter fallback catch block before 2-use-case binding.
  3. Guarded `ExposureSuggester.kt` aperture range calculations with `minOf` and `maxOf`.
- **Regression Test Case**: Open `Select Film Stock from Presets` $\to$ select `120 (Medium Format)` $\to$ select *Ilford HP5 Plus 400* $\to$ verify main form receives `120 (Medium Format)` and `12 exp`.

---

### BUG-083: Filter-As-You-Type Soft Keyboard Search Across All Preset Modal Filter Dropdowns
- **Platform**: Fotologue Mobile (`AddEditCameraScreen.kt`, `AddEditLensScreen.kt`, `AddEditFilmScreen.kt`, `AddEditFlashScreen.kt`, `AddEditViewfinderScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Tapping filter dropdowns inside preset modals did not launch the soft keyboard or permit text query filtering.
- **Root Cause**: Filter dropdown text fields were configured with `readOnly = true` and `MenuAnchorType.PrimaryNotEditable`.
- **Resolution**: Converted all filter dropdowns across all 5 preset modals to `readOnly = false` with `MenuAnchorType.PrimaryEditable`, launching the soft keyboard and filtering options in real-time as the user types.
- **Regression Test Case**: Open `Select Camera Body from Presets` $\to$ tap `Filter by Camera Brand` $\to$ verify soft keyboard launches $\to$ type "Nik" $\to$ verify brand dropdown filters in real-time to *Nikon*.

---

### BUG-084: LightMeter Navigation 60fps Coroutine Recomposition Crash Elimination
- **Platform**: Fotologue Mobile (`MainActivity.kt`, `WelcomeScreen.kt`, `AddEditCameraViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: Tapping `Measure Exposure` caused LightMeter to spawn hundreds of active coroutines per second, crashing the app due to coroutine stack overflow; Onboarding lacked GPS permission card; Edit Camera mode had blank Brand field.
- **Root Cause**: `viewModel.setParams(...)` was invoked directly in the Composable body of `entry<Route.LightMeter>` without `LaunchedEffect(lightRoute)`.
- **Resolution**:
  1. Wrapped `viewModel.setParams(...)` inside `LaunchedEffect(lightRoute)` in `MainActivity.kt`.
  2. Added an "Automatic GPS Location Tagging" permission card to `WelcomeScreen.kt`.
  3. Pre-filled `make = c.name.substringBefore(" ")` in `loadCamera()` in `AddEditCameraViewModel.kt`.
- **Regression Test Case**: Open `Add Exposure Log` $\to$ tap `Measure Exposure` $\to$ verify `LightMeterScreen` opens instantly and operates smoothly with zero crashes.

---

### BUG-085: ExposureSuggester Shutter Speed Range Parse Crash Resolution
- **Platform**: Fotologue Mobile (`ExposureSuggester.kt`)
- **Reported**: October 2026
- **Symptom**: Opening LightMeter or tapping Measure Exposure crashed with `NumberFormatException: For input string: "1s - 1"` when camera shutter speeds contained range strings.
- **Root Cause**: `parseShutterSpeed()` split raw range strings like `"1s - 1/500s, B"` on `/`, producing `"1s - 1"` and attempting `Double.parseDouble()`.
- **Resolution**:
  1. Implemented `expandShutterSpeeds()` in `ExposureSuggester.kt` to expand raw shutter speed range strings into discrete shutter speed values.
  2. Wrapped `parseShutterSpeed()` in a `try-catch` block returning `1.0` fallback on malformed input strings.
- **Regression Test Case**: Select *Yashica Mat-124G* TLR $\to$ open `Add Exposure Log` $\to$ tap `Measure Exposure` $\to$ verify live Light Meter opens cleanly with zero crashes.

---

### BUG-086: Full-Screen Preset Catalog Browsers, Single-Camera Auto-Selection & Top Form Warning Save
- **Platform**: Fotologue Mobile (`AddEditFilmScreen.kt`, `AddEditLensScreen.kt`, `AddEditFilterScreen.kt`, `AddEditFlashScreen.kt`, `AddEditViewfinderScreen.kt`, `AddEditCameraKitViewModel.kt`, `AddEditViewfinderViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: Preset dialogs were cramped floating popups; Add Kit Bag allowed selecting 35mm film for 120 TLRs; Yashica Mat-124G appeared under Viewfinders; missing associated camera warnings were hidden at bottom of form.
- **Root Cause**: Floating AlertDialog UI layout, un-filtered Kit Bag film flow, and missing TLR exclusion in Viewfinders.
- **Resolution**:
  1. Re-architected `Film`, `Lens`, `Filter`, `Flash`, and `Viewfinder` preset selection into Full-Screen Preset Catalog Browsers.
  2. Filtered `Add Kit Bag` film dropdown by camera format (`camera.format`).
  3. Excluded fixed TLRs (*Yashica Mat-124G*, *Rolleiflex 2.8F*) from `Add Viewfinder`.
  4. Implemented single-camera auto-selection when 1 camera exists in user's library.
  5. Added immediate top red error warning banner directly under `Associated Cameras` / `Associated Lenses` chips with a 1-tap top `[ 💾 Save ]` button.
- **Regression Test Case**: Open `Add Film` or `Add Lens` $\to$ verify full-screen preset browser opens; open `Add Kit Bag` with *Yashica Mat-124G* TLR $\to$ verify Film dropdown lists only 120 films.

---

### BUG-087: Desktop Master Rolls QA Frame Inspector Notes & Tags Inline Editing
- **Platform**: Fotologue Desktop (`Main.kt`)
- **Reported**: October 2026
- **Symptom**: During Macro Copy scanning and Master Roll inspection on Desktop, frame Notes/Comments and Tags were read-only, preventing photographers from correcting comments and tags on the fly.
- **Root Cause**: Static `Text("Notes: ...")` and `Text("Tags: ...")` UI rendering in `Main.kt`.
- **Resolution**:
  1. Replaced static text with interactive, multi-line `OutlinedTextField` for `Notes / Comments` and comma-separated `Tags`.
  2. Implemented dynamic hashtag chip rendering (`#Bike`, `#Street`, `#Outdoor`).
  3. Integrated direct database persistence via `DatabaseRepository.saveRoll()`.
- **Regression Test Case**: Open Desktop App $\to$ Master Rolls QA Inspector $\to$ select a frame $\to$ edit Notes and Tags $\to$ verify hashtag chips render and edits persist automatically.

---

### BUG-088: Desktop TAXONOMY & TAGS High-Density Management Module
- **Platform**: Fotologue Desktop (`Main.kt`)
- **Reported**: October 2026
- **Symptom**: Desktop lacked a dedicated Taxonomy & Tags administration module to manage taxonomy categories and tags with high data density.
- **Root Cause**: Unrepresented Taxonomy navigation destination in Desktop `FoundationAdminLeftRail`.
- **Resolution**:
  1. Integrated **`TAXONOMY & TAGS`** navigation section into `FoundationAdminLeftRail` with live count badges.
  2. Built `TaxonomyAdminPanel` with a high-density Sortable Data Grid (`TAG NAME`, `CATEGORY`, `USAGE FREQUENCY`, `SOURCE`, `ACTIONS`).
  3. Added `[ + Add New Tag ]`, `[ Edit Tag ]` (global tag renaming across Master Vault), `[ Delete Tag ]`, and `[ + Add Category ]` CRUD dialogs.
- **Regression Test Case**: Open Desktop App $\to$ click `TAXONOMY & TAGS` on left rail $\to$ verify Data Grid loads with `Subjects`, `Content`, and `Intent` tags and supports global tag editing.

---

### BUG-089: Desktop Interactive Taxonomy Tag Picker & Sub-Group Alignment
- **Platform**: Fotologue Desktop (`Main.kt`)
- **Reported**: October 2026
- **Symptom**: Desktop QA Inspector used free-form comma-separated text entry for tags; Data Grid lacked Sub-Group/Parent Subject alignment with Mobile.
- **Root Cause**: Un-aligned UI controls on Desktop Master Rolls QA Inspector.
- **Resolution**:
  1. Replaced comma-separated text box with Mobile's **Interactive Taxonomy Dropdown & Multi-Select Chip Picker** (`Subject` Dropdown $\to$ `Content` Chips, `Intent` Dropdown $\to$ `Intent Sub-Tags` Chips).
  2. Added **`SUB-GROUP / PARENT SUBJECT`** column to Desktop `TaxonomyAdminPanel` Data Grid.
  3. Aligned Desktop `Edit Tag` dialog to 2-tier structure (`Associated Subject` dropdown + `Content Name` text field).
- **Regression Test Case**: Open Desktop App $\to$ Master Rolls QA Inspector $\to$ select frame $\to$ verify Subject dropdown and Content chips render and toggle tags dynamically.

---

### BUG-090: Desktop QA Inspector Dropdown Clickability & Auto-Subject Detection
- **Platform**: Fotologue Desktop (`Main.kt`)
- **Reported**: October 2026
- **Symptom**: Clicking Subject Group or Intent Category dropdowns in Desktop QA Inspector failed to open menus due to OutlinedTextField click interception; active frame tags belonged to unselected subject groups.
- **Root Cause**: OutlinedTextField internal pointer event consumption; hardcoded default selectedSubject = "Events".
- **Resolution**:
  1. Wrapped Subject/Intent dropdowns in overlaid clickable `Box` containers with explicit `IconButton`s.
  2. Implemented automatic detection of `selectedSubject` and `selectedIntent` based on `activeFrame.tags` (e.g. `#Portrait` $\to$ `People & Portraits`).
  3. Added 1-tap `[ × ]` tag removal on `Active Tags:` chips.
  4. Added `Custom Tag` input field with `[ Add Tag ]` button.
- **Regression Test Case**: Open Desktop App $\to$ Master Rolls QA Inspector $\to$ select frame with `#Portrait` $\to$ verify Subject Group auto-detects `People & Portraits` and dropdown opens on click.

---

### BUG-091: Auto-Link Frames Flexible Capacity & Index Safety
- **Platform**: Fotologue Desktop (`Main.kt`)
- **Reported**: October 2026
- **Symptom**: Clicking Auto-Link Frames threw `IndexOutOfBoundsException: index: 25, size: 25` and blocked merges when frame counts exceeded nominal capacity.
- **Root Cause**: Hardcoded capacity checks and index removal `removeAt(selectedRollIndex)` causing array index shifts.
- **Resolution**:
  1. Removed blocking Auto-Link frame capacity check to support bulk-loaded ("rolling your own") and extended film rolls up to 45 exposures.
  2. Replaced `removeAt(selectedRollIndex)` with object-based removal `masterVaultRolls.remove(orphanRoll)` and safe index clamping.
  3. Wrapped Auto-Link execution in `try-catch (e: Exception)` block.
- **Regression Test Case**: Open Desktop App $\to$ Master Rolls view $\to$ click `Auto-Link Frames` $\to$ verify unassigned frame merges cleanly with zero index errors or popup dialogs.

---

### BUG-092: Standalone Mobile Taxonomy Screen & Hub Brand Icon Integration
- **Platform**: Fotologue Mobile (`HubScreen.kt`, `TaxonomyScreen.kt`, `TaxonomyViewModel.kt`, `GearLibraryScreen.kt`, `TimelineScreen.kt`, `Routes.kt`, `MainActivity.kt`)
- **Reported**: October 2026
- **Symptom**: Taxonomy metadata sat buried inside Camera Gear; Hub header lacked official brand camera icon.
- **Root Cause**: Unrepresented Taxonomy route; unaligned Information Architecture on Mobile Staging Portal.
- **Resolution**:
  1. Added official Fotologue brand camera icon (`settings_photo_camera_24px`) to top header of `HubScreen.kt`.
  2. Elevated **`TAXONOMY & TAGS`** to dedicated Module Card #2 on `HubScreen.kt` with `ic_label` icon.
  3. Created standalone `TaxonomyScreen.kt` and `TaxonomyViewModel.kt` displaying `Subjects (4)`, `Content (30)`, and `Intent (14)` with sub-group navigation.
  4. Cleaned `GearLibraryScreen.kt` so `CAMERA GEAR` remains 100% focused on physical equipment.
  5. Added `Taxonomy & Tags` quick-access icon (`ic_label`) alongside `Camera Gear` in `FIELD` top bar (`TimelineScreen.kt`).
- **Regression Test Case**: Open Mobile Staging Portal $\to$ verify brand camera icon renders in top bar and `TAXONOMY & TAGS` appears as Module Card #2.

---

### BUG-093: Official Circular 'fl' Fotologue Brand Logo Integration in Hub Header
- **Platform**: Fotologue Mobile (`HubScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Mobile Staging Portal header used generic camera settings vector icon next to `Fotologue` title instead of the official brand logo.
- **Root Cause**: Placeholder vector drawable in `HubScreen.kt` top app bar.
- **Resolution**: Replaced placeholder vector icon with official high-resolution circular `fl` Fotologue logo (`R.mipmap.ic_launcher_round`), rendering silver gradient, red viewfinder grid lines, and italic `fl` typography.
- **Regression Test Case**: Open Mobile Staging Portal $\to$ verify official circular `fl` logo renders next to `Fotologue` title in top bar.

---

### BUG-094: Launch Crash Resolution & Full-Screen Film Catalog Browser Architecture
- **Platform**: Fotologue Mobile (`HubScreen.kt`, `AddEditFilmScreen.kt`, `WelcomeScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Cold launch crashed due to painterResource loading adaptive icon XML; Add Film preset selection displayed a cramped floating popup; form save actions were disconnected.
- **Root Cause**: `painterResource(R.mipmap.ic_launcher_round)` called on adaptive icon; floating AlertDialog UI; missing save triggers on detail form view.
- **Resolution**:
  1. Replaced `R.mipmap.ic_launcher_round` in `HubScreen.kt` with `R.drawable.settings_photo_camera_24px` VectorDrawable, restoring 100% startup stability.
  2. Re-architected `AddEditFilmScreen.kt` to remove `FilmPresetsDialog` `AlertDialog` and render `FilmPresetsBrowser` as a full-screen view.
  3. Connected both bottom `[ Save Film ]` and top checkmark `✓` to `viewModel.saveFilm(onBack)`.
  4. Added brand camera icon to `WelcomeScreen.kt` top app bar.
- **Regression Test Case**: Clean launch Mobile app $\to$ verify cold launch succeeds $\to$ open `Add Film` $\to$ verify full-screen preset browser opens and saves cleanly.

---

### BUG-095: High-Res Circular 'fl' Fotologue Brand Logo Integration
- **Platform**: Fotologue Mobile (`HubScreen.kt`, `WelcomeScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Header displayed camera gear settings icon instead of the official circular 'fl' brand logo.
- **Root Cause**: Temporary fallback to `settings_photo_camera_24px` vector icon.
- **Resolution**: Copied official 512x512 high-resolution `ic_launcher-playstore.png` to `res/drawable/ic_fotologue_logo.png` and rendered in `HubScreen.kt` and `WelcomeScreen.kt` headers.
- **Regression Test Case**: Open Mobile App $\to$ verify official circular `fl` logo renders in top bar.

---

### BUG-096: CircleShape Logo Clipping & Edit Film State Invariant Realignment
- **Platform**: Fotologue Mobile (`HubScreen.kt`, `WelcomeScreen.kt`, `AddEditFilmScreen.kt`, `GearLibraryScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Outer green checkerboard pixels rendered around the logo; Edit Film opened as Preset Browser instead of Edit Form; Gear Library category indices were offset.
- **Root Cause**: `ic_fotologue_logo.png` outer background; `isCustomFormView` remember state evaluation timing; legacy category index offset.
- **Resolution**:
  1. Applied `Modifier.clip(CircleShape)` to `Image` composables in `HubScreen.kt` and `WelcomeScreen.kt`, cleanly clipping away all outer green pixels.
  2. Fixed `isCustomFormView` remember state in `AddEditFilmScreen.kt` (`var isCustomFormView by remember(uiState.name, viewModel.filmId) { mutableStateOf(viewModel.filmId != null || uiState.name.isNotBlank()) }`), displaying the Edit Form immediately when editing existing films.
  3. Re-aligned `GearLibraryScreen.kt` category index handlers to match the 8 physical hardware categories (`0`=Cameras, `1`=Lenses, `2`=Films ... `7`=Kit Bags).
- **Regression Test Case**: Open Mobile App $\to$ verify top bar renders clean circular `fl` logo $\to$ open `Camera Gear` $\to$ tap `Films` $\to$ tap an existing film $\to$ verify pre-filled Edit Film form opens cleanly.

---

### BUG-097: Skullinator Protocol Alignment for Mobile Taxonomy Screen & Onboarding Camera-Format Pre-Filtering
- **Platform**: Fotologue Mobile (`TaxonomyScreen.kt`, `TaxonomyViewModel.kt`, `AddEditFilmViewModel.kt`, `Skullinator.md`)
- **Reported**: October 2026
- **Symptom**: Mobile Taxonomy screen was non-interactive ("dead"); Onboarding film selection did not match chosen camera format.
- **Root Cause**: Static hardcoded sub-group list in `TaxonomyScreen.kt` without SQLite flow binding; missing camera format inspection in `AddEditFilmViewModel.kt`.
- **Resolution**:
  1. Applied The Skullinator Protocol: Connected `TaxonomyViewModel` to `repository.allTags` Flow in SQLite with non-zero assertion and auto-seeding.
  2. Implemented dynamic category tab filtering (`All`, `Subjects`, `Content`, `Intent`), 1-tap card edit dialogs, delete confirmation dialogs, and FAB `[ ➕ ]` tag creation.
  3. Auto-detected primary camera format in `AddEditFilmViewModel.kt` during Onboarding/gear setup (e.g. 120 TLRs $\to$ `Film.FORMAT_120`), pre-selecting 120 film stock presets automatically.
- **Regression Test Case**: Open Mobile App $\to$ `TAXONOMY & TAGS` $\to$ tap `Subjects` chip $\to$ verify list filters dynamically and tapping cards opens Edit Tag dialog.

---

### BUG-098: Mobile Taxonomy Parent-Child SQLite Hierarchy Resolution & 2-Field Edit Dialog
- **Platform**: Fotologue Mobile (`TaxonomyScreen.kt`, `TaxonomyViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: T&T screen displayed Parent Subject names (e.g. `Urban/built environment`) as individual tags under section `General`; category filtering failed.
- **Root Cause**: Category string constant mismatch (`Tag.CATEGORY_SUBJECT` = `"Subject"` vs `"Subjects"`); missing `parentId` Subject lookup against SQLite tags.
- **Resolution**:
  1. Normalized category string matching across `"Subject"`, `"Subjects"`, `"Content"`, and `"Intent"`.
  2. Mapped `parentId` to Parent Subject names directly from SQLite (`Urban/built environment`, `Events`, `Nature`, `People`), rendering them as section headers with child content tags (`Architecture`, `Street`, `Wedding`, `Landscape`) under each header.
  3. Aligned `Edit Content` dialog to 2-field structure (`Associated Subject` dropdown + `Content Name` text field).
- **Regression Test Case**: Open Mobile App $\to$ `TAXONOMY & TAGS` $\to$ verify `Urban/built environment`, `Events`, `Nature`, and `People` render as section headers with child tags underneath and tapping cards opens 2-field edit dialog.

---

### BUG-099: Add Taxonomy Tag Associated Subject Selector Integration
- **Platform**: Fotologue Mobile (`TaxonomyScreen.kt`)
- **Reported**: October 2026
- **Symptom**: `Add Taxonomy Tag` dialog on Mobile lacked an `Associated Subject` selector, creating orphan content tags without parent subjects.
- **Root Cause**: Un-implemented parent subject dropdown in Add Tag dialog.
- **Resolution**: Added `Associated Subject` dropdown (populated with `Urban/built environment`, `Events`, `Nature`, `People`) to `Add Taxonomy Tag` dialog, persisting parent ID to SQLite upon creation.
- **Regression Test Case**: Open Mobile App $\to$ `TAXONOMY & TAGS` $\to$ tap `[ ➕ ]` FAB $\to$ verify `Associated Subject` dropdown renders and links new content tag under chosen subject header.

---

### BUG-100: Default Enablement of Automatic Thumbnail Capture for Metered Logs
- **Platform**: Core & Mobile App (`SettingsRepository.kt`, `OnboardingViewModel.kt`)
- **Reported**: October 2026
- **Symptom**: Metering an exposure log on a new install did not render a photo thumbnail on the log list item.
- **Root Cause**: `SettingsRepository.kt` defaulted `IS_THUMBNAIL_ENABLED` to `false`, causing LightMeter to skip camera thumbnail capture during metering.
- **Resolution**: Updated `SettingsRepository.kt` and `OnboardingViewModel.kt` to default `IS_THUMBNAIL_ENABLED` to `true`. Camera thumbnail photos are now captured during metering and rendered as 64dp thumbnails on log cards.
- **Regression Test Case**: Clean install Mobile app $\to$ complete Onboarding $\to$ Add Log $\to$ Measure Exposure $\to$ tap Use $\to$ Save Log $\to$ verify 64dp photo thumbnail renders on log list card.

---

### BUG-101: Reversion of Automatic Thumbnail Capture Default to User Choice
- **Platform**: Core & Mobile App (`SettingsRepository.kt`, `OnboardingViewModel.kt`, `LightMeterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: User requested reverting Automatic Thumbnail Capture back to disabled by default.
- **Root Cause**: Overly assertive default setting.
- **Resolution**: Reverted `IS_THUMBNAIL_ENABLED` default setting back to `false` in `SettingsRepository.kt` and `OnboardingViewModel.kt`, while preserving instant 0ms `previewView.bitmap` capture whenever the photographer explicitly toggles it ON.
- **Regression Test Case**: Clean install Mobile app $\to$ verify Automatic Thumbnail Capture defaults to OFF in Quick Settings.

---

### BUG-102: M3 Segmented Safelight Control, Darkroom Light Guard & FlatLaf Themes
- **Platform**: Core & Mobile App (`Theme.kt`, `SettingsRepository.kt`, `DarkroomLightGuard.kt`, `HubScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Need for M3 Segmented Safelight control (`Auto` / `On` / `Off`), ambient light sensor darkroom guard, ISO 3664 photo review canvas, and FlatLaf/photographic theme engine.
- **Root Cause**: Unrepresented M3 Segmented Button control and theme palette options.
- **Resolution**:
  1. Built `DarkroomLightGuard.kt` utilizing `Sensor.TYPE_LIGHT` ($< 0.01\text{ mA}$) for `Auto` Safelight control (ON $\le 3$ lux, OFF $\ge 20$ lux).
  2. Implemented Material 3 `SingleChoiceSegmentedButtonRow` (`Auto` / `On` / `Off`) in `HubScreen.kt` top app bar.
  3. Expanded `Theme.kt` with `PhotographicTheme` (`FLAT_DARCULA`, `FLAT_MAC_DARK`, `FLAT_NORD`, `SILVER_GELATIN`, `WARM_SEPIA`, `SAFELIGHT_RED`) and `PhotoCanvasMode` (`ISO_MID_GRAY`, `PITCH_BLACK`, `FLAT_WHITE`).
- **Regression Test Case**: Open Mobile App $\to$ tap `On` on Safelight Segmented Button $\to$ verify UI converts to Red Safelight $\to$ tap `Off` $\to$ verify UI reverts to standard theme palette.

---

### BUG-103: Clean Transparent Circular Logo & Red Shift Night Vision Mode
- **Platform**: Core, Mobile & Desktop (`Theme.kt`, `SettingsScreen.kt`, `HubScreen.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Green background box appeared around circular logo; top bar title squashed on HubScreen; need for Red Shift night vision mode & neutral alias placeholder.
- **Root Cause**: Un-cropped logo PNG files; wide top bar controls; `e.g. GB` placeholder in Settings.
- **Resolution**:
  1. Processed `logo.png` and `ic_fotologue_logo.png` into clean transparent circular PNGs with `CircleShape` clipping.
  2. Implemented **`Red Shift`** night vision mode with custom deep crimson fills (`#330000`/`#FF0000`/`#000000`) and scientific transparency notice.
  3. Restored `HubScreen.kt` top bar title layout with `Modifier.weight(1f, fill = false)`.
  4. Neutralized Settings placeholder to `Alias / Shortcode (e.g. P1)`.
- **Regression Test Case**: Open Mobile App $\to$ verify `Fotologue` title renders on single line without text squashing and logo is clean circular PNG $\to$ open `Settings` $\to$ verify `Alias / Shortcode (e.g. P1)`.

---

### BUG-104: Reactive Location Permission State & Instant GPS Switch Response
- **Platform**: Fotologue Mobile (`WelcomeScreen.kt`)
- **Reported**: October 2026
- **Symptom**: GPS Location Tagging switch on WelcomeScreen did not move to checked state immediately upon approving location permission until an unrelated click occurred.
- **Root Cause**: Non-reactive local permission check and empty permission launcher callback `{ _ -> }`.
- **Resolution**: Wrapped location permission check in reactive Compose state (`isLocationPermissionGranted`) updated in real-time by the permission launcher callback and `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)`.
- **Regression Test Case**: Clean install Mobile app $\to$ tap GPS Location Tagging switch $\to$ approve permission $\to$ verify switch flips instantly to checked (`ON`).

---

### BUG-105: WCAG 2.1 AA 48dp Minimum Touch Target & TalkBack Semantics Sweep
- **Platform**: Fotologue Mobile (`HubScreen.kt`, `TaxonomyScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Need for strict WCAG 2.1 AA accessibility compliance across touch targets and TalkBack screen reader announcements.
- **Root Cause**: Touch targets under 48dp; missing explicit card semantics.
- **Resolution**:
  1. Enforced minimum 48dp x 48dp touch targets on all Edit and Delete icon buttons in `TaxonomyScreen.kt`.
  2. Added explicit TalkBack accessibility semantics (`contentDescription = "$title module. $subtitle. $description"`) to `HubModuleCard`s in `HubScreen.kt`.
- **Regression Test Case**: Open Mobile App $\to$ inspect `TAXONOMY & TAGS` $\to$ verify all Edit/Delete icon buttons pass 48dp minimum touch target bounds.

---

### BUG-106: Live App Theme Palette Switcher & ISO 3664 Photo Review Canvas
- **Platform**: Core, Mobile & Desktop (`Theme.kt`, `SettingsScreen.kt`, `LogDetailScreen.kt`, `MainActivity.kt`)
- **Reported**: October 2026
- **Symptom**: Need for 1-tap live theme palette switching (FlatLaf & Photographic) and isolated ISO 3664 photo review canvas backgrounds.
- **Root Cause**: Unwired `appTheme` flow and unrepresented photo canvas modes in photo detail views.
- **Resolution**:
  1. Added **App Theme Palette** chips (*System Default*, *Flat Darcula*, *Flat Mac Dark*, *Flat Nord*, *Silver Gelatin*, *Warm Sepia*) to `SettingsScreen.kt` and wired `appTheme` to root `MainActivity.kt` for live 1-tap theme recoloring.
  2. Added **Photo Review Canvas (ISO 3664)** selector (*ISO Mid-Gray #505050*, *Pitch Black #000000*, *Flat White #FFFFFF*) and wrapped photo previews in `LogDetailScreen.kt` inside isolated neutral canvas colors.
  3. Added Red Shift Safety Notice card in Settings.
- **Regression Test Case**: Open Mobile App $\to$ `Settings` $\to$ tap `Flat Darcula` $\to$ verify entire app UI converts to IntelliJ Darcula colors live $\to$ open `Field Log Detail` $\to$ verify photo preview sits on an isolated `#505050` ISO Mid-Gray canvas.

---

### BUG-107: Hub Header Top Bar Un-Squashing & Red Shift Icon Integration
- **Platform**: Fotologue Mobile (`HubScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Top bar title on `HubScreen.kt` was squashed into `Fotolo` / `gue` due to a 220dp wide SegmentedButton in `TopAppBar.actions`.
- **Root Cause**: Excessive horizontal width of SegmentedButton row inside TopAppBar.actions.
- **Resolution**:
  1. Replaced the 220dp wide SegmentedButton in `HubScreen.kt` `TopAppBar.actions` with a compact 48dp Red Shift `IconButton`.
  2. Restored 100% of the top bar title width for `Fotologue` and `A Photographic Catalogue Suite`, completely eliminating text squashing.
- **Regression Test Case**: Open Mobile App $\to$ verify `Fotologue` title renders on single line without text squashing.

---

### BUG-108: Desktop Theme Engine & ISO 3664 Photo Canvas Integration
- **Platform**: Fotologue Desktop (`Main.kt`)
- **Reported**: October 2026
- **Symptom**: Need for live 1-tap theme palette switching and ISO 3664 photo review canvas controls on Desktop.
- **Root Cause**: Unwired `FotologueTheme` at root level in Desktop `Main.kt`.
- **Resolution**:
  1. Wrapped root Desktop `Main.kt` window in `FotologueTheme(photographicTheme = activeTheme)` enabling live 1-tap theme recoloring across the entire Desktop app.
  2. Added **App Theme Palette** chips (*Flat Darcula*, *Flat Mac Dark*, *Flat Nord*, *Silver Gelatin*, *Warm Sepia*, *Red Shift*) and **Photo Review Canvas (ISO 3664)** controls to Desktop Reference Admin.
  3. Wrapped Desktop active frame photo preview panel inside isolated neutral canvas colors (`#505050` ISO Mid-Gray).
- **Regression Test Case**: Open Desktop App $\to$ `Reference & Admin` $\to$ `Theme & Canvas Mode` $\to$ click `Flat Darcula` $\to$ verify entire Desktop window recolors to IntelliJ Darcula `#2B2B2B` live.

---

### BUG-109: Desktop Theme Palette Re-Calibration & System Default Restoration
- **Platform**: Fotologue Desktop (`Main.kt`, `Theme.kt`)
- **Reported**: October 2026
- **Symptom**: `Flat Darcula`, `Flat Mac Dark`, and `Flat Nord` rendered with low-contrast washed-out grey text; `System Default` lost its original rich Material 3 primary color.
- **Root Cause**: Low-contrast `#AFB1B3` / `#A9A9A9` text color definitions and missing M3 default primary values.
- **Resolution**:
  1. Restored `System Default` to original rich Material 3 Dark ColorScheme (`#D0BCFF` primary, `#1C1B1F` background, `#E6E1E5` crisp white text).
  2. Re-calibrated `Flat Darcula` (`#589DF6` IntelliJ blue accent, `#F0F0F0` crisp text), `Flat Mac Dark` (`#3574F0` macOS blue accent, `#F0F2F5` crisp text), and `Flat Nord` (`#88C0D0` cyan accent, `#ECEFF4` Nord snow white text).
- **Regression Test Case**: Open Desktop App $\to$ `Reference & Admin` $\to$ `Theme & Canvas Mode` $\to$ click `System Default` $\to$ verify original rich M3 primary and high-contrast text render with zero wash-out.

---

### BUG-110: Mobile & Desktop Theme Palette Recalibration for Zero-Washout High Contrast
- **Platform**: Core, Mobile & Desktop (`Theme.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: `Warm Sepia` rendered with harsh electric yellow `#E6B800`; `Flat Darcula`, `Flat Mac Dark`, and `Flat Nord` rendered with low-contrast washed-out grey text.
- **Root Cause**: Un-calibrated primary accent and surface variant color definitions.
- **Resolution**:
  1. Recalibrated `Warm Sepia` with gentle warm sepia gold (`#D4A373`) on deep dark espresso (`#191512`) and soft warm ivory text (`#F5E6D3`).
  2. Aligned `Flat Darcula` (`#4B6EAF` Darcula blue accent, `#2B2B2B` background, `#A9B7C6` text), `Flat Mac Dark` (`#3574F0` macOS blue, `#1E1F22` background, `#DFE1E5` text), and `Flat Nord` (`#88C0D0` Frost cyan, `#2E3440` Polar Night background, `#ECEFF4` Nord snow white text).
  3. Added `Daylight Clean (Light)` theme palette with crisp white background (`#FFFFFF`) and high-contrast pitch black text (`#1C1B1F`).
- **Regression Test Case**: Open Desktop App $\to$ `Reference & Admin` $\to$ `Theme & Canvas Mode` $\to$ click `Warm Sepia` $\to$ verify gentle warm sepia gold `#D4A373` accent renders on deep dark espresso background with zero harshness.

---

### BUG-111: FlowRow Theme Multi-Line Wrapping & Streamlined Photographic Suite
- **Platform**: Core, Mobile & Desktop (`Theme.kt`, `SettingsScreen.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Theme picker row suffered right-edge overflow on constrained window widths; generic Flat themes created palette clutter.
- **Root Cause**: Rigid single-line `Row` wrapper and unneeded Flat theme definitions.
- **Resolution**:
  1. Replaced `Row` with `FlowRow` in `SettingsScreen.kt` and Desktop `Main.kt` for automatic multi-line chip wrapping.
  2. Streamlined `PhotographicTheme` enum to 5 core photographic palettes (`SYSTEM_DEFAULT`, `DAYLIGHT_LIGHT`, `SILVER_GELATIN`, `WARM_SEPIA`, `SAFELIGHT_RED`).
- **Regression Test Case**: Open Desktop App $\to$ `Reference & Admin` $\to$ `Theme & Canvas Mode` $\to$ verify theme chips wrap cleanly across multiple lines with **ZERO OVERFLOW OR TEXT SQUASHING**.

---

## 📋 Comprehensive Regression Test Checklist

### BUG-113: Option 2 LAB Wet Darkroom Session Engine Integration
- **Platform**: Fotologue Mobile (`feature:lab` / `FilmProcessingSessionScreen.kt`, `EnlargerPrintingSessionScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Finalization of Option 2 LAB Session Engine (film development agitation timer and enlarger print recipe logger).
- **Root Cause**: Need for integrated darkroom timer guidance, agitation alerts, and print recipe outcome tracking.
- **Resolution**:
  1. Finalized `FilmProcessingSessionScreen.kt` with multi-stage development wizard, agitation countdown alerts (`AGITATE NOW: 10s`), and auto-recommended development times.
  2. Finalized `EnlargerPrintingSessionScreen.kt` with paper stock tracking, grade filters (0–5), test strip steps, dodge/burn map, and KEEPER vs REJECT print outcome logging.
  3. Integrated 100% zero-white red shift mode and `DarkroomLightGuard` sensor monitoring during active sessions.
- **Regression Test Case**: Open Mobile App $\to$ `LAB` $\to$ start **Film Processing Session** $\to$ verify agitation timer countdown renders in strict zero-white red shift mode.

---

### BUG-114: Option 3 Gear Custody, Loan & Repair Ledger Finalization
- **Platform**: Fotologue Mobile (`feature:lab` / `CustodyLedgerScreen.kt`, `PreArrivalOrdersScreen.kt`, `StorageLocationsScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Finalization of Option 3 Gear Custody, Loan & Repair Ledger.
- **Root Cause**: Need for 3-Pillar Asset+Contact+Location custody tracking, pre-arrival orders, and WCAG 2.1 AA 48dp touch targets.
- **Resolution**:
  1. Finalized `CustodyLedgerScreen.kt` with native phone contact picker for borrower selection, active vs returned tracking, and assigned storage locations.
  2. Finalized `PreArrivalOrdersScreen.kt` with pre-arrival order tracking (*Waitlisted*, *In Transit*) and 1-tap `[ Mark Delivered ]` storage location assignment.
  3. Enforced minimum 48dp x 48dp touch targets on all edit, delete, and delivery buttons.
- **Regression Test Case**: Open Mobile App $\to$ `LAB` $\to$ `Custody Ledger` $\to$ log a Service / Repair ticket $\to$ verify active custody record renders with expected return date.

---

### BUG-115: Option 4 1-Tap Cloud Sync & XMP Desktop Sidecar Dispatcher Validation
- **Platform**: Core, Mobile & Desktop (`LogEntryXmpExporter.kt`, `LogEntryZipExporter.kt`, `ArchiveScreen.kt`, `Main.kt`)
- **Reported**: October 2026
- **Symptom**: Finalization of Option 4 1-Tap Cloud Sync & XMP Desktop Sidecar Dispatcher.
- **Root Cause**: Need for Adobe & digiKam XMP Core 5.6 compliant metadata sidecar exports with EXIF, IPTC, and 3-axis taxonomy paths.
- **Resolution**:
  1. Verified `LogEntryXmpExporter.kt` generates Adobe XMP Core 5.6 compliant `.xmp` sidecars with EXIF exposure (`exif:FNumber`, `exif:ExposureTime`, `exif:ISOSpeedRatings`), GPS coordinates, IPTC creator/copyright, and Lightroom/digiKam 3-level taxonomy paths (`lr:hierarchicalSubject`).
  2. Verified `LogEntryZipExporter.kt` packages XMP sidecars, metadata JSON, and digital contact sheets into a single 1-tap backup package for cloud syncing and Desktop DAM ingestion.
- **Regression Test Case**: Open Mobile App $\to$ `ARCHIVE` $\to$ tap `Export XMP Sidecars` $\to$ verify generated `.xmp` file contains valid XML with EXIF exposure, GPS coordinates, and 3-axis taxonomy paths.

---

### BUG-116: Option 1 Zone System Viewfinder & Spot Metering Engine Integration
- **Platform**: Fotologue Mobile (`app` / `ZoneSystemCalculator.kt`, `LightMeterViewModel.kt`, `LightMeterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Finalization of Option 1 Zone System Viewfinder & Spot Metering Engine.
- **Root Cause**: Need for Ansel Adams 10-Zone System spot metering, dynamic range contrast calculator, and N±1 push/pull recommendations.
- **Resolution**:
  1. Implemented `ZoneSystemCalculator.kt` with Ansel Adams Zone mapping ($\text{Zone} = 5.0 + \Delta EV$), scene dynamic range calculation, and $N \pm 1$ development recommendation logic.
  2. Updated `LightMeterViewModel.kt` and `LightMeterScreen.kt` to display live Ansel Adams Zone badges on spot tap, scene contrast range bar (in stops), and $N \pm 1$ push/pull development recommendation cards.
- **Regression Test Case**: Open Mobile App $\to$ `FIELD` $\to$ `Measure Exposure` $\to$ tap a spot on viewfinder $\to$ verify Ansel Adams Zone badge and scene contrast range display live.

---

### BUG-117: Intent Parent Group Dropdown, Field Tags Action & Prominent FAB Styling
- **Platform**: Fotologue Mobile (`TaxonomyScreen.kt`, `TimelineScreen.kt`, `LightMeterScreen.kt`, `AddLogScreen.kt`, `GearLibraryScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Intent edit dialog lacked parent category dropdown and mislabeled field as `Content Name`; Field top-bar Tags icon was unhandled; Light Meter calibration icon used generic tools; FABs had low visibility.
- **Root Cause**: Unhandled navigation lambda; default M3 FAB surface colors.
- **Resolution**:
  1. Added `Parent Intent Group` dropdown selector to `TaxonomyScreen.kt` edit dialog and updated field label to `Intent Name`.
  2. Wired `onTaxonomyClick` in `TimelineScreen.kt` to open `TaxonomyScreen`.
  3. Created `brightness_auto_24px.xml` M3 vector icon and updated Light Meter calibration top-bar icon.
  4. Upgraded FABs across all screens to high-visibility primary container fills (`containerColor = primary`, `contentColor = onPrimary`) and elevated 8dp shadow.
- **Regression Test Case**: Open Mobile App $\to$ `FIELD` $\to$ tap top-right **Tags** icon $\to$ verify `Taxonomy & Tags` opens $\to$ edit an Intent tag $\to$ verify `Parent Intent Group` dropdown renders.

---

### BUG-118: Desktop Consumables Form Preset Button Target Fix
- **Platform**: Fotologue Desktop (`Main.kt`)
- **Reported**: October 2026
- **Symptom**: Clicking "Select from Presets" in Consumables form opened Lab Equipment presets modal.
- **Root Cause**: Misdirected button click handler target calling `showEquipmentPresetsDialog = true`.
- **Resolution**:
  1. Implemented dedicated `ConsumablePresetsDialog` (`showConsumablePresetsDialog`) in `Main.kt` searching consumable presets (*Ilford Rapid Fixer*, *Kodak D-76*, *Adox Rodinal*, *Ilford Multigrade RC Paper*).
  2. Updated Consumables form preset button to open `showConsumablePresetsDialog`.
  3. Audited all 14 Reference Admin form targets across Desktop.
- **Regression Test Case**: Open Desktop App $\to$ `Reference & Admin` $\to$ `Consumables` $\to$ click `[ Select from Presets ]` $\to$ verify `Select Consumable Chemistry from Presets` modal opens cleanly.

---

### BUG-119: Milestone A Real-Time Viewfinder Histogram & Exposure Clipping Zebra Overlays Integration
- **Platform**: Fotologue Mobile (`app` / `LuminanceAnalyzer.kt`, `LightMeterViewModel.kt`, `LightMeterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Need for live 11-Zone histogram visualization and highlight/shadow clipping zebra overlays on live camera meter feed.
- **Root Cause**: Unrepresented histogram array and clipping state in LightMeterUiState.
- **Resolution**:
  1. Updated `LuminanceAnalyzer.kt` to compute 256-bin luminance histogram, 11-Zone bin mapping array, and highlight/shadow clipping flags.
  2. Updated `LightMeterViewModel.kt` and `LightMeterScreen.kt` to render a live 11-Zone histogram overlay box and animated red/blue zebra clipping stripes over live camera preview.
- **Regression Test Case**: Open Mobile App $\to$ `FIELD` $\to$ `Measure Exposure` $\to$ point camera at bright light $\to$ verify red zebra clipping stripes and Zone X histogram spike render live.

---

### BUG-120: Documentation Alignment with Modular Application Architecture
- **Platform**: Core Documentation (`README.md`, `docs/user_guide.md`)
- **Reported**: October 2026
- **Symptom**: Legacy documentation described a rigid linear "4-Stage Division of Labour", which no longer held true for the app's modular architecture.
- **Root Cause**: Un-updated documentation files predating the Staging Portal module architecture.
- **Resolution**:
  1. Overhauled `README.md` and `docs/user_guide.md` to document the **Modular Operational Architecture**: 7 Mobile Staging Portal Modules (`CAMERA GEAR`, `TAXONOMY & TAGS`, `FIELD`, `CONTACTS DIRECTORY`, `LAB`, `STATION`, `ARCHIVE`) and 4 Desktop Command Center Stages (`1. Ingestion`, `2. Master QA`, `3. Image Tagging`, `4. Reference Data Administration`).
  2. Documented all newly implemented capabilities: Zone System Viewfinder spot metering, 11-Zone real-time histogram, red/blue zebra clipping overlays, wet darkroom agitation countdown timer, enlarger print recipe logger, 3-pillar custody ledger, live theme switcher suite, and ISO 3664 neutral photo review canvas.
- **Regression Test Case**: Review `README.md` $\to$ verify all 7 Mobile Staging Portal Modules and 4 Desktop Command Center Stages are accurately documented.

---

### BUG-121: Un-Squashed Exposure Selection & Auto-Scroll Sweet Spot
- **Platform**: Fotologue Mobile (`app` / `LightMeterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: LightMeterScreen ran out of vertical space on mobile screens, squashing exposure setting selection cards (`f/stop Shutter [Use]`) at the bottom.
- **Root Cause**: Excessive vertical height consumed by 3-line Zone Card and standalone 60dp Focal Length row.
- **Resolution**:
  1. De-cluttered tool header into a single 1-line horizontal row with compact Zone Badge (`🎯 Zone V`).
  2. Hidden focal length adjustment controls for prime/fixed lenses (`isZoomLens == false`), rendering compact inline controls only for Zoom lenses.
  3. Implemented `LazyListState` initial auto-scroll to the recommended exposure "sweet spot" index (bypassing unusable overexposures at the top).
  4. Allocated 100% of reclaimed bottom panel height to Exposure Selection cards.
- **Regression Test Case**: Open Mobile App $\to$ `FIELD` $\to$ `Measure Exposure` $\to$ verify exposure selection cards render as unobstructed primary centerpiece with initial auto-scroll to recommended sweet spot.

---

### BUG-122: Sensor Calibration Wrench Icon & Exposure List Vertical Scrollbar Integration
- **Platform**: Fotologue Mobile (`app` / `mobile_wrench_24px.xml`, `LightMeterScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Sensor calibration wizard icon was ambiguous; exposure settings list lacked a visual scrollbar making scrollable alternatives non-obvious.
- **Root Cause**: Ambiguous top-bar icon resource; missing LazyColumn scrollbar decorator.
- **Resolution**:
  1. Created `mobile_wrench_24px.xml` M3 vector icon and updated Light Meter top bar calibration button.
  2. Implemented a custom high-visibility vertical scrollbar canvas indicator on the right edge of the exposure settings `LazyColumn` in `LightMeterScreen.kt`.
- **Regression Test Case**: Open Mobile App $\to$ `FIELD` $\to$ `Measure Exposure` $\to$ verify top-bar calibration button displays clear Wrench icon and exposure settings list renders a visible vertical scrollbar.

---

### BUG-123: Exact Material Symbols Mobile Wrench Vector Icon
- **Platform**: Fotologue Mobile (`app` / `mobile_wrench_24px.xml`)
- **Reported**: October 2026
- **Symptom**: Top-bar sensor calibration icon did not match the user-supplied Material Symbols Mobile Wrench specification.
- **Root Cause**: Standalone wrench vector path instead of phone frame + wrench overlay composite vector path.
- **Resolution**: Updated `mobile_wrench_24px.xml` to match the exact Material Symbols Mobile Wrench specification (rounded smartphone frame with top camera dot and bottom-right wrench overlay).
- **Regression Test Case**: Open Mobile App $\to$ `FIELD` $\to$ `Measure Exposure` $\to$ verify top-bar calibration button displays the exact Mobile Wrench icon.

---

### BUG-124: Native Windows Desktop Distribution Toolchain Setup
- **Platform**: Fotologue Desktop (`desktop/build.gradle.kts`)
- **Reported**: October 2026
- **Symptom**: Executing `./gradlew :desktop:packageMsi` failed with `jpackage.exe is missing`.
- **Root Cause**: Android Studio embedded JBR lacks jpackage.exe installer tool.
- **Resolution**: Configured `compose.desktop.application.javaHome` in `desktop/build.gradle.kts` targeting JDK 21 runtime with jpackage.exe, and added convenience `:desktop:packageWindows` Gradle task generating standalone Windows `.msi` and `.exe` installers.
- **Regression Test Case**: Run `./gradlew :desktop:packageWindows` $\to$ verify build finishes successfully and generates `FotologueDesktop-1.0.0.msi` (122.5MB) and `FotologueDesktop-1.0.0.exe` (123.2MB).

---

### BUG-125: Java 21 Bytecode Target Alignment for Packaged Desktop Application
- **Platform**: Fotologue Desktop (`desktop/build.gradle.kts`)
- **Reported**: October 2026
- **Symptom**: `FotologueDesktop.exe` exited silently on startup with `UnsupportedClassVersionError: class file version 69.0` (Java 25).
- **Root Cause**: Kotlin compiler targeted Java 25 while jpackage bundled Java 21 JRE runtime.
- **Resolution**:
  1. Configured `kotlin { jvmToolchain(21) }` in `desktop/build.gradle.kts` to target Java 21 bytecode (class file version 65.0).
  2. Enabled `windows { upgradeUuid = "a2d1847e-8c3b-4b10-a7d1-9a101038dd80" }` for seamless in-place Windows installer upgrades.
- **Regression Test Case**: Install `FotologueDesktop-1.0.1.msi` $\to$ launch `C:\Program Files\FotologueDesktop\FotologueDesktop.exe` $\to$ verify application launches and remains running cleanly.

---

### BUG-126: Android Release Version Bump to v1.0.18 (versionCode 19)
- **Platform**: Fotologue Mobile (`app/build.gradle.kts`)
- **Reported**: October 2026
- **Symptom**: Preparing Android release update for Google Play Store submission (version 18 / 1.0.17 currently in production on Play Console).
- **Root Cause**: Need to increment versionCode and versionName for Play Console submission.
- **Resolution**:
  1. Updated `versionCode = 19` and `versionName = "1.0.18"` in `app/build.gradle.kts`.
  2. Compiled release App Bundle (`app-release.aab`, 27.0MB) and release APK (`app-release.apk`, 42.1MB) via `./gradlew :app:bundleRelease :app:assembleRelease`.
- **Regression Test Case**: Verify `app/build/outputs/bundle/release/app-release.aab` exists and contains version code 19.

---

### BUG-127: Official GitHub Privacy Policy URL Domain Migration
- **Platform**: Core Documentation & Mobile App (`PRIVACY_POLICY.md`, `docs/privacy_policy.md`, `SettingsScreen.kt`)
- **Reported**: October 2026
- **Symptom**: Privacy policy and settings screen referenced legacy website domain (`fotologue.au`).
- **Root Cause**: Un-migrated placeholder web domain strings.
- **Resolution**:
  1. Updated `PRIVACY_POLICY.md` and `docs/privacy_policy.md` to reference the official GitHub Privacy Policy URL (`https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md`).
  2. Updated `SettingsScreen.kt` privacy policy click handler to launch `https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md` via `UriHandler`.
- **Regression Test Case**: Open Mobile App $\to$ `Settings` $\to$ tap `Privacy Policy` $\to$ verify official GitHub Privacy Policy URL opens cleanly.

---

### BUG-128: Android 15 (SDK 35) Edge-to-Edge System Bar Appearance Compliance
- **Platform**: Fotologue Mobile (`core/designsystem` / `Theme.kt`, `MainActivity.kt`)
- **Reported**: October 2026
- **Symptom**: Google Play Console warning regarding deprecated edge-to-edge system bar parameters on Android 15 (SDK 35).
- **Root Cause**: Un-managed status/navigation bar appearance flags when switching between light and dark photographic themes.
- **Resolution**:
  1. Integrated `WindowCompat.getInsetsController` `SideEffect` in `Theme.kt` to dynamically update `isAppearanceLightStatusBars` and `isAppearanceLightNavigationBars` based on active photographic theme (`Daylight Clean` vs `Dark/Red Shift`).
  2. Maintained `enableEdgeToEdge()` call in `MainActivity.kt` before `setContent` and set `isNavigationBarContrastEnforced = false` for SDK 29+.
- **Regression Test Case**: Open Mobile App $\to$ `Settings` $\to$ toggle between `Daylight Clean (Light)` and `Red Shift` $\to$ verify system status and navigation bar icon colors switch dynamically between dark and light icons.

---

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
