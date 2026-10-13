# Desktop Consumables Preset Modal & Target Auditing Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 06:00 AM UTC

---

This plan fixes the misdirected preset modal on Desktop where clicking "Select from Presets" under **Consumables** accidentally opened **Lab Equipment**, by implementing a dedicated `ConsumablePresetsDialog` (`showConsumablePresetsDialog`) and auditing all 14 category targets in `Main.kt`.

---

## 1. Technical Fix Specifications

### A. Consumable Presets Modal Implementation (`Main.kt`)
- **Symptom**: Clicking "Select from Presets" under Consumables (Category 9) in Desktop Reference Admin opened `Select Lab Equipment from Presets`.
- **Root Cause**: Line 3936 in `Main.kt` called `showEquipmentPresetsDialog = true` inside the Consumables form block.
- **Fix**:
  1. Add `showConsumablePresetsDialog`, `selectedConsumableCategoryFilter`, and `consumableSearchQuery` to `FoundationAdminFormState`.
  2. Implement `ConsumablePresetsDialog` searching `DatabaseRepository.searchPresetConsumables(query)` with category filters (*Chemistry*, *Paper*, *Film Stock*, *Safety Gear*, *Supplies*).
  3. Wire Consumable form button to `showConsumablePresetsDialog = true`.

### B. Audit of All 14 Category Targets (`Main.kt`)
- Audited category routing across all 14 Reference Admin forms:

| Category Index | Category Name | Preset Action Target | Status |
| :--- | :--- | :--- | :--- |
| `0` | Cameras | `showCameraPresetsDialog` | ✅ Correct |
| `1` | Lenses | `showLensPresetsDialog` | ✅ Correct |
| `2` | Films | `showFilmPresetsDialog` | ✅ Correct |
| `3` | Kit Bags | Camera / Lens DB Selectors | ✅ Correct |
| `4` | Teleconverters | Preset Dropdown | ✅ Correct |
| `5` | Filters | Preset Dropdown | ✅ Correct |
| `6` | Flashes | Preset Dropdown | ✅ Correct |
| `7` | Viewfinders | Preset Dropdown | ✅ Correct |
| `8` | Contacts Directory | `isSupplierPresetDropdownExpanded` | ✅ Correct |
| `9` | Consumables | `showConsumablePresetsDialog` | 🔧 FIXED |
| `10` | Lab Equipment | `showEquipmentPresetsDialog` | ✅ Correct |
| `11` | Storage Locations | Storage Location Form | ✅ Correct |
| `12` | Pre-Arrival Orders | Order History Form | ✅ Correct |
| `13` | Custody Ledger | Custody Form | ✅ Correct |

---

## 2. Proposed Code Changes

- **[MODIFY]** `desktop/src/main/kotlin/com/fotologue/desktop/Main.kt`
  - Implement `showConsumablePresetsDialog` and `ConsumablePresetsDialog` composable.
  - Wire Consumable form preset button to `showConsumablePresetsDialog = true`.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Desktop App `./gradlew :desktop:run`.
3. Open `Reference & Admin` $\to$ click **`Consumables`** in the left rail $\to$ click **`[ + Add New ]`** or **`[ Select from Presets ]`**.
4. Verify the modal opens as **`Select Consumable Chemistry from Presets`** displaying consumable presets (*Ilford Rapid Fixer*, *Kodak D-76*, *Adox Rodinal*, *Ilford Multigrade RC Paper*)!
5. Click **Select** on a consumable preset $\to$ verify it pre-fills into the Consumable form cleanly!
