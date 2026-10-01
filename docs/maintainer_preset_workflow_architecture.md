# Maintainer Preset Tooling & Release Workflow Architecture

This document serves as the permanent architectural specification for **Maintainer Tooling, Catalog Authoring, and Preset Release Workflows** across the Fotologue project.

---

## 1. Architectural Philosophy: Separating Maintainer Tooling from End-User Apps

End-user applications (**Fotologue Desktop** and **Fotologue Mobile**) are designed for photographers managing their hardware assets, logbooks, darkroom stock, and physical image archives.

Maintainers and core contributors authoring the **Canonical Master Preset Catalogue** require a dedicated, file-based, source-controlled workflow with automated schema validation and linting.

```
MAINTAINER AUTHORING AREA                       AUTOMATED VALIDATION & BUILD              END-USER APPLICATIONS
(Source Control / Modular JSON)                 (Gradle / GitHub Actions)                 (Client Ingestion)

┌──────────────────────────────────────┐        ┌─────────────────────────────┐        ┌─────────────────────────┐
│ core/data/.../presets/               │        │  ./gradlew validatePresets  │        │ Fotologue Desktop       │
│ ├── cameras.json                     │ ─────> │  ├── Schema Validation      │ ─────> │ └── Ingests base bundle │
│ ├── lenses.json                      │        │  ├── Duplicate ID Detection │        └─────────────────────────┘
│ ├── films.json                       │        │  ├── Lexicon Lint (-ue)     │        ┌─────────────────────────┐
│ ├── consumables.json                 │        │  └── Compiles Release Bundle│ ─────> │ Fotologue Mobile        │
│ └── lab_equipment.json               │        └─────────────────────────────┘        │ └── Ingests base bundle │
└──────────────────────────────────────┘                                               └─────────────────────────┘
```

---

## 2. Modular Preset Source Structure (`core/data/src/main/resources/presets/`)

Maintainers work in human-readable, domain-separated JSON files inside source control:

- **`cameras.json`**: Film SLRs, Rangefinders, TLRs, Medium Format, Large Format bodies.
- **`lenses.json`**: 35mm, Medium Format, Large Format, and Enlarger Lenses.
- **`films.json`**: B&W, Colour Negative, Slide/Reversal film stocks.
- **`teleconverters.json`**: Teleconverters, Macro Extension Tubes, Speed Boosters.
- **`filters.json`**: Threaded filters, Bayonet filters, Front Aux Lenses (Rolleiflex Mutar).
- **`flashes.json`**: Hotshoe flashes, Coldshoe units, PC sync flashes.
- **`viewfinders.json`**: Removable prisms, waist-level finders, coldshoe auxiliary finders.
- **`consumables.json`**: Chemistry, Paper stocks, Safety gear, Darkroom supplies.
- **`lab_equipment.json`**: Enlargers, Dev Tanks, Trays, Washers, Scanners, Timers.
- **`suppliers.json`**: Global & regional analogue photography suppliers & labs.

---

## 3. Automated Validation & Compilation Engine (`./gradlew validatePresets`)

A dedicated build task validates maintainer edits before release:

1. **Schema Validation**: Ensures all items have valid `id`, `category`, `brand`, and `name`.
2. **Duplicate ID Detection**: Fails the build if duplicate preset IDs exist.
3. **Australian English Lexicon Linting**: Enforces Australian spelling (`Colour`, `Catalogue`).
4. **Bundle Compilation**: Compiles all modular files into the master release asset `master_preset_catalog.json`.

---

## 4. Non-Destructive Client Ingestion Guarantee

When Fotologue Mobile or Desktop receives an updated `master_preset_catalog.json` (via app update or sync package):
- `MasterPresetEngine` ingests new base entries into the local database (`isDeveloperPreset = true`).
- **CRITICAL GUARANTEE**: It **NEVER overrides, alters, or deletes** any user-created custom entries, user custom LOVs, or user-edited asset properties.

---

*Document created: March 2026 for Fotologue v3.0 Maintainer Tooling Architecture.*
