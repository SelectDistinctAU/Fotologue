# Fotologue Lab: Data Model, Processing Pathways & Custodianship Whitepaper 🔬🎞️📄

**Version:** 1.0.17 (Build 18) | **Last Updated:** September 2026 | **Audience:** Product Strategy, Darkroom Photographers & Core Engineering Team

---

> [!IMPORTANT]
> **Core Mission of Fotologue Lab**:
> Once a roll of film leaves the camera body, **Fotologue Lab** takes over to track the physical negative, darkroom print, and digital asset through every stage of its lifecycle—from chemical development, to scanning, printing, framing, exhibition, and final sale or disposition.

---

## Executive Summary

Fotologue Lab provides a complete, local-first management system for home darkroom processing, commercial lab tracking, silver-gelatin darkroom printing, and fine-art print edition custodianship.

---

## 1. The 5 Processing & Custodianship Pathways

Fotologue Lab models 5 distinct pathways that a physical roll or negative can take after exposure:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       FOTOLOGUE LAB PROCESSING PATHWAYS                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ PATH 1: Commercial Lab Processing                                           │
│  Camera ──► Storage ──► Commercial Lab ──► Film Returned ──► Sleeve ──► Library│
│                                                                             │
│ PATH 2: Self-Processing + Digitisation                                      │
│  Camera ──► Storage ──► Home Dev ──► Macro Scan ──► Film Sleeve ──► Library │
│               └─► [Digitised Retrieval] ──► Printed ──► Library             │
│                                                                             │
│ PATH 3: Full Self-Processing + Darkroom Printing                            │
│  Camera ──► Home Dev ──► Macro Scan ──► Darkroom Print ──► Archive ──► Library│
│                                                                             │
│ PATH 4: Retrieval from Storage & Final Disposition / Custodianship          │
│  4.1: Film/Print ──► Printed ──► Sold                                       │
│  4.2: Film/Print ──► Printed ──► On Display (Gallery / Exhibition)          │
│  4.3: Film/Print ──► Sold (Original Negative or Vintage Print)              │
│  4.4: Film/Print ──► Disposed of / Lost / Destroyed                         │
│  4.5: Film/Print ──► On Display (Original Negative on display - Risky!)     │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. The 3 Core Components of Fotologue Lab

Fotologue Lab is structured into 3 integrated modules:

### 1. Film Processing Module
* **Commercial Lab Orders**: Track lab name (*Rewind Photo Lab*), order submission date, tracking numbers, and lab push/pull instructions.
* **Home Chemistry Processing**:
  * Step-by-step dev process (Pre-wet, Developer, Stop Bath, Fixer, Hypo Clear, Final Wash, Wetting Agent).
  * Real-time timer with temperature compensation (*e.g., D-76 at 20°C vs 22°C*).
  * Agitation schedule alerts (*Initial 30s continuous, 10s per minute*).

### 2. Digitising & Scan Matching Module
* Camera macro copy presets (`_DSF1024.xmp`) and flatbed scanner alignment.
* Contact sheet generation and in-grid frame gap adjustment (+1 / -1 offset).

