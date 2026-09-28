# Fotologue Ubiquitous Language (Lexicon)

This document serves as the single source of truth for terminology across the Fotologue ecosystem. To prevent cognitive load and naming fragmentation, all codebase variables, database schemas, UI copy, and documentation must strictly adhere to these canonical terms.

## 1. Applications

| Canonical Term | Rejected Anti-patterns / Synonyms | Context |
| :--- | :--- | :--- |
| **Fotologue Desktop** | CCC, Central Command Centre, Base Camp, Desktop Vault, Admin Gatekeeper | The desktop JVM application used for archiving, darkroom management, and reference data administration. |
| **Fotologue Mobile** | Field Device, The Spoke, Capture Tool, Mobile App | The Android mobile application used for field exposure logging and local gear management. |

## 2. Infrastructure

| Canonical Term | Rejected Anti-patterns / Synonyms | Context |
| :--- | :--- | :--- |
| **Reference Data** | Foundation Data, Master Picklists, Knowledge Base, Master Data | The master lists of cameras, films, suppliers, and chemistry maintained by Fotologue Desktop. |
| **Reference Sync Package** | Foundation Manifest, Fridge Manifest, Sync Payload, Foundation Archive | The JSON payload exported by Fotologue Desktop and ingested by Fotologue Mobile to sync Reference Data. |

## 3. Photography Entities

| Canonical Term | Rejected Anti-patterns / Synonyms | Context |
| :--- | :--- | :--- |
| **Frame** | Log, Log Entry, Exposure, Shot | A single photographic exposure. *Note: A Frame belongs to a Roll and is contextually a Roll Frame.* |
| **Roll** (or **Film Roll** / **Plate**) | Session, Physical Sleeve | A collection of Frames on a single physical medium. |
| **Kit** (or **Camera Kit**) | Loadout | A specific combination of camera, lens, and accessories used for a shoot. |
| **Gear Collection** | Gear Library, Gear Inventory, Gear Locker, Equipment Pool | Represents the total sum of available hardware (owned, borrowed, or hired). |

## 4. Lab & Inventory

| Canonical Term | Rejected Anti-patterns / Synonyms | Context |
| :--- | :--- | :--- |
| **Consumables** | Inventory, Chemistry, Materials | Expendable lab items (e.g., film, developer, paper). |
| ├── **Product Definition** | Inventory Master, Catalogue Item | The abstract base product (e.g., "Ilford HP5 Plus 400"). |
| └── **Stock Item** | Stock Lot, Physical Stock, Batch | The physical purchased bottle or box. |
| **Lab Equipment** | Hardware, Darkroom Gear, Apparatus | Non-consumable hardware (e.g., Enlargers, Dev Tanks). |

## 5. Sync & Triage Actions

| Canonical Term | Rejected Anti-patterns / Synonyms | Context |
| :--- | :--- | :--- |
| **Unassigned Frames** | Orphans, Legacy Imports, Pending Review | Unrecognised mobile data arriving in Fotologue Desktop without a mapped physical Roll. |
| **Data Reconciliation** | *N/A (Umbrella category)* | The process of resolving incoming data conflicts or unassigned items. |
| ├── **Auto-Link** | Auto-Home | Automatically mapping Unassigned Frames to an active Roll. |
| ├── **Merge Duplicates** | Combine | Consolidating two identical records into one. |
| ├── **Consolidate Records** | | Merging related or fragmented data. |
| ├── **Link to Existing** | Map to Existing | Manually connecting an unassigned item to a known Reference Data entry. |
| └── **Standardise Metadata** | Standardize | Harmonising names or typos to match official Reference Data. |
| **Sync to Mobile** | Publish Manifest, Sync Down, Share with Field | Action on Desktop to export the Reference Sync Package. |
| **Submit for Import** | Export Master Archive, Ingest ZIP, Push to Vault, Share with Base Camp | Action on Mobile to export the ZIP payload for Desktop ingestion. |