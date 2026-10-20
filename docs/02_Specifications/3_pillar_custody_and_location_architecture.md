# 3-Pillar Custody, Location & Interaction Architecture

This document serves as the permanent architectural specification for the **3-Pillar Asset + Contact + Interaction** framework across the Fotologue ecosystem (**Fotologue Desktop** and **Fotologue Mobile**).

---

## 1. Architectural Vision & Domain Motivation

In analogue, hybrid, and cinema photography, hardware assets (cameras, rare lenses, medium format backs, chemistry) are valuable, mobile, and frequently move between physical storage, hire houses, friends, repair technicians, and processing labs.

Tacking commercial, rental, loan, and location columns onto individual asset tables causes schema duplication, data fragmentation, and a maintenance nightmare.

The **3-Pillar Architecture** applies textbook **Domain-Driven Design (DDD)** by decoupling:
1. **What** (The Physical Asset or Hardware)
2. **Who** (The Party or Contact)
3. **The Relationship Interaction** (The Custody, Order, Loan, or Repair Transaction)

```
PILLAR 1: ASSETS                 PILLAR 2: CONTACTS               PILLAR 3: CUSTODY INTERACTION
┌───────────────────────┐       ┌───────────────────────┐        ┌─────────────────────────────┐
│ Cameras               │       │ Suppliers / Stores    │        │ Custody Interaction Ledger  │
│ Lenses                │       │ Labs                  │ ─────> │ ├── Asset Type & ID         │
│ Teleconverters / Rings│       │ Repair Techs          │        │ ├── Contact ID              │
│ Filters / Front Aux   │       │ Borrowers / Friends   │        │ ├── Interaction Type        │
│ Flashes / Viewfinders │       │ (System PickContact)  │        │ │   (PURCHASE_ORDER /       │
│ Consumables           │       └───────────────────────┘        │ │    LOAN_OUT / RENTAL_IN /  │
│ Lab Equipment         │                                        │ │    SERVICE_REPAIR)          │
└───────────────────────┘                                        │ ├── Order Status Lifecycle  │
                                                                 │ │   (WAITLISTED / PACKAGING │
STORAGE LOCATIONS                                                │ │    IN_TRANSIT / DELIVERED) │
┌───────────────────────┐                                        │ ├── Tracking # & Carrier    │
│ Dry Cabinet A         │ ──────────────────────────────────────────────────────> │ ├── Start / Due / Return Date│
│ Studio Shelf 2        │                                        │ └── Fee / Rating / Notes    │
│ Camera Bag #1         │                                        └─────────────────────────────┘
└───────────────────────┘
```

---

## 2. Dynamic LOV (List of Values) Paradigm

To prevent the limitations of rigid hardcoded ENUMs or static string lists:
1. **Zero Rigid ENUMs**: No field locks the user into hardcoded options.
2. **User-Extendable LOV Database**: Every dropdown across Mobile and Desktop is backed by dynamic database tables (`reference_lovs` / `lab_lovs`).
3. **Auto-Register on the Fly**: When a user enters a custom category (*"Anamorphic Adapter"*), mount (*"Bayonet VI"*), or condition (*"Restored 1920s Classic"*), it automatically registers into their dynamic LOV database for 1-tap reuse across Mobile and Desktop.
4. **Minimise Free Text**: Fields present editable LOV dropdowns to ensure data consistency while empowering complete user customization.

---

## 3. Pillar 1: Assets & Gear Collection (`What`)

Represents the physical hardware or Darkroom Inventory. Every asset attribute is backed by dynamic LOVs:

- **Cameras**: Film SLRs, Rangefinders, TLRs, Point & Shoots, Compacts, Medium Format SLRs, Large Format bodies + *User LOVs*.
  - *TLR & Fixed-Lens Rule*: TLRs and fixed-lens cameras lock mount to `"Fixed"`, default format to `"120"`, and automatically generate an associated taking lens record in `Lenses`.
- **Lenses**: Make, Model Name, Lens Type (`Prime`, `Zoom`, `Macro`, `Anamorphic`, `Tilt-Shift` + *User LOVs*), Lens Mount, Focal Length (mm), Max/Min Apertures (`f/1.4`, `f/22`), Filter Size (mm), Min Focus (m), Max Magnification (x), Serial Number, Image Stabilised (Boolean).
- **Teleconverters & Extension Rings**: Optical converters and macro extension tubes mounting *behind* interchangeable lenses. (SLR / Rangefinder bodies only; fixed-lens bodies filtered out).
- **Filters & Front Aux Lenses**: Front-mounting optics attached to filter threads or bayonets (`ND`, `Yellow Y2/8`, `Red R2/25`, `CPL`, `UV / Skylight`, `Front Aux Lens / Rolleiflex Mutar / Diopter`, `Specialty` + *User LOVs*).
- **Flashes**: Guide Number (ISO 100, meters), Sync Type (`Hotshoe`, `Coldshoe`, `PC Sync Cord`, `Bulb Sync` + *User LOVs*), Serial Number.
- **Viewfinders**: Removable eye-level prisms, waist-level finders, action finders, right-angle finders, or coldshoe auxiliary finders.
- **Consumables**: Product Definitions & Stock Items (Chemistry, Paper, Supplies) with Dilution, Major Container Amount & Unit, Minor Inner Yield & Unit, and Safety Hazard flags.
- **Lab Equipment**: Darkroom hardware (Enlargers, Enlarger Lenses, Dev Tanks, Trays, Timers, Scanners) with condition ratings and parent-child sub-equipment links.

