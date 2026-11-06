# 04. User Manual, Practical How-To Guides & Community Framework 📖🛠️

**Version:** 1.0.27 | **Author:** Gerard (`fotologue@pm.me` | `https://www.fotologue.au/`)

---

## 5W's Strategic Overview
* **WHO**: Active Fotologue users and community guide contributors.
* **WHAT**: Deep-dive practical guides for tagging, inventory, desktop export, and a community framework for writing how-tos.
* **WHY**: Empower users to master library organization and enable community members to author structured guides.
* **WHEN**: In-depth usage, setup, and desktop integration.
* **HOW**: Step-by-step modular walkthroughs with clear technical rationales.

---

## Guide 1: How to Set Up Your Own 3-Axis Tagging Taxonomy (And Why You Would)

### Why Set Up a 3-Axis Taxonomy?
Traditional photo tagging is flat and messy (*e.g., "beach, sunset, holiday, film, canon"*). As your library grows to thousands of negatives, flat tags break down.

Fotologue separates metadata into **3 distinct, orthogonal dimensions**:

1. **Subject (WHAT is the core domain?)**:
   * *Architecture, Landscape, Portrait, Street, Wildlife, Still Life*.
2. **Content (WHAT specific physical elements are in the frame?)**:
   * *Bridge, Building, Waterway, Skyline, Tree, Reflection, Vehicle*.
3. **Intent (WHY did you take this photograph?)**:
   * *Series (Urban Decay), Photo Book (Western Australia), Study (Light & Shadow), Commission, Personal Archive*.

### Step-by-Step Setup:
1. Open **`Subject, Content and Intent Tags`** from the main portal.
2. Select the **`Subject`** tab $\to$ tap **`+ Add Category`** (*e.g. Street Photography*).
3. Select the **`Content`** tab $\to$ add specific visual elements (*e.g. Neon Sign, Rain, Pavement*).
4. Select the **`Intent`** tab $\to$ add project goals (*e.g. Book: Night Walk 2026*).
5. When logging exposures or editing contact sheets, select one tag from each axis.

> **Result**: You can instantly query *"Show me all 'Neon Signs' (Content) in 'Street' (Subject) shot for 'Book: Night Walk 2026' (Intent)"* across 10 years of film negatives!

---

## Guide 2: How to Set Up Inventories, Suppliers & Barcode Auto-Fill

### Why Manage Physical Inventories?
Film stocks expire, liquid developers lose potency, and darkroom paper sheets run out. Tracking inventory prevents ruined rolls and unexpected chemical exhaustion.

### Step-by-Step Setup:
1. **Add Your Suppliers**:
   - Open **`People & Vendor Management`** $\to$ **`Suppliers`**.
   - Tap **`+`** to add local or online suppliers (*Walkens House of Film, B&H, Fitzgerald Photo, Rewind Lab*).
2. **Add Inventory Specification (`Add Inventory Item`)**:
   - Open **`Assets & Inventory`** $\to$ **`Master Inventory`** $\to$ tap **`+`**.
   - Tap the **Barcode Scanner Icon** (`R.drawable.barcode_scanner`) in the TopBar.
   - Scan the product barcode on your bottle or box (*e.g., UPC `019498905162` for Ilford Ilfosol 3 500ml*).
   - **Category, Manufacturer, Item Name, and Container Size auto-populate in 1 tap!**
3. **Log Physical Batches & Stock Lots (`Add Stock Item`)**:
   - Record purchase cost, batch/lot numbers, purchase date, and storage location (*Dry Cabinet, Fridge, Shelf A*).

---

## Guide 3: How to Export Metadata to Desktop DAMs (Lightroom, Capture One, digiKam)

### How Desktop Export Works:
Fotologue exports industry-standard **ISO 16684 XMP Sidecar files** (`.xmp`).

### Step-by-Step Export:
1. Open **`Archive Vault`** $\to$ **`Desktop Photo Library Exporter`**.
2. Select your roll or shot series.
3. Tap **`Export XMP Sidecars`**.
4. Transfer the generated `.xmp` files to your computer and place them in the same folder as your scanned RAW/TIFF/JPEG files.
5. In **Lightroom**, **Capture One**, or **digiKam**:
   - Select your scans $\to$ **`Metadata > Read Metadata from Files`**.
   - **EXIF parameters, 3-Axis hierarchical tags (`lr:hierarchicalSubject`), GPS geotags, and copyright auto-import instantly!**

---

## Guide 4: Community Authoring Framework — "How to Write a How-To"

We encourage community members to author guides for specialized photographic workflows (*e.g., Large Format Movements, C-41 Home Development, Darkroom Split-Grade Printing*).

### Use This 5-Step Structure for Community Guides:

1. **Title**: `How to [Achieve Specific Goal] in Fotologue`
2. **Why Do This?**: 2–3 sentences explaining the practical benefit for photographers.
3. **Prerequisites**: What equipment, film, or darkroom gear is required.
4. **Step-by-Step Walkthrough**: Numbered steps with screen navigation (*Portal > Module > Action*).
5. **Pro-Tip / Troubleshooting**: 1–2 practical field tips.

Submit your community guide to **`fotologue@pm.me`** to be featured in the official Fotologue documentation hub!
