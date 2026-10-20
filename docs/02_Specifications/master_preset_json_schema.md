# Fotologue Master Preset JSON Schema & Data Definition (v3.0)

This document serves as the authoritative specification for all **10 Preset Categories** in Fotologue (`cameras.json`, `lenses.json`, `films.json`, `teleconverters.json`, `filters.json`, `flashes.json`, `viewfinders.json`, `consumables.json`, `lab_equipment.json`, `suppliers.json`).

---

## 1. Top-Level `MasterPresetItem` Schema

Every preset item in the catalog complies with the `MasterPresetItem` Kotlin data class:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "MasterPresetItem",
  "type": "object",
  "required": ["id", "category", "brand", "name", "attributes", "isDeveloperPreset"],
  "properties": {
    "id": {
      "type": "string",
      "description": "Unique catalog identifier formatted as {cat}_{brand}_{model}"
    },
    "category": {
      "type": "string",
      "enum": [
        "CAMERA",
        "LENS",
        "FILM",
        "TELECONVERTER",
        "FILTER",
        "FLASH",
        "VIEWFINDER",
        "CONSUMABLE",
        "EQUIPMENT",
        "SUPPLIER"
      ]
    },
    "subCategory": {
      "type": ["string", "null"],
      "description": "Optional secondary classification (e.g., 'Film SLR', 'Prime', 'B&W Negative')"
    },
    "brand": {
      "type": "string",
      "description": "Canonical manufacturer name or brand"
    },
    "name": {
      "type": "string",
      "description": "Full product or preset title"
    },
    "modelNumber": {
      "type": ["string", "null"],
      "description": "Short model designation or catalog number"
    },
    "attributes": {
      "type": "object",
      "additionalProperties": { "type": "string" },
      "description": "Category-specific key-value specification map"
    },
    "isDeveloperPreset": {
      "type": "boolean",
      "default": true
    }
  }
}
```

---

## 2. Category Specification Rules & Controlled LOVs

### A. `CAMERA` (`cameras.json`)
- **Required Attributes**:
  - `type`: `Film SLR` | `Rangefinder` | `TLR` | `Medium Format SLR` | `Point & Shoot` | `Compact` | `Large Format` | `DSLR` | `Mirrorless`
  - `mount`: `Canon FD` | `Nikon F` | `Pentax K` | `Leica M` | `Leica L39` | `Hasselblad V` | `Contax C/Y` | `Olympus OM` | `Minolta SR/MD` | `M42 Thread` | `Fixed` | `Bay I` | `Bay II` | `Bay III`
  - `shutterSpeeds`: Shutter range string (e.g. `"125s - 1/2000s, B"`)
  - `fixedLens`: [Required when `mount == "Fixed"`] Exact name matching an entry in `lenses.json`.

```json
{
  "id": "cam_pentax_lx",
  "category": "CAMERA",
  "subCategory": "Film SLR",
  "brand": "Pentax",
  "name": "Pentax LX",
  "modelNumber": "LX",
  "attributes": {
    "type": "Film SLR",
    "mount": "Pentax K",
    "shutterSpeeds": "125s - 1/2000s, B"
  },
  "isDeveloperPreset": true
}
```

---

### B. `LENS` (`lenses.json`)
- **Required Attributes**:
  - `type`: `Prime` | `Zoom` | `Macro` | `Anamorphic` | `Tilt-Shift`
  - `mount`: Mount LOV option (or `"Fixed"` for fixed-lens camera taking lenses)
  - `focalLength`: Focal length string (e.g. `"50mm"`, `"100-400mm"`)
  - `maxAperture`: Maximum aperture (e.g. `"f/1.4"`, `"f/2.8"`)
  - `minAperture`: Minimum aperture (e.g. `"f/16"`, `"f/22"`, `"f/32"`)
  - `filterSize`: Filter thread or bayonet mount (e.g. `"52mm"`, `"77mm"`, `"Bay III"`)

```json
{
  "id": "lens_canon_ef_100_28l_macro",
  "category": "LENS",
  "subCategory": "Macro",
  "brand": "Canon",
  "name": "Canon EF 100mm f/2.8L Macro IS USM",
  "attributes": {
    "type": "Macro",
    "mount": "Canon EF",
    "filterSize": "67mm",
    "focalLength": "100mm",
    "maxAperture": "f/2.8",
    "minAperture": "f/32"
  },
  "isDeveloperPreset": true
}
```

---

### C. `FILM` (`films.json`)
- **Required Attributes**:
  - `type`: `Colour Negative (C-41)` | `Black & White Negative (B&W)` | `Colour Slide / Reversal (E-6)` | `B&W Slide` | `Infrared`
  - `iso`: Box ISO speed string (e.g. `"100"`, `"400"`, `"3200"`)
  - `format`: Format string (e.g. `"35mm"`, `"120"`, `"4x5 in"`)

```json
{
  "id": "film_kodak_portra_400",
  "category": "FILM",
  "subCategory": "Colour Negative (C-41)",
  "brand": "Kodak",
  "name": "Kodak Portra 400",
  "modelNumber": "Portra 400",
  "attributes": {
    "type": "Colour Negative (C-41)",
    "iso": "400",
    "format": "35mm"
  },
  "isDeveloperPreset": true
}
```

---

### D. `TELECONVERTER` (`teleconverters.json`)
- **Required Attributes**:
  - `magnification`: `1.4x` | `2.0x`
  - `lightLoss`: `1 Stop` | `2 Stops`
  - `mount`: Lens mount LOV option

```json
{
  "id": "tc_canon_ef_14_2",
  "category": "TELECONVERTER",
  "subCategory": "Teleconverter",
  "brand": "Canon",
  "name": "Canon Extender EF 1.4x II",
  "modelNumber": "EF 1.4x II",
  "attributes": {
    "magnification": "1.4x",
    "lightLoss": "1 Stop",
    "mount": "Canon EF"
  },
  "isDeveloperPreset": true
}
```

---

### E. `FILTER` (`filters.json`)
- **Required Attributes**:
  - `compensationStops`: `+0.0 Stops (Clear / UV)` | `+0.3 Stops` | `+0.6 Stops` | `+1.0 Stop (Yellow K2)` | `+1.5 Stops (CPL / Polarizer)` | `+2.0 Stops (Orange 16)` | `+3.0 Stops (Red 25A / ND8)` | `+6.0 Stops (ND64)` | `+10.0 Stops (ND1000)`
  - `threadSize`: Filter size string (e.g. `"52mm"`, `"77mm"`, `"Bay I"`, `"Bay 60"`)

```json
{
  "id": "filter_hoya_nd8",
  "category": "FILTER",
  "subCategory": "ND (Neutral Density)",
  "brand": "Hoya",
  "name": "Hoya PRO1 Digital ND8 (52mm)",
  "modelNumber": "ND8",
  "attributes": {
    "compensationStops": "+3.0 Stops (Red 25A / ND8)",
    "threadSize": "52mm"
  },
  "isDeveloperPreset": true
}
```

---

### F. `FLASH` (`flashes.json`)
- **Required Attributes**:
  - `type`: `Shoe Mount / Speedlight` | `Built-in / Pop-up Flash` | `Handle Mount / Potato Masher` | `Ring Flash` | `Studio / Strobe`
  - `syncType`: `Hotshoe` | `PC Sync` | `Built-in`
  - `guideNumber`: Guide number string (e.g. `"36m (ISO 100)"`, `"12m (ISO 100)"`)

```json
{
  "id": "flash_canon_580ex2",
  "category": "FLASH",
  "subCategory": "Shoe Mount / Speedlight",
  "brand": "Canon",
  "name": "Canon Speedlite 580EX II",
  "modelNumber": "580EX II",
  "attributes": {
    "type": "Shoe Mount / Speedlight",
    "syncType": "Hotshoe",
    "guideNumber": "58m (ISO 100)"
  },
  "isDeveloperPreset": true
}
```

---

### G. `VIEWFINDER` (`viewfinders.json`)
- **Required Attributes**:
  - `type`: `Eye-Level Prism` | `Waist-Level Finder` | `Action Finder` | `Right-Angle Finder` | `Coldshoe Auxiliary Finder` | `System Finder Base`
  - `compatibleCamera`: Camera body reference (e.g. `"Pentax LX"`, `"Nikon F3"`, `"Canon New F-1"`)

```json
{
  "id": "vf_pentax_fa1",
  "category": "VIEWFINDER",
  "subCategory": "Eye-Level Prism",
  "brand": "Pentax",
  "name": "Pentax FA-1 Eye-Level Finder",
  "modelNumber": "FA-1 / 30961",
  "attributes": {
    "type": "Eye-Level Prism",
    "magnification": "0.90x",
    "compatibleCamera": "Pentax LX"
  },
  "isDeveloperPreset": true
}
```

---

### H. `CONSUMABLE` (`consumables.json`)
- **Required Attributes**:
  - `type`: `Developer (Film)` | `Developer (Paper)` | `Stop Bath` | `Fixer` | `Wetting Agent` | `Bleach / Toner` | `Darkroom Paper` | `Chemical Cleaner`
  - `brand`: Restricted darkroom manufacturer LOV (`Ilford`, `Kodak`, `Agfa / Adox`, `Foma`, `Cinestill`, `Fujifilm`, `Paterson`, `Jobo`, `Tetenal`, `Bergger`, `Bellini`, `Champion`, `Photographers' Formulary`)
  - `mixRatio`: Dilution ratio string (e.g. `"Stock or 1+1"`, `"1+4"`, `"1+200"`)

