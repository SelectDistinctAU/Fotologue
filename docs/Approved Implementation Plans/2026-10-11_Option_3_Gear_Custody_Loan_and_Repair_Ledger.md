# Option 3: Gear Custody, Loan & Repair Ledger Polish Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 04:30 AM UTC

---

This plan finalizes **Option 3 (Gear Custody, Loan & Repair Ledger)** across Mobile and Desktop by enforcing **WCAG 2.1 AA 48dp touch targets**, explicit TalkBack accessibility semantics, and 1-tap interaction logging for gear loans, repair tracking, pre-arrival orders, and storage location assignments.

---

## 1. Architectural & Technical Specifications

```
┌─────────────────────────────────────────────────────────────┐
│ CUSTODY LEDGER: ASSET + CONTACT + LOCATION (3-PILLAR)      │
│ 📸 Pentax LX ➞ 👤 Repair Tech (Service / Repair)            │
│ Status: Active • Expected Return: Oct 25 • Location: Lab    │
├─────────────────────────────────────────────────────────────┤
│ PRE-ARRIVAL ORDERS                                          │
│ 🚚 Hasselblad 503CX ➞ Vendor: B&H Photo                     │
│ Status: In Transit • Tracking: #TRK9021 • Storage: Cabinet A│
└─────────────────────────────────────────────────────────────┘
```

### Key Functional Areas:
1. **Gear Loans, Rentals & Repair Tracking (`CustodyLedgerScreen.kt`)**:
   - Log gear loans out to friends, rentals in from suppliers, and service/repair dispatches to repair technicians.
   - Track expected return dates, active vs returned status, and assigned storage locations.
2. **Pre-Arrival Order Tracking (`PreArrivalOrdersScreen.kt`)**:
   - Track gear purchase orders and pre-arrival shipment statuses (*Waitlisted*, *Packaging*, *In Transit*, *Delivered*).
   - 1-tap **`[ Mark Delivered ]`** action automatically moves delivered assets into assigned Storage Locations.
3. **Storage Locations Hierarchy (`StorageLocationsScreen.kt`)**:
   - Manage physical storage cabinets, shelves, dry boxes, and darkroom vaults (`Cabinet`, `Shelf`, `Dry Box`, `Vault`).
4. **Accessibility (WCAG 2.1 AA)**:
   - Enforce $\ge 48\text{dp} \times 48\text{dp}$ touch targets on all edit/delete/deliver buttons.
   - Complete TalkBack semantic descriptions on custody cards.

---

## 2. Proposed Code Changes

- **[MODIFY]** `feature/lab/src/main/java/com/fotologue/feature/lab/CustodyLedgerScreen.kt`
  - Enforce 48dp touch targets and TalkBack semantics.
- **[MODIFY]** `feature/lab/src/main/java/com/fotologue/feature/lab/PreArrivalOrdersScreen.kt`
  - Enforce 48dp touch targets and TalkBack semantics.
- **[MODIFY]** `feature/lab/src/main/java/com/fotologue/feature/lab/StorageLocationsScreen.kt`
  - Enforce 48dp touch targets and TalkBack semantics.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `LAB` $\to$ navigate to `Custody Ledger`.
3. Log a **Service / Repair** interaction for a camera body $\to$ verify status renders cleanly with expected return date.
4. Open `Pre-Arrival Orders` $\to$ tap **`Mark Delivered`** on a pending shipment $\to$ verify asset moves to designated storage location!
