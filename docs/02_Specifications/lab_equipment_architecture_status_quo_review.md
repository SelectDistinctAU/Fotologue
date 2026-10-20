# Lab Equipment Architecture: Status Quo & Comprehensive Review

This document provides a detailed architectural audit of how **Lab Equipment** (Enlargers, Enlarger Lenses, Dev Tanks, Trays, Scanners, Timers) is handled across **Fotologue Mobile** and **Fotologue Desktop**, identifying workflow friction points and detailing recommendations for a unified, intuitive user experience.

---

## Part 1: Mobile Status Quo & Architectural Audit

### 1. Current Mobile Data Model (`core:data` / Room)
On Mobile, equipment is represented by the `LabEquipment` entity:
```kotlin
@Entity(tableName = "lab_equipment")
data class LabEquipment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Enlarger, Enlarger Lens, Dev Tank, Tray, Light Table, etc.
    val notes: String = ""
)
```

> [!WARNING]
> **Schema Deficit on Mobile**: The Mobile Room entity currently lacks explicit fields for `manufacturer`, `modelNumber`, `serialNumber`, `condition`, and `parentEquipmentId`. All specs are crammed into `name` or `notes`.

### 2. Mobile User Workflow (`AddEquipmentScreen.kt`)
Mobile currently uses a **2-Tab Screen**:
- **Tab 0: `Pick List Catalog`**: Displays category filter chips (*All*, *Enlarger*, *Enlarger Lens*, *Dev Tank*, etc.), a search bar, and 17+ presets (*Intrepid Compact Enlarger*, *Beseler 23C II*, *Rodenstock 50mm*).
  - *Interaction*: Tapping a preset directly inserts it into the database and exits.
- **Tab 1: `Custom Equipment`**: Manual text fields for `customName`, `customCategory`, and `customNotes`.
  - *Interaction*: Tapping "Save Equipment" inserts the custom item.

### 3. Mobile Display & Management (`LabHomeScreen.kt`)
Equipment is displayed in a vertical list flatly grouped by uppercase category headers:
- `ENLARGER LENS`
  - *El-Nikkor 50mm f/2.8 N* `[ ✏️ ]` `[ 🗑️ ]`
  - *Rodenstock Rodagon 80mm f/4* `[ ✏️ ]` `[ 🗑️ ]`
- `ENLARGER`
  - *Intrepid Compact Enlarger (35mm / 120)* `[ ✏️ ]` `[ 🗑️ ]`

### 4. Mobile Friction Points & Gaps
1. **Disjointed Creation Experience**: If a user wants to select a preset (*Intrepid Enlarger*) and then customize its brand, model, or serial number, the 2-tab split forces them away from the preset list to the custom tab.
2. **Missing Parent-Child Sub-Equipment Visualization**: An Enlarger Lens (*Rodenstock 80mm*) or Color Head is displayed as an unlinked item in the `ENLARGER LENS` section rather than visually nested under the *Intrepid Compact Enlarger* body.
3. **Editing Inconsistency**: Tapping `[ ✏️ ]` on an existing item opens the `Custom Equipment` tab pre-filled, but offers no indication of attached sub-equipment or parent links.

---

## Part 2: Desktop Status Quo & Architectural Audit

### 1. Current Desktop Data Model (`Database.kt` / Exposed SQLite)
On Desktop, equipment is represented by `FoundationLabEquipmentTable` and `ReferenceLabEquipmentItem`:
```kotlin
object FoundationLabEquipmentTable : IntIdTable("foundation_lab_equipment") {
    val name = varchar("name", 255).uniqueIndex()
    val manufacturer = varchar("manufacturer", 255).nullable()
    val modelNumber = varchar("model_number", 255).nullable()
    val serialNumber = varchar("serial_number", 255).nullable()
    val category = varchar("category", 100).default("Enlarger")
    val condition = varchar("condition", 50).default("Good")
    val parentEquipmentName = varchar("parent_equipment_name", 255).nullable()
    val notes = text("notes").nullable()
}
```

