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
