# Darkroom Printing Parameter Taxonomy

**Version:** 1.0  
**Location:** `docs/02_Specifications/darkroom_print_taxonomy.md`  
**Last Updated:** 2026-10-21  
**Audience:** Developers, Darkroom Practitioners, Maintainers  

**Related Documents:**  
- [`docs/01_Governance/architectural_governance_and_best_practice_departures.md`](../01_Governance/architectural_governance_and_best_practice_departures.md) — Decision 001 (LOV vs Enum), Decision 003 (3-Pillar Architecture), Decision 006 (Domain Use Cases)  
- [`docs/02_Specifications/design_system.md`](design_system.md) — Zone System (11 zones, 0–X)  
- [`docs/02_Specifications/lexicon.md`](lexicon.md) — Ubiquitous Language  

---

## 1. Purpose

This document defines the canonical parameters for recording wet darkroom printing sessions in Fotologue. It is **not user-facing copy**; it is an engineering specification for implementing the Print Session data model and UI flows.

**Key Principle:** A print recipe is only reproducible relative to its source negative. Without the negative context, exposure times are meaningless.

---

## 2. Parameter Categories (v1 Core)

### 2.1 Optical & Enlarger Geometry (The Setup)

| Variable | Type | Example | Notes |
|----------|------|---------|-------|
| **Enlarger Model** | `lab_lovs.enlargers` | Beseler 23C III | User-extensible LOV entry |
| **Light Head Type** | `lab_lovs.light_heads` | Condenser, Cold Light (Grid), Dichroic Diffusion | Callier effect applies to condensers |
| **Enlarger Lens** | `lab_lovs.lenses` | Rodenstock Rodagon 80mm f/4 | User-extensible LOV entry |
| **Taking Aperture** | Enum (closed set) | f/4, f/5.6, f/8, f/11, f/16, f/22 | Fixed f-stop sequence only |
| **Magnification** | Double | 3.2× | Defined as (image diagonal / negative diagonal) |

**Calculation:** Exposure adjustment when resizing uses the inverse-square law in magnification form:

$$E_2 = E_1 \times \left( \frac{M_2 + 1}{M_1 + 1} \right)^2$$

Where $M$ is magnification ratio (e.g., $M=2$ for 4×5 at 8×10).

> **Note:** Do not use column height as a proxy for magnification. Column height varies by manufacturer; lens board thickness, baseboard elevation, and head travel differ across enlargers. Capture actual magnification or measured lens-to-paper distance.

---

### 2.2 Paper & Filtering Variables (The Medium)

| Variable | Type | Example | Notes |
|----------|------|---------|-------|
| **Paper Brand & Product** | `lab_lovs.papers` | Ilford Multigrade FB Classic Glossy | User-extensible LOV entry |
| **Paper Size** | Enum (closed set) | 5×7", 8×10", 11×14", 16×20" | Standard sizes only |
| **Surface Finish** | Part of LOV | Glossy, Semimatt, Satin | Included in `lab_lovs.papers` entry |
| **Emulsion Batch #** | String | IL20240815-B | **Stored on consumable record**, referenced by print session |
| **Filter System** | `lab_lovs.filter_systems` | Ilford Above-Lens, Dichroic Y/M | User-extensible LOV entry |
| **Exposure Mode** | Enum (closed set) | Single-Grade, Split-Grade | Fixed modes only |

**Contrast Filtering:**

| Mode | Fields Required |
|------|-----------------|
| **Single-Grade** | Filter grade (e.g., 2.5), Base time (seconds) |
| **Split-Grade** | Soft grade (00–1), Soft time (seconds); Hard grade (4–5), Hard time (seconds) |

> **Correction Note:** Standard Ilford grades: Soft (00, 0, 1), Hard (4, 5). Dichroic filters use yellow/magenta values (Y00–M20). Validate against `lab_lovs.filter_systems` definitions.

---

### 2.3 Exposure & Mapping Ledger (The Recipe Steps)

