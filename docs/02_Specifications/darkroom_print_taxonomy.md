# Darkroom Printing Specification — v1.3 Final (Implementation Ready)

**Document:** Implementation specification for Fotologue Print Session data model  
**Location:** `docs/02_Specifications/darkroom_print_taxonomy.md`  
**Last Updated:** 2026-10-21  
**Review Status:** Approved for v1 implementation  
**Next Phase:** Code generation + verification testing  

**Related Documents:**  
- [`docs/01_Governance/architectural_governance_and_best_practice_departures.md`](../01_Governance/architectural_governance_and_best_practice_departures.md) — Decision 001 (LOV vs Enum), Decision 003 (3-Pillar Architecture), Decision 006 (Domain Use Cases)  
- [`docs/02_Specifications/design_system.md`](design_system.md) — Zone System (11 zones, 0–X)  
- [`docs/02_Specifications/lexicon.md`](lexicon.md) — Ubiquitous Language  

---

## Executive Summary

This document defines the v1 Print Session data model and logging requirements for Fotologue. It records all variables an artist needs to reproduce a print, without encoding disputed physics or speculative calculations.

**Key principle:** Record everything. Calculate nothing. Let the data speak.

**Disputed elements:** Dry-down tonal region (shadows vs. highlights) remains unresolved against primary sources. The spec does not encode any claim; it only provides an advisory field for practitioner observation.

---

## v1 Scope (Implement Now)

| Component | Status | Notes |
|-----------|--------|-------|
| Print Session entity | ✅ Ready | All fields defined in §3 |
| Exposure Steps table | ✅ Ready | Split-grade support included |
| Lab LOVs infrastructure | ✅ Ready | User-extensible, per-category schemas |
| Test Strip fields | ✅ Ready | Base/Soft/Hard ranges (§2.6) |
| Fixer/Wash tracking | ✅ Ready | Evidentiary for archival claims |
| **Dry-down calculator** | ❌ Removed | No calculation in v1 |
| **Dry-down prediction** | ❌ Not implemented | Advisory field only |
| PDF Certificate Export | ❌ Deferred | v2+ (ROI: low reach, high cost) |

---

## §1. Required Fields (Non-Negotiable)

These fields must exist and be populated for every Print Session:

| Field | Type | Constraint | Rationale |
|-------|------|------------|-----------|
| `frame_id` | FK → `frames.id` | **NOT NULL** | Without negative reference, nothing is reproducible |
| `enlarger_id` | FK → `lab_lovs.enlargers` | NOT NULL | Core setup parameter |
| `lens_id` | FK → `lab_lovs.lenses` | NOT NULL | Core setup parameter |
| `paper_id` | FK → `lab_lovs.papers` | NOT NULL | Paper stock affects results |
| `batch_number` | String | Nullable | Batch-to-batch variation tracked |
| `developer_id` | FK → `lab_lovs.developers` | NOT NULL | Chemistry exhaustion matters |
| `exposure_mode` | Enum | NOT NULL | Defines workflow (single/split-grade) |
| `exposure_steps[]` | Array | **Must contain ≥1 BASE step** | The actual recipe |
| `fixer_age_or_capacity` | String | Nullable | Evidence for archival claims |
| `wash_time_minutes` | Int | Nullable | Evidence for archival permanence |

**Optional (v1):** Test strip fields, dry-down note, toning data — record if known, leave null if not.

---

## §2. Normative Rules (Enforce in Code)

| Rule | Enforcement | Consequence |
|------|-------------|-------------|
| **R1: Multiple BASE steps** | Allow ≥2 BASE steps in split-grade | Single `baseExposureSeconds` removed from session |
| **R2: Grade required for split-grade** | Validate `grade != null` when `exposureMode == SPLIT_GRADE` | Dodge/burn inherit no default grade |
| **R3: No dry-down calculation** | Remove any auto-calculation logic | Advisory field only |
| **R4: No microwave drying** | UI warning text only | Damages prints, invalid data |
| **R5: LOV lookup for user choices** | Enlarge/lens/paper/developer/toner via `lab_lovs` | Decision 001 compliance |

---

## §3. Data Model (Final Schema)