### 3. Darkroom Printing Module ("Print Recipes")
* Enlarger height, aperture (*Rodagon 80mm @ f/11*), paper stock (*Ilford FB Glossy 11x14"*).
* Split-grade exposure seconds (Grade 00 + Grade 5).
* Interactive Dodge & Burn diagram map.

---

## 3. Darkroom UI & Safelight Requirements

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         RED SAFELIGHT MODE UI                               │
├─────────────────────────────────────────────────────────────────────────────┤
│ • Pure Deep Red (#FF0000) text and icons on Pure Black (#000000) background  │
│ • Zero blue/green subpixel emission to prevent darkroom paper fogging       │
│ • User Confirmation Modal: "Verify OLED screen red output before opening"   │
│ • Oversized Touch Targets (≥ 64 dp) for darkroom gloves & dim lighting       │
└─────────────────────────────────────────────────────────────────────────────┘
```

1. **Red Safelight Mode (`#FF0000` on `#000000`)**:
   * Uses pure red OLED pixels to prevent paper fogging during black-and-white printing.
   * Prompts user acceptance modal: *"Please verify your device's screen renders pure red without light bleed before exposing sensitive photographic paper."*
2. **Oversized Field & Darkroom Inputs**:
   * Large, high-contrast touch targets ($\ge 64 \text{ dp}$) and giant timer digits for easy reading across a darkroom sink or enlarger station.

---

## 4. Fotologue Lab Data Model

Fotologue Lab extends the Room database with 5 core data entities:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          FOTOLOGUE LAB DATA MODEL                           │
├─────────────────────────────────────────────────────────────────────────────┤
│ 1. EQUIPMENT    │ Enlargers, Enlarger Lenses, Dev Tanks, Trays, Timers      │
│ 2. CHEMICALS    │ Film/Paper Developers, Fixers, Stop Bath, Capacity Counts │
│ 3. ACTIONS      │ Temp (°C), Duration, Agitation Ratios, Split-Grade Times │
│ 4. STORAGE      │ Archival Binders, Negative Sleeves, Print Boxes, Shelves │
│ 5. OUTCOME      │ Editions (01/10), Gallery Exhibition, Sales, Disposition  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1. Equipment (`LabEquipment`)
```kotlin
@Entity(tableName = "lab_equipment")
data class LabEquipment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. "Beseler 23C II Enlarger"
    val category: String, // Enlarger, Enlarger Lens, Dev Tank, Tray, Light Table
    val notes: String = ""
)
```

### 2. Chemicals & Solutions (`LabChemical`)
```kotlin
@Entity(tableName = "lab_chemicals")
data class LabChemical(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. "Kodak D-76" or "Ilford Rapid Fixer"
    val type: String, // Film Developer, Paper Developer, Stop Bath, Fixer
    val dilution: String = "1+1", // 1+1, 1+3, 1+25, Stock
    val mixDate: Long = System.currentTimeMillis(),
    val capacityRollsTotal: Int = 12,
    val capacityRollsUsed: Int = 0,
    val isExhausted: Boolean = false
)
```

### 3. Processing Event / Darkroom Recipe (`LabProcessEvent` & `PrintRecipe`)
```kotlin
@Entity(tableName = "lab_process_events")
data class LabProcessEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filmRollId: String,
    val developerName: String, // e.g. "D-76 (1+1)"
    val temperatureCelsius: Float = 20.0f,
    val durationSeconds: Int = 540, // 9 mins
    val agitationSchedule: String = "Initial 30s continuous, 10s/min",
    val pushPullStops: Float = 0.0f
)

@Entity(tableName = "print_recipes")
data class PrintRecipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val logEntryId: Long,
    val paperName: String, // e.g. "Ilford Multigrade FB Glossy"
    val paperSize: String = "11x14",
    val enlargerLens: String = "Rodagon 80mm f/4 @ f/11",
    val baseExposureSeconds: Float = 14.5f,
    val gradeFilter: String = "Grade 2.5",
    val dodgeBurnMapUri: String? = null // Hand-drawn dodge/burn diagram
)
```

### 4. Physical Storage (`LabStorageLocation`)
```kotlin
@Entity(tableName = "lab_storage_locations")
data class LabStorageLocation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. "Binder A - 2026 Urban Series"
    val sleeveNumber: String = "Sleeve #12",
    val locationType: String // Negative Sleeve, Print Box, Framing Case, Safe
)
```

### 5. Outcome & Custodianship (`PrintOutcome` & `CustodianshipRecord`)
```kotlin
@Entity(tableName = "print_outcomes")
data class PrintOutcome(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val printRecipeId: Long,
    val editionNumber: String, // "Edition 02/10" or "Artist Proof AP1"
    val status: String, // ARCHIVED, DISPLAY_EXHIBITION, SOLD, DISPOSED
    val exhibitionName: String? = null, // e.g. "Sydney Fine Art Gallery 2026"
    val buyerName: String? = null,
    val salePrice: String? = null,
    val saleDate: Long? = null,
    val notes: String = ""
)
```

---

## 5. Summary Roadmap for v3.0 Implementation

1. **Phase 1 (Chemistry & Film Dev)**: Home film processing timer, chemistry capacity counters, and commercial lab order tracking.
2. **Phase 2 (Darkroom Printing & Red Safelight UI)**: Pure red OLED darkroom theme, enlargement exposure timers, and split-grade print recipe logging.
3. **Phase 3 (Storage & Custodianship Ledger)**: Archival sleeve location tracking, limited print edition logging, gallery exhibition records, and sales history.