```json
{
  "id": "con_kodak_d76_1l",
  "category": "CONSUMABLE",
  "subCategory": "Developer (Film)",
  "brand": "Kodak",
  "name": "Kodak D-76 Developer (1L)",
  "modelNumber": "D-76",
  "attributes": {
    "type": "Developer (Film)",
    "mixRatio": "Stock or 1+1",
    "capacity": "10 rolls"
  },
  "isDeveloperPreset": true
}
```

---

### I. `EQUIPMENT` (`lab_equipment.json`)
- **Required Attributes**:
  - `type`: `Enlarger` | `Dev Tank` | `Film Reel` | `Developing Tray` | `Measuring Cylinder / Beaker` | `Film Squeegee` | `Darkroom Timer / Exposure Timer` | `Safelight` | `Thermometer` | `Film Changing Bag` | `Graduated Pitcher` | `Focus Finder / Grain Focus Scope`
  - `brand`: Restricted lab hardware manufacturer LOV (`Adox`, `AP`, `Beseler`, `Durst`, `Hama`, `Ilford`, `Jobo`, `Kaiser`, `Kindermann`, `LPL / Saunders`, `Meopta`, `Omega / Saunders`, `Paterson`, `Peak`)

```json
{
  "id": "eq_paterson_universal_tank",
  "category": "EQUIPMENT",
  "subCategory": "Dev Tank",
  "brand": "Paterson",
  "name": "Paterson Super System 4 Universal Tank (2 Reels)",
  "modelNumber": "PTP115",
  "attributes": {
    "type": "Dev Tank",
    "capacity": "2x 35mm / 1x 120"
  },
  "isDeveloperPreset": true
}
```