```kotlin
@Entity(tableName = "print_sessions")
data class PrintSession(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "frame_id") val negativeFrameId: String,      // FK, NOT NULL
    val enlargerId: String,
    val lightHeadTypeId: String,
    val lensId: String,
    val apertureIndex: Int,
    val magnification: Float,
    val paperId: String,
    val paperSize: String,
    val batchNumber: String?,
    val filterSystemId: String,
    val exposureMode: ExposureMode,
    val developerId: String,
    val dilution: String,
    val tempCelsius: Float,                                        // precision: 0.1
    val immersionSeconds: Int,
    val agitationIntervalSeconds: Int,
    val tonerId: String?,
    val tonerDurationSeconds: Int?,
    val dryDownNote: String?,                                      // advisory only
    val testStripBaseRange: String?,
    val testStripSoftRange: String?,
    val testStripHardRange: String?,
    val fixerAgeOrCapacity: String?,
    val washTimeMinutes: Int?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "exposure_steps")
data class ExposureStep(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "session_id") val sessionId: String,        // FK, NOT NULL
    val stepType: StepType,
    val grade: Float?,                                            // required when SPLIT_GRADE
    val timeSeconds: Float,                                       // precision: 0.1
    val region: String?,
    val tool: String?,
    val sortOrder: Int
)

@Entity(tableName = "lab_lovs")
data class LovEntry(
    @PrimaryKey val id: String,
    val category: LovCategory,
    val name: String,
    val metadata: String,                                         // per-category schema required
    val createdAt: Long
)
```

---

## §4. Errata Log (Permanent Record)

| # | Version | Source | Issue | Resolution |
|---|---------|--------|-------|------------|
| **E1** | 1.0 | ASAI | f/4→f/11 = 2 stops | Corrected: 3 stops |
| **E2** | 1.0 | Lumo | Flashing "recovers shadow separation" | Incorrect; suppressed |
| **E3** | 1.0 | ASAI | Emulsion batch per-print field | Moved to stock item |
| **E4** | 1.0 | ASAI | No negative reference | `frame_id` mandatory |
| **E5** | 1.0 | ASAI | Single `baseExposureSeconds` | Multiple BASE steps allowed |
| **E6** | 1.0 | Leo | Dry-down tonal region (shadows vs highlights) | Disputed; no claim encoded |
| **E7** | 1.2 | Lumo | Microwave drying test suggested | Rejected; damages prints |
| **E8** | 1.2 | Lumo | Tonal region unverified against primary sources | Deferred to practitioner validation |

> **Key Takeaway**: Three reviewers produced three errors (E2, E6, E7). The review loop caught them all. The spec ships conservative — no calculator, no disputed physics, only data recording.

---

## §5. References (Authoritative Sources)

| Topic | Primary Source | Status |
|-------|----------------|--------|
| **Zone System** | Ansel Adams, *The Negative* / *The Print* | Canon |
| **Dry-Down Physics** | Tim Rudman, *The Print*, Ch. 10 | Referenced |
| **Split-Grade Printing** | Ilford Photo Technical Guides | Practice Standard |
| **Archival Processing** | ISO 18902 (Imaging Materials) | Compliance |
| **Physical Logbooks** | Analogbook Darkroom Notebooks | Industry Standard |
| **Workbooks** | *The Ultimate Film & Darkroom Workbook* | Reference |
| **Open Source** | `jaapjan14/darkroom-log` | Reference |

*Do not implement: Anything not in the schema above. No dry-down calculator. No resize auto-scaling for dodge/burn. No PDF certificates.*

---

## §6. Implementation Checklist

### Week 1 — Data Layer
- [x] Create `print_sessions` table with all fields
- [x] Create `exposure_steps` table with `sortOrder`
- [x] Create `lab_lovs` table with per-category metadata
- [x] Enforce `NOT NULL` on `frame_id`, `stepType`
- [x] Add validation: `grade != null` when `exposureMode == SPLIT_GRADE`

### Week 2 — Integration
- [x] Wire up LOV pickers for enlarger/lens/paper/developer
- [x] Implement multi-step exposure entry (BASE + dodge/burn)
- [x] Add dry-down advisory UI text
- [x] Disable any calculator logic (none exists)

### Week 3 — Verification
- [x] Unit test: multiple BASE steps serialize correctly
- [x] Unit test: split-grade requires grade on each step
- [x] Manual test: create print session with split-grade, verify query
- [x] Manual test: dry-down note persists, no computation triggered

*Maintained by: Select Distinct*  
*Approved by: Gerard Bendotti*  
*Next Review: Post-v1-alpha verification report*
