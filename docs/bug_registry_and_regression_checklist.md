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