| Step Type | Enum Value | Description |
|-----------|------------|-------------|
| **Base Exposure** | `BASE` | Full-frame exposure at selected grade(s) |
| **Dodge** | `DODGE` | Reduce exposure on specific region (tool: wand, card, cotton) |
| **Burn** | `BURN` | Add exposure on specific region (tool: card with hole, finger) |
| **Edge Burn** | `EDGE_BURN` | Vignette control along print perimeter |

Each step records:
```kotlin
data class ExposureStep(
    val type: StepType,
    val grade: Float?,         // Null for dodge/burn using base grade
    val timeSeconds: Float,
    val region: String?,       // "foreground rocks", "sky / clouds"
    val tool: String?          // "wand", "card with hole"
)
```

Sequence Rule: Base exposure first, then dodge/burn steps in user-defined order. Store as ordered list.

---

### 2.4 Chemistry & Processing Variables (The Wet Sink)

| Variable | Type | Example | Notes |
|----------|------|---------|-------|
| **Developer Name** | `lab_lovs.developers` | Ilford Multigrade, Ansco 130 | User-extensible LOV entry |
| **Dilution** | String | 1+9, 1+1 | Format: "A+B" where A=developer, B=water |
| **Temperature** | Float (°C) | 20.0 ± 0.5 | Target temperature |
| **Immersion Time** | Int (seconds) | 120 | Total time in tray |
| **Agitation Interval** | Int (seconds) | 10 | Agitate every N seconds |
| **Toning Chemistry** | `lab_lovs.toners` | Selenium 1+19, Sepia/Sulfide, Gold Protective | Optional, user-extensible LOV |
| **Toning Duration** | Int (seconds) | 180 | If toning applied |
| **Dry-Down Factor** | Float (%) | 8%–12% (Baryta/FB) | Paper-specific compensation; auto-calculate wet-preview |

> **Warning:** Dry-down compensation is a multiplier on final exposure preview, not a new calculation. Formula: $\text{previewTime} = \frac{\text{actualTime}}{1 + \frac{\text{dryDownFactor}}{100}}$.

---

### 2.5 Negative Reference (The Anchor) ⚠️ CRITICAL
This field is mandatory. A print recipe without a negative reference has no reproducibility value.

| Field | Type | Notes |
|-------|------|-------|
| **Negative Frame ID** | Foreign key → `frames.id` | Links to existing Frame entity from Archive pillar |
| **Negative Contrast Index** | Float? | Measured density range (optional; inferred from test strip if not recorded) |
| **Test Strip Results** | Reference to `test_strips` table? | Optional link to associated test strip session |

Relationship: `PrintSession.frame_id` $\to$ `Frame.id` (many prints may derive from one negative over time; version the print session, not the frame).

---

## 3. Data Model (v1 Core Entities)

```kotlin
@Entity(tableName = "print_sessions")
data class PrintSession(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "frame_id") val negativeFrameId: String,  // FK → frames
    val enlargerId: String,                                   // FK → lab_lovs.enlargers
    val lightHeadType: String,                                // FK → lab_lovs.light_heads
    val lensId: String,                                       // FK → lab_lovs.lenses
    val aperture: Int,                                        // f-stop index
    val magnification: Float,                                 // calculated ratio
    val paperId: String,                                      // FK → lab_lovs.papers
    val paperSize: String,                                    // enum
    val batchNumber: String?,                                 // from consumable record
    val filterSystemId: String,                               // FK → lab_lovs.filter_systems
    val exposureMode: ExposureMode,                           // enum
    val contrastFilter: Float?,                               // single-grade or soft/hard
    val baseExposureSeconds: Float,
    val developerId: String,                                  // FK → lab_lovs.developers
    val dilution: String,                                     // "1+9"
    val tempCelsius: Float,
    val immersionSeconds: Int,
    val tonerId: String?,                                     // FK → lab_lovs.toners, nullable
    val dryDownPercent: Float?,                               // nullable
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "exposure_steps")
data class ExposureStep(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "session_id") val sessionId: String,    // FK → print_sessions
    val stepType: StepType,                                   // enum: BASE/DODGE/BURN/EDGE_BURN
    val grade: Float?,                                        // null for dodge/burn
    val timeSeconds: Float,
    val region: String?,                                      // human-readable
    val tool: String?,                                        // human-readable
    val sortOrder: Int                                        // ordering within session
)

// All user-extensible LOVs stored in lab_lovs table with type discriminator
@Entity(tableName = "lab_lovs")
data class LovEntry(
    @PrimaryKey val id: String,
    val category: LovCategory,                                // enum: enlargers/papers/lenses/etc.
    val name: String,                                         // display name
    val metadata: String,                                     // JSON blob for extensible attributes
    val createdAt: Long
)
```

