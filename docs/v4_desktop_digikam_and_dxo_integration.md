# Fotologue V4 Architecture & Workflow: Desktop, DAM & RAW Processor Integration

## 1. Executive Summary & The 3-Pillar Ecosystem

Fotologue operates as part of a **subscription-free, local-first, professional hybrid photography stack**. Rather than attempting to replace professional desktop RAW processors or enterprise digital asset managers (DAMs), Fotologue provides the **missing analogue metadata bridge** that connects in-situ field exposure logging, darkroom inventory supply-chain tracking, and print reflection logs directly to any desktop imaging software.

```
 📱 FOTOLOGUE MOBILE              🖥️ RAW SCAN PROCESSOR            🏛️ DAM / FOTOLOGUE DESKTOP
 ────────────────────             ──────────────────────           ──────────────────────────────
  • In-situ Field Logging          (DxO PhotoLab / Lightroom /      • Master Archival DAM
  • Pro Light Metering             Capture One / FilmLab /          • Hierarchical Tag Search
  • Darkroom Sessions              Darktable / Negative Lab Pro)    • GPS Map & Face Indexing
  • Stock Lot & Chemistry         • Macro Scan Inversion & Cleanup  • Native SQLite / XMP Vault
           │                               │                                 │
           └───────────────────────────────┴─────────────────────────────────┘
                                           │
                           UNIVERSAL GLUE: ISO 16684 XMP
                           (`_DSF1024.xmp` / `IMG_1024.xmp`)
```

---

## 2. Role Division in the 3-Pillar Stack

### Pillar 1: Fotologue Mobile (Agile Field & Darkroom Capture)
- **Field**: 1-tap exposure logging, calibrated spot/centre-weighted light metering, 3-Axis Tag Taxonomy (`Subject • Content • Intent`), and Smart Gear Kits.
- **Lab**: Darkroom Safelight Mode, stock lot inventory supply chain (*`IN_TRANSIT` $\to$ `ON_HAND` $\to$ `IN_USE` $\to$ `EXHAUSTED`*), active vendor directory with Google Maps store pins, B&W / C-41 / E-6 development countdown timers, and enlarger printing reflection logs (*`Keeper`* vs *`Reject`* with Photographer Learning tags).

### Pillar 2: Vendor-Neutral RAW Scan Processor (Inversion & Optical Cleanup)
- **Compatible Engines**: DxO PhotoLab, Adobe Lightroom / Camera Raw, Capture One, FilmLab, Darktable, Negative Lab Pro, Affinity Photo, or RawTherapee.
- **Macro Scan Processing**: Digital camera macro scan inversion (`.RAF`, `.CR3`, `.NEF`, `.ARW`, `.DNG`).
- **Optical & Noise Correction**: Industrial-grade noise reduction, lens softness correction, distortion removal, and dust/scratch cleanup.

### Pillar 3: Open DAM (digiKam / Lightroom) + Fotologue Desktop
- **Open-Source Enterprise DAM**: Museum-grade C++/Qt Digital Asset Manager (e.g. digiKam) operating over local SSDs and mounted NAS shares (`//NAS/FilmArchive/`).
- **Hierarchical Searching**: Deep searching across 3-Axis tag trees, creator copyright, GPS map coordinates, and face region tags.
- **Local-First Sovereignty**: 100% offline, zero-subscription data sovereignty powered by SQLite and open ISO 16684 XMP sidecars.

---

## 3. Universal Glue: ISO 16684 XMP Sidecar Specification

To ensure seamless interoperability between any RAW scan processor, DAM, and Fotologue, Fotologue generates open, standard ISO 16684 XMP sidecar files (`_DSF1024.xmp`) that sit alongside RAW camera scan files.

### Standard & Custom Namespaces Used:
- **Dublin Core (`dc:`)**: Creator copyright, title, description, and rights.
- **Lightroom Hierarchical Subject (`lr:`)**: Tree-structured 3-Axis Tag taxonomy (`Subject/Architecture`, `Content/Medium Format/6x6`, `Intent/Fine Art Exhibition`).
- **Custom Fotologue Namespace (`fotologue:`)**: Preserves darkroom chemistry, dilution, development time, enlarger lens aperture, and paper grade filter notes without data loss.

### Example Generated XMP Sidecar (`_DSF1024.xmp`):