### 2. Desktop User Workflow (`Main.kt`)
Desktop uses a **Single Modal Dialog** (`Add Lab Equipment` / `Edit Lab Equipment`):
- **Top Button**: **`[ ⚙️ Select from Presets ]`** opens a category-filtered preset picker dialog (*All*, *Enlarger*, *Enlarger Lens*, *Dev Tank*, etc.).
  - *Interaction*: Selecting a preset populates all fields in the background form for immediate editing.
- **Form Fields Layout**:
  - **Row 1**: `Equipment Name` & `Manufacturer / Brand`
  - **Row 2**: `Category` & `Parent Equipment` (Relational dropdown listing primary enlarger bodies)
  - **Row 3**: `Condition` & `Model Number`
  - **Row 4**: `Serial Number`

### 3. Desktop Display & Management (`Main.kt`)
Equipment is displayed as cards inside the Stage 4 Administration Data Grid under `Equipment (x)`.

### 4. Desktop Friction Points & Gaps
1. **Schema Mismatch with Mobile**: Desktop supports `manufacturer`, `modelNumber`, `serialNumber`, `condition`, and `parentEquipmentName`, but Mobile cannot store these fields natively without a Room migration!
2. **Preset Modal vs Page Transition**: Desktop uses a popup modal for presets, whereas Mobile uses a full screen tab.

---

## Part 3: Comparative Friction & Gap Matrix

| Aspect | Fotologue Mobile | Fotologue Desktop | Target Unified Solution |
| :--- | :--- | :--- | :--- |
| **Schema Depth** | Minimal (`name`, `category`, `notes`) | Rich (`name`, `manufacturer`, `modelNumber`, `serialNumber`, `condition`, `parentEquipmentName`) | **Migrate Mobile Room DB** to store `manufacturer`, `modelNumber`, `serialNumber`, `condition`, and `parentEquipmentId`. |
| **Creation Form** | 2-Tab Split (`Pick List` vs `Custom`) | Single Form with Top Preset Button | **Unified Single Form** on both platforms with a top **`[ ⚙️ Select from Presets ]`** button. |
| **Sub-Equipment Linkage** | ❌ Unlinked flat lists | Relational `Parent Equipment` dropdown | **Parent-Child Linkage** (e.g. Lens linked to Enlarger) stored natively. |
| **Visual Hierarchy** | Flat category headers | Flat card list | **Nested Parent-Child Card View** (e.g. Enlarger Body card with nested sub-equipment chips underneath). |

---

## Part 4: Recommendations for Complete Parity & Intuitive Experience

### 1. Upgrade Mobile `LabEquipment` Room Schema
Add missing columns to Mobile's Room database via Migration:
- `manufacturer: String?`
- `modelNumber: String?`
- `serialNumber: String?`
- `condition: String` (default `"Good"`)
- `parentEquipmentId: Long?`

### 2. Replace Mobile's 2-Tab Split with a Unified Form
Collapse Mobile's `Pick List Catalog` vs `Custom Equipment` tabs into a single, seamless **`Add Lab Equipment`** screen matching Desktop:
- Top button: **`[ ⚙️ Select from Presets ]`** (opens category-filtered preset picker).
- Inlined fields: `Equipment Name`, `Manufacturer`, `Category`, `Parent Equipment`, `Condition`, `Model Number`, `Serial Number`, `Notes`.

### 3. Implement Visual Sub-Equipment Grouping on Both Platforms
When displaying equipment on `LabHomeScreen` (Mobile) and Stage 4 Data Grid (Desktop):
- Primary equipment (*Intrepid Compact Enlarger*) renders as a master card.
- Sub-equipment (*Rodenstock 80mm Lens*, *35mm Carrier*, *Dichro Head*) renders as **nested child chips/cards** directly inside or under the parent enlarger card!

---

*Document created: March 2026 for Fotologue v3.0 Architecture Alignment.*