---

## 4. Pillar 2: Contacts Directory (`Who`)

Unifies all commercial vendors, darkroom labs, repair technicians, and personal colleagues into a single, tagged **Contacts Directory**:

- **Role Tags**: `#Supplier`, `#Lab`, `#RepairTech`, `#Borrower`, `#Friend` + *User LOVs*.
- **Attributes**: `id`, `name`, `email`, `phone`, `address`, `website`, `region`, `reliabilityRating` (1–5 Stars), `isActive` (In Use), `notes`.

### Privacy-First Android Contacts Integration (`ActivityResultContracts.PickContact`)
When loaning gear to a colleague on **Fotologue Mobile**:
1. Tapping **`[ 👤 Loan to... ]`** triggers Android's native system Contact Picker.
2. The user selects a contact from their phone's local address book.
3. Android hands back *only* the selected contact's name and contact ID.
4. **Privacy Guarantee**: Fotologue never scans, uploads, or harvests the user's address book. Data remains 100% local-first and private.

---

## 5. Pillar 3: Custody Interactions & Storage Locations (`The Relationship Interaction`)

Connects an **Asset** (Pillar 1) to a **Contact** (Pillar 2) and a **Storage Location**.

### Storage Locations (`StorageLocation`)
Defines canonical physical storage spaces:
- Attributes: `id`, `name` (*"Dry Cabinet A"*, *"Studio Shelf 2"*, *"Camera Bag #1"*), `type` (`Cabinet`, `Shelf`, `Bag`, `Studio`, `Drawer` + *User LOVs*), `notes`.

### Custody Interaction Types (`CustodyInteraction`)
1. **`PURCHASE_ORDER`** (Pre-Arrival Orders & In-Transit Asset Purchases)
   - Lifecycle States: **`WAITLISTED`** $\to$ **`PACKAGING`** $\to$ **`IN_TRANSIT`** $\to$ **`DELIVERED`**.
   - Attributes: `trackingNumber`, `carrier` (*AusPost*, *DHL*, *FedEx* + *User LOVs*), `estimatedArrivalMs`.
   - **Delivery Completion Flow**: Tapping `[ Mark Delivered ]` prompts the user for a physical `Storage Location` (*"Dry Cabinet A"*), automatically registers the asset into active inventory, and assigns its location!
2. **`LOAN_OUT`**: Asset loaned out to a borrower (`contactId`).
3. **`RENTAL_IN`**: Asset hired from a rental house (`contactId`).
4. **`SERVICE_REPAIR`**: Asset sent to a camera clinic for CLA or shutter repair (`contactId`).
5. **`LAB_PROCESSING`**: Film Roll dropped off at a darkroom processing lab (`contactId`).

---

## 6. Cross-Platform Ecosystem Parity (Mobile $\leftrightarrow$ Desktop)

### Fotologue Desktop (Stage 4 Administration)
- **100% Full-Width Layout**: Static Review Queue pane replaced with top header Actionable Alert Button (`[ 📥 Review Queue (x Pending) ]`).
- **13 Sub-Category Rail Navigation**:
  - `GEAR COLLECTION`: Kits, Cameras, Lenses, Films, Teleconverters, Filters, Flashes, Viewfinders.
  - `LAB & INVENTORY`: Suppliers, Consumables, Equipment, Storage Locations, Pre-Arrival Orders, Custody Ledger.
- **Relational Parent Dropdowns & Dynamic LOVs**: Live database dropdown menus for Cameras, Lenses, Films, Suppliers, Storage Locations, and Dynamic LOVs across all form dialogs.

### Fotologue Mobile
- **`LAB` Portal Entry Points**: Dedicated mobile screens for **`📦 Storage Locations`**, **`📋 Custody & Loans`**, and **`🚚 Pre-Arrival Orders`**.
- **`Gear Collection` Detail Cards**: Expandable **`[ Custody & Location ]`** card on camera, lens, and accessory detail screens.

### Synchronization Bridge (`fotologue_reference_sync_package.json`)
When `exportReferenceSyncPackageJson` is invoked on Desktop, all 13 categories—including Custom LOVs, Contacts, Storage Locations, Orders, and Custody Interactions—serialize cleanly into `fotologue_reference_sync_package.json` for full-fidelity ingestion by Mobile.

---

## 7. Lexicon & Terminology Reference Table

| Canonical Term | Rejected Anti-pattern / Synonym | Definition |
| :--- | :--- | :--- |
| **Fotologue Desktop** | Base Camp, CCC, Desktop Vault | Desktop JVM administration & darkroom management app. |
| **Fotologue Mobile** | Spoke, Field Device | Android exposure logging & field gear app. |
| **Dynamic LOV** | Rigid ENUM, Hardcoded Dropdown | User-extendable List of Values stored in SQLite. |
| **Reference Data** | Foundation Data, Master Data | Master picklists maintained on Desktop. |
| **Reference Sync Package** | Foundation Manifest, Sync Payload | Exported JSON payload (`fotologue_reference_sync_package.json`). |
| **Preset Catalogue** | Preset Catalog, Cloud Lookup | Pre-packaged developer specification library (Australian `-ue` spelling). |
| **Gear Collection** | Gear Library, Equipment Pool | Total sum of available hardware. |
| **Consumables** | Inventory, Materials | Expendable lab items (Chemistry, Paper). |
| **Lab Equipment** | Darkroom Gear, Apparatus | Non-consumable darkroom hardware (Enlargers, Tanks). |
