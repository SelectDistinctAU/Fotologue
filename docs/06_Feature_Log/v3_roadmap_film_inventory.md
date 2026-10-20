# Fotologue v3.0 Darkroom & Lab Inventory Roadmap 🎞️📦🧪

**Version:** 1.0.17 (Build 18) | **Last Updated:** September 2026 | **Audience:** Product Strategy & Core Engineering Team

---

## Executive Summary

Fotologue v3.0 expands beyond camera gear and shot logging into a **Full Analogue Darkroom & Lab Inventory Suite**. Photographers can manage physical film rolls through their lifecycle, track darkroom printing paper stocks, and monitor processing chemistry capacity and expiration dates.

---

## 1. Physical Film Roll Lifecycle States

Tracks rolls from fridge storage to darkroom archive:

```
┌──────────────┐     Load into     ┌──────────────┐     Finish      ┌──────────────┐
│  1. IN STOCK │  ──────────────►  │  2. LOADED   │  ────────────►  │  3. EXPOSED  │
│ (Fridge/Bag) │                   │  (In Camera) │    Shooting     │ (In Bag/Lab) │
└──────────────┘                   └──────────────┘                 └──────┬───────┘
                                                                           │
┌──────────────┐                  Link to Contact                   Develop│
│ 5. ARCHIVED  │  ◄─────────────  Sheet & XMP Export  ◄────────────  / Scan│
│  (In Sleeve) │                                                    └──────┘
└──────────────┘
```

1. **`IN_STOCK` (Storage / Fridge)**:
   * Track quantity, purchase date, storage location (*Fridge*, *Camera Bag*, *Shelf*), and box expiration date.
   * Expired Film Helper: Auto-calculates ISO compensation for vintage film (+1 stop per decade).
2. **`LOADED` (In Camera)**:
   * Maps a specific physical roll to a camera body (*"Roll #04 loaded in Hasselblad 500C/M"*).
   * *"What's Loaded Where?"* Dashboard Widget shows active film and frame count for every camera at a glance.
3. **`EXPOSED` (Awaiting Lab / Dev)**:
   * Finished rolls marked as exposed. Helps photographers track rolls sitting in their camera bag awaiting lab drop-off.
4. **`IN_PROCESSING` (At Lab / Home Dev)**:
   * Tracks lab name, order date, tracking number, or home dev batch (*C-41 Batch #12*).
5. **`ARCHIVED` (Scanned & Sleeved)**:
   * Linked directly to Fotologue's **Digital Contact Sheet** and XMP export pipeline.

---

## 2. Darkroom Printing Paper Inventory

Track photo paper boxes, sheet counts, and surface characteristics for silver-gelatin printing:

* **Paper Specs**:
  * **Brand & Type**: *Ilford Multigrade FB Glossy*, *Foma Fomabrom*, *Kodak Polycontrast*, *Adox MCC*.
  * **Sheet Dimensions**: `5x7"`, `8x10"`, `11x14"`, `16x20"`, `20x24"`, or custom rolls.
  * **Surface Finish**: Glossy, Pearl, Semi-Matte, Matt, Fiber Base (FB), Resin Coated (RC).
  * **Base Tone**: Warmtone, Neutral, Cooltone.
* **Sheet Quantity Counter**:
  * Decrement remaining sheets when printing in the darkroom.
  * Low paper alerts when box falls below threshold (e.g. *"Only 4 sheets of 11x14 FB Glossy left"*).

---

## 3. Processing Chemistry & Solution Tracker

Manage film and paper developers, stop bath, fixer, and archival clearing agents:

* **Chemistry Categories**:
  * **Film Developers**: *Kodak D-76*, *Rodinal / R09*, *Ilford ID-11*, *Cinestill C-41 / E-6*.
  * **Paper Developers**: *Ilford Multigrade Developer*, *Kodak Dektol*, *Adox Neutol*.
  * **Fixer & Stop Bath**: *Ilford Rapid Fixer*, *Kodak Indicator Stop Bath*, *Hypo Clearing Agent*, *Photo-Flo / Wetting Agent*.
* **Dilution & Mix Ratios**:
  * Stock vs. Working Solution ratios (`1+1`, `1+3`, `1+25`, `1+50`, `One-Shot`).
* **Capacity & Reuse Counter**:
  * Tracks used working solution capacity (e.g., *"1L Fixer used for 8 of 12 rolls"*).
  * Mix date and expiration warnings for working solutions prone to oxidation (*C-41 developer*, *D-76 Working Solution*).

---

## 4. Smart Barcode & Box Scanner

* **Barcode Scanner**: Use the phone's camera to scan standard 12D/UPC barcodes on film boxes, paper boxes, and chemical bottles (Kodak, Ilford, Fujifilm, Foma, CineStill).
* **Auto-Populate**: Automatically populates Name, Manufacturer, Native ISO/Grade, Format, and Sheet/Exposure counts into your inventory.

---

## 5. Integration with Existing v1.0 / v2.0 Features

* **3-Axis Taxonomy**: When a roll transitions from `EXPOSED` to `ARCHIVED`, all attached *Subject*, *Content*, and *Intent* tags (*Series > Urban Decay*) flow seamlessly into the Digital Contact Sheet and XMP sidecars.
* **v2.0 Bluetooth Trigger (`Fotologue Trigger`)**: Firing the hotshoe trigger on a camera marked as `LOADED` automatically updates the active roll's frame counter and GPS timeline.
