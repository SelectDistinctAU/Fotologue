# Fotologue Design System & UI Principles

This document serves as the single source of truth for all UI, typography, colour, and layout decisions across **Fotologue Capture**, **Fotologue Lab**, and future modules.

---

## 1. Centralised Theme & Module Architecture (`:core:designsystem`)

All shared visual assets, colour tokens, typography scales, and reusable Composable components reside in the **`:core:designsystem`** library module.

### Core Token Standards
*   **Typography Tokens**: Always use `MaterialTheme.typography` scale (`titleLarge`, `titleMedium`, `bodyLarge`, `bodyMedium`, `labelSmall`). Never hardcode raw `fontSize = 16.sp` in feature screens.
*   **Colour Palette Tokens**: Use `MaterialTheme.colorScheme` (`primary`, `surface`, `surfaceVariant`, `onSurface`, `errorContainer`).
*   **Darkroom Safelight Theme**: When Safelight mode is active, the UI wraps in `Color.Black` (`#000000`) background and `Color(0xFFFF0000)` pure red text/icons to eliminate subpixel paper-fogging light.

---

## 2. Standard Component Patterns

### A. Top App Bars
*   Every top-level screen uses standard Material 3 `TopAppBar` or `CenterAlignedTopAppBar`.
*   Title text uses `MaterialTheme.typography.titleLarge`.
*   Left navigation icon uses standard `ic_arrow_back` or `ic_photo_camera` icon.
*   Action icons on the right use standard 24dp M3 `IconButton` actions (e.g. `ic_science`, `ic_handyman`, `ic_settings`).

### B. Cards & List Containers
*   Container cards use `CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)`.
*   Card padding is standardized at `16.dp` external padding, `12.dp` to `16.dp` internal content padding.
*   Item headers inside cards use `titleMedium.copy(fontWeight = FontWeight.Bold)`.
*   Subtitles and metadata use `bodySmall` with `color = MaterialTheme.colorScheme.onSurfaceVariant`.

### C. Action Buttons
*   Main action buttons use explicit labels with a single M3 vector icon on the left (`Icon(Icons.Default.Add)` + `Text("Master Item")`).
*   Never duplicate text pluses (`+ +`).
*   Button height is standardized at `48.dp` to `52.dp`.

### D. Single-Choice Inputs & Pick Lists
*   Long single-selection pick lists MUST use clean `ExposedDropdownMenuBox` dropdowns.
*   Never clutter screens with multi-row horizontal filter chips for long option lists.

---

## 3. Form & Field Labels
*   Form input fields use `OutlinedTextField` with clear, non-promotional label strings (e.g., `"Supplier Name"`, `"Item Name"`, `"Container Size"`).
*   Never use brand/vendor hints in text fields that look like advertisements.
*   Multiline text fields (Notes, Reflection) take full width with flexible height.