```xml
<x:xmpmeta xmlns:x="adobe:ns:meta/">
  <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <rdf:Description rdf:about=""
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:lr="http://ns.adobe.com/lightroom/1.0/"
      xmlns:photoshop="http://ns.adobe.com/photoshop/1.0/"
      xmlns:exif="http://ns.adobe.com/exif/1.0/"
      xmlns:fotologue="http://fotologue.app/xmp/1.0/">
      
      <!-- Creator Copyright & Summary -->
      <dc:creator><rdf:Seq><rdf:li>Gerard Bendotti</rdf:li></rdf:Seq></dc:creator>
      <dc:rights><rdf:Alt><rdf:li xml:lang="x-default">© 2026 Gerard Bendotti. All Rights Reserved.</rdf:li></rdf:Alt></dc:rights>
      <dc:description>Yashica Mat-124G + Yashinon 80mm f/3.5 • Ilford HP5 Plus 400 • Dev: D-76 (1+1 @ 20.0°C)</dc:description>
      
      <!-- 3-Axis Tag Taxonomy Tree -->
      <lr:hierarchicalSubject>
        <rdf:Bag>
          <rdf:li>Subject|Architecture|Urban Heritage</rdf:li>
          <rdf:li>Content|Medium Format|6x6</rdf:li>
          <rdf:li>Intent|Fine Art Exhibition|2026 Series</rdf:li>
        </rdf:Bag>
      </lr:hierarchicalSubject>
      
      <!-- Custom Fotologue Darkroom & Field Lineage -->
      <fotologue:camera>Yashica Mat-124G</fotologue:camera>
      <fotologue:lens>Yashinon 80mm f/3.5</fotologue:lens>
      <fotologue:filmStock>Ilford HP5 Plus 400</fotologue:filmStock>
      <fotologue:filmRoll>Roll 01</fotologue:filmRoll>
      <fotologue:physicalSerial>YSH-HP5-R01</fotologue:physicalSerial>
      <fotologue:developer>Kodak D-76</fotologue:developer>
      <fotologue:dilution>1+1</fotologue:dilution>
      <fotologue:developmentTemp>20.0°C</fotologue:developmentTemp>
      <fotologue:enlargerLens>Rodagon 80mm f/5.6</fotologue:enlargerLens>
      <fotologue:paperStock>Multigrade RC Deluxe Satin 8x10</fotologue:paperStock>
      <fotologue:gradeFilter>Grade 2.5</fotologue:gradeFilter>
    </rdf:Description>
  </rdf:RDF>
</x:xmpmeta>
```

---

## 4. Solving the "Partial Export / Multiple ZIPs" Problem

Exporting partial ZIP files while shooting a roll in progress often creates messy, fragmented archive folders. Fotologue solves this via a **Canonical Living JSON Vault & Idempotent Merging Engine**:

### 1. Single Living JSON Vault (`backup_YYYYMMDD_HHMM.json`)
Instead of partial ZIPs, Fotologue exports a **single, full-fidelity relational database graph**.

### 2. Idempotent Merging Engine (UPSERT)
When importing a partial or field-updated JSON backup into a master desktop catalogue, Fotologue’s `restore()` engine performs an **UPSERT (Insert or Update on Conflict)**:
- Existing frames (e.g. frames 1..10) are updated with any new darkroom development or print reflection notes.
- Newly shot frames (e.g. frames 11..24) are appended to the **exact same physical roll**.
- Zero duplicate rolls, zero split ZIP clutter!

---

## 5. Physical Film Roll Serial Labeling Protocol

To eliminate ambiguity when shooting multiple "Roll 1" canisters simultaneously across different camera bodies (*e.g., Roll 1 in a Pentax ME Super vs Roll 1 in a Canon AE-1*), Fotologue generates a unique physical serial label:

$$\text{[Camera Body]} \bullet \text{[Film Stock]} \bullet \text{[Roll Number]} \quad (\text{e.g. } \text{Pentax ME Super} \bullet \text{Kodak Tri-X 400} \bullet \text{Roll 01})$$

Photographers physically write this unique serial identifier (*e.g. `PNTX-TX400-R01`*) on canister leader tape, film canisters, or negative binder sleeves. This serial matches 1-to-1 across Field logs, Lab processing sessions, Station contact sheets, and DAM XMP sidecars.

---

## 6. Desktop Integration Options & Photographer Choices

As a photographer, you have **3 clear, flexible architectural options** for how Fotologue operates on your desktop:

### Option A: Mobile App + SAF Direct NAS Bridge (Zero Desktop Setup Needed)
- **How it works**: Use Fotologue on your phone/tablet in the Field and Darkroom. Use the **Archive Vault & Desktop Bridge** (`ArchiveScreen.kt`) to dispatch backups, CSV logs, and XMP sidecars directly to your mounted NAS share (`//NAS/FilmArchive/`) or Proton Drive / Google Drive folder.
- **DAM & RAW Software**: Open the NAS folder in digiKam, DxO PhotoLab, Lightroom, or Capture One. The sidecars automatically populate all metadata.

### Option B: Native Compose for Desktop App (Kotlin Multiplatform / JVM)
- **How it works**: Run a standalone **Fotologue Desktop Application** (`:desktop` module) on Windows, macOS, or Linux.
- **Desktop Features**:
  - Full-screen desktop dashboard for reviewing exposure logs, darkroom inventories, and contact sheets.
  - Direct local hard drive & NAS folder access without SAF permission prompts.
  - Drag-and-drop RAW macro scan files directly onto digital contact sheets to auto-generate matching XMP sidecars.

### Option C: Automated Background NAS Sync Service
- **How it works**: Fotologue Desktop runs as a lightweight background service alongside digiKam or Lightroom. Whenever you copy new RAW macro scan files into your NAS archive folder, Fotologue automatically detects them and generates matching XMP sidecars in real-time.
