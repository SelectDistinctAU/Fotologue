# Architectural Governance: Departures from Standard Android / Kotlin Best Practices

This document serves as the permanent **Architectural Governance Register** for Fotologue, recording every intentional departure from standard Android/Kotlin boilerplate, its rationale, impact, and user approval history.

---

## Governance Protocol for Future Departures

> [!IMPORTANT]
> **Governance Rule**:
> 1. **Alert Before Implementation**: Whenever a new feature or refactoring introduces a non-standard pattern or departure from standard Android/Kotlin/Gradle conventions, the developer MUST explicitly alert the user in the implementation plan.
> 2. **Impact Acknowledgment Requirement**: The departure MUST NOT be implemented until the user has explicitly acknowledged the impact and approved the decision.
> 3. **Governance Logging**: Every approved departure MUST be permanently logged in this governance register with date, rationale, impact, and approval status.

---

## Approved Architectural Governance Register

### Decision 001: Dynamic User-Extendable LOVs vs. Rigid Closed Kotlin Enums
- **Category**: Data Model & Schema Governance
- **Standard Convention**: Hardcode fixed `enum class` definitions in Kotlin for categories, mounts, types, and statuses.
- **Fotologue Departure**: Use dynamic, user-extendable database LOV tables (`reference_lovs` / `lab_lovs`) backed by on-the-fly auto-registration.
- **Domain Rationale**: Closed Enums lock analogue, hybrid, and darkroom photographers out of custom, historical, 3D-printed, or DIY equipment (*e.g., 19th-century wet-plate cameras, custom Bayonet VI filter threads*).
- **Impact & Trade-offs**: Requires runtime validation and database LOV lookup queries instead of static compile-time type safety.
- **Approval Status**: Approved (March 2026).

---

### Decision 002: Dual-Environment Architecture (Android Mobile + Compose Desktop JVM)
- **Category**: Module & Platform Architecture
- **Standard Convention**: Build a single Android mobile app targeting phones/tablets.
- **Fotologue Departure**: Maintain a shared multi-module architecture (`:core:data`, `:core:model`) serving both an Android Mobile app and a Compose Desktop JVM Command Center (`:desktop`).
- **Domain Rationale**: Analogue photographers use desktop PCs in studio darkrooms for sidecar file export, ExifTool metadata embedding, and master vault management.
- **Impact & Trade-offs**: Desktop JVM lacks Android-specific Context and Lifecycle APIs, requiring cross-platform state hoisting and desktop-compatible Compose UI components.
- **Approval Status**: Approved (March 2026).

---

### Decision 003: Decoupled 3-Pillar Architecture (Asset + Contact + Interaction)
- **Category**: Domain-Driven Design (DDD)
- **Standard Convention**: Tack commercial, loan, repair, and location columns directly onto camera/lens asset tables.
- **Fotologue Departure**: Decouple hardware assets (**Pillar 1: What**), contacts directory (**Pillar 2: Who**), and relationship transactions (**Pillar 3: Custody/Order Ledger**).
- **Domain Rationale**: Gear frequently moves between physical storage, hire houses, friends, repair technicians, and processing labs.
- **Impact & Trade-offs**: Requires relational cross-reference tables and joined queries rather than simple single-table CRUD.
- **Approval Status**: Approved (March 2026).

---

### Decision 004: Darkroom Red Safelight Mode Theme Override
- **Category**: UI / UX Theme System
- **Standard Convention**: Standard Light Mode (`MaterialTheme.colorScheme.surface`) and Dark Mode themes.
- **Fotologue Departure**: Provide a global **Darkroom Red Safelight Mode** override (`#FF0000` text/icons on `#000000` background).
- **Domain Rationale**: Protects light-sensitive silver-halide photographic paper from accidental light-fogging during enlarger printing and developing sessions.
- **Impact & Trade-offs**: Requires custom color mapping for text fields, borders, dropdowns, and buttons when Safelight Mode is active.
- **Approval Status**: Approved (March 2026).

---

### Decision 005: Privacy-First Native Android Contact Picker
- **Category**: Security & Privacy
- **Standard Convention**: Request `READ_CONTACTS` dangerous permission and query content resolver directly.
- **Fotologue Departure**: Use native `ActivityResultContracts.PickContact` system intent picker.
- **Domain Rationale**: Guarantees 100% local privacy—Fotologue never requests dangerous contacts permissions or harvests the user's address book.
- **Impact & Trade-offs**: Returns only the single selected contact ID and display name rather than continuous contact list indexing.
- **Approval Status**: Approved (March 2026).

---

### Decision 006: Clean Architecture Domain Use Case Layer for Complex Logic
- **Category**: Clean Architecture & Domain Layer Isolation
- **Standard Convention**: Couple complex calculations, color science, and process workflows directly inside ViewModels.
- **Fotologue Departure**: Extract non-trivial domain logic (Ansel Adams 11-Zone System calculations, wet darkroom chemical timing, $N \pm 1$ recommendations, enlarger print outcomes) into explicit **Domain Use Cases** (`core/data/src/main/java/com/fotologue/core/data/usecase/`).
- **Domain Rationale**: Preserves testability, isolates core optical/chemical color science from Android lifecycle components, and enables seamless reuse across Mobile (`:app`) and Desktop (`:desktop`).
- **Impact & Trade-offs**: Requires injecting Use Case classes into ViewModels via Hilt/Dagger rather than inline private functions.
- **Approval Status**: Approved (October 2026).

---

*Governance Register maintained for Fotologue v3.0.*