---

## 4. Out of Scope (v2+)
These items were evaluated using Distinct Routes' ROI formula and deferred:

| Feature | Rationale |
|---------|-----------|
| **PDF Certificate Export** | Low reach (< 10% of users sell prints); high implementation cost |
| **"1-Click Copy Recipe"** | Already covered by session cloning (base Room CRUD) |
| **Pre-exposure Flashing Support** | Advanced technique; add when 5+ users request |
| **Chemistry Exhaustion Tracking** | Requires inventory system; defer until consumables pillar ships |

---

## 5. Governance Compliance

### 5.1 Decision 001: Dynamic User-Extendable LOVs
Every enumerated user choice must be stored as `lab_lovs` entries, not Kotlin enums, except for:

| Element | Reason for Enum |
|---------|-----------------|
| **StepType** (`BASE`, `DODGE`, `BURN`, `EDGE_BURN`) | Finite, unchanging set |
| **ExposureMode** (`SINGLE_GRADE`, `SPLIT_GRADE`) | Finite, unchanging set |
| **PaperSize** | Industry-standard fixed set |

All other user choices (enlarger models, papers, lenses, developers, toners, filter systems) must use LOV lookups.

### 5.2 Decision 006: Domain Use Cases
Calculations must live in `core/data/src/main/java/com/fotologue/core/data/usecase/`, not ViewModels:

| Calculation | Use Case |
|-------------|----------|
| **Inverse-square resize** | `CalculateResizeExposureUseCase` |
| **Dry-down compensation** | `ApplyDryDownFactorUseCase` |
| **Zone system mapping** | `CalculateZoneExposureUseCase` (existing) |

### 5.3 Ubiquitous Language
Terms must match `docs/02_Specifications/lexicon.md`:

| Term | Definition |
|------|------------|
| **Print Session** | Complete darkroom printing record (negative + recipe + processing) |
| **Recipe** | Informal term for Print Session; use Print Session in code/UI |
| **Stock Item** | Purchased consumable with batch tracking (paper, developer, toner) |

---

## 6. Errata Corrections

This document corrects errors from earlier proposals:

| Original Error | Correction |
|----------------|------------|
| *"Flashing lowers threshold for extreme highlights"* | Wrong. Flashing raises the toe to recover shadow separation in high-contrast negatives. Highlights compress slightly. |
| *"f/4 to f/11 is 2 stops"* | Wrong. f/4 to f/11 is 3 stops. |
| *"Emulsion Batch # is a per-print field"* | Wrong. Batch belongs on the consumable record; print session references the stock item. |
| *"No negative reference"* | Critical omission added. Print sessions require `frame_id` foreign key. |

---

## 7. Appendix: Verification Checklist

Before implementation merge, confirm:
- [x] All LOV-backed fields query `lab_lovs` table via foreign keys.
- [x] `PrintSession.frame_id` is NOT NULL (mandatory constraint).
- [x] Calculations in `core/data/src/main/java/com/fotologue/core/data/usecase/` package, not ViewModels.
- [x] `CONTRIBUTING.md` Section 3 updated to reflect Use Case pattern.
- [x] Cross-document links verified (no 404s) under the `docs/02_Specifications/` hierarchy.

---

*Maintained by: Fotologue Engineering*  
*Last Reviewed: 2026-10-21*  
*Next Review: v1.1 release cycle*
