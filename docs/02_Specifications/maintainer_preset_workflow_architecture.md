# Maintainer Preset Tooling & Release Workflow Architecture

This document serves as the permanent architectural specification for **Maintainer Tooling, Catalog Authoring, Data Grid Management, and Preset Release Workflows** across the Fotologue project.

---

## 1. Architectural Philosophy: Separating Maintainer Tooling from End-User Apps

End-user applications (**Fotologue Desktop** and **Fotologue Mobile**) are designed for photographers managing their hardware assets, logbooks, darkroom stock, and physical image archives.

Maintainers and core contributors authoring the **Canonical Master Preset Catalogue** require a dedicated, file-based, source-controlled workflow with automated schema validation, visual form editing, and linting.

```
MAINTAINER AUTHORING TOOL                      AUTOMATED VALIDATION & BUILD              END-USER APPLICATIONS
(./gradlew :desktop:runMaintainer)             (Gradle / GitHub CI Action)               (Client Ingestion / Cloud Refresh)

┌──────────────────────────────────────┐       ┌─────────────────────────────┐        ┌─────────────────────────┐
│ Maintainer GUI / Authoring App       │       │ ./gradlew assembleMaster..  │        │ Fotologue Desktop       │
│ ├── Sortable Data Grid Architecture  │ ────> │  ├── Schema Validation      │ ─────> │ └── Ingests base bundle │
│ ├── Column Header Filter Dropdowns   │       │  ├── Dual-Output Compilation│        └─────────────────────────┘
│ ├── Active File Path Display         │       │  │   (src/main/assets &    │        ┌─────────────────────────┐
│ └── Saves to modular JSON files:     │       │  │    src/main/resources)   │ ─────> │ Fotologue Mobile        │
│     core/data/.../presets/*.json     │       │  └── Master Release Bundle  │        │ └── 1-Click Cloud Refresh
└──────────────────────────────────────┘       └─────────────────────────────┘        └─────────────────────────┘
```

---

## 2. Maintainer Preset Authoring Tool (`./gradlew :desktop:runMaintainer`)

Maintainers use a dedicated desktop authoring application launched via `./gradlew :desktop:runMaintainer`:

- **Sortable Data Grid Architecture**: Displays all presets in a clean tabular grid with 1-click column sorting (`Brand ▲`, `Preset Name`, `Type`, `Mount`, `Preset ID`).
- **Header-Integrated LOV Filtering**: Click `BRAND ▾`, `TYPE ▾`, or `MOUNT ▾` directly in column header cells to apply multi-criteria filtering across active category datasets.
- **Tailored Category Forms**: Category-tailored forms with controlled dropdowns for brands, mounts, chemical types, and exposure compensation factors (*Hoya ND8*, *CPL*, *Yellow K2*).
- **Active Preset File Path Display**: Prominently displays the resolved absolute path to the active JSON file (*e.g. `📂 Active Preset File Path: C:\...\core\data\src\main\resources\presets\cameras.json`*).
- **Direct JSON Serialization**: Formats, lints, and saves directly into modular source-controlled JSON files in `core/data/src/main/resources/presets/`.

---

## 3. Modular Preset Source Structure (`core/data/src/main/resources/presets/`)

Maintainers work in human-readable, domain-separated JSON files inside source control:

- **`cameras.json`**: 82 Film SLRs, Rangefinders, TLRs, Medium Format, Large Format bodies.
- **`lenses.json`**: 93 35mm, Medium Format, Large Format, Flagship L-Series, and Taking Lenses.
- **`films.json`**: B&W, Colour Negative, Slide/Reversal film stocks.
- **`teleconverters.json`**: 12 Teleconverters across Canon, Nikon, Pentax, Minolta, Leica, Hasselblad.
- **`filters.json`**: 35 Optical filters across Hoya, B+W, Tiffen, Rollei, and Hasselblad Bayonets with pre-wired exposure compensation stops.
- **`flashes.json`**: 15 Speedlights, Shoe-mount, and Pop-up/Built-in flash presets.
- **`viewfinders.json`**: 21 System finders, including the complete Pentax LX System (FA-1, FE-1, FF-1, FB-1, FC-1, FD-1, FD-2).
- **`consumables.json`**: 30 Darkroom chemistry and paper stock presets (*D-76, Xtol, HC-110, Rodinal, Cs41, Multigrade V*).
- **`lab_equipment.json`**: 37 Enlargers (*Beseler, Durst, LPL, Omega*), Dev Tanks (*Paterson, Jobo, AP, Kindermann*), Timers, Safelights, and Scopes.
- **`suppliers.json`**: 22 Global and Australian photography vendors & processing labs (*B&H Photo, Camera Electronic Perth, Leederville Cameras, Fitzgerald Photo, Rewind Photo Lab, Vanbar*).

---

## 4. Automated Compilation Engine (`./gradlew assembleMasterCatalog`)

A dedicated build task compiles maintainer edits for release:

1. **Dual Output Compilation**: Writes `master_preset_catalog.json` to both `src/main/assets/` (for Android APK assets) and `src/main/resources/` (for JVM Desktop resources).
2. **Pre-Build Trigger**: Runs automatically during Gradle build tasks (`preBuild.dependsOn("assembleMasterCatalog")`).

---

## 5. Non-Destructive Client Ingestion & Remote Cloud Refresh Guarantee

When Fotologue Mobile or Desktop receives an updated `master_preset_catalog.json` (via app update or 1-click Cloud Preset Refresh over HTTPS):
- `MasterPresetEngine` ingests new base entries into the local database (`isDeveloperPreset = true`).
- **CRITICAL GUARANTEE**: It **NEVER overrides, alters, or deletes** any user-created custom entries, user custom LOVs, or user-edited asset properties.

---

*Document updated: October 2026 for Fotologue v3.0 Maintainer Tooling Architecture.*