---

### J. `SUPPLIER` (`suppliers.json`)
- **Required Attributes**:
  - `type`: `Regional Supplier` | `Online Retailer` | `Film Processing Lab` | `Repair Technician` | `Equipment Rental`
  - `region`: `Global / International` | `North America (USA/Canada)` | `Europe & UK` | `Australia & Oceania` | `Asia-Pacific`
  - `website`: Live URL string (e.g. `"https://www.bhphotovideo.com"`)

```json
{
  "id": "sup_camera_electronic",
  "category": "SUPPLIER",
  "subCategory": "Regional Supplier",
  "brand": "Camera Electronic",
  "name": "Camera Electronic (Perth, WA)",
  "attributes": {
    "type": "Regional Supplier",
    "region": "Australia & Oceania",
    "website": "https://www.cameraelectronic.com.au"
  },
  "isDeveloperPreset": true
}
```

---

## 3. Build & Release Pipeline

The Gradle task `./gradlew :core:data:assembleMasterCatalog` reads all 10 modular JSON files from `core/data/src/main/resources/presets/`, validates their array structures, and compiles them into a single release artifact written to both:
1. `core/data/src/main/assets/master_preset_catalog.json` (Mobile Android APK)
2. `core/data/src/main/resources/master_preset_catalog.json` (JVM Desktop Classpath Resource)
