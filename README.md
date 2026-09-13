# Fotologue 📸🎞️

**Version:** 1.0.17 (Build 18) | **Release Date:** September 2024 | **Audience:** Developers, Beta Reviewers & Testers

**Professional Exposure Log & Light Meter companion for Analog Photographers.**

Fotologue is a high-precision, privacy-first mobile application designed to bridge the technical gap between physical film photography and modern digital archiving. Built for speed in the field and accuracy in the lab, Fotologue replaces paper shot lists with a robust, metadata-rich logging ecosystem.

---

## ✨ Key Features

### 🛠️ The Wizard's Calibration
A specialized 2-stage hardware tuning tool designed to transform your smartphone into a precision instrument.
*   **Stage 1: The Void**: Measures the sensor "Noise Floor" with a covered lens to eliminate electronic "ghost light."
*   **Stage 2: The Balance**: Calibrates signal gain against an 18% grey card for industrial-grade accuracy.
*   **Hardware Baseline**: Transparent monitoring of "Noise Floor" units and "Signal Gain" multipliers.

### 📐 Relational Gear Library & 3-Axis Taxonomy
Maintain a professional database of your entire arsenal with intelligent camera cross-referencing and live category badges.
*   **Cameras**: Full support for 35mm, 120 (Medium Format), Half-Frame (e.g., Pentax 17), Large Format, and TLR (e.g. Yashica Mat-124G) bodies.
*   **Fixed & Interchangeable Lenses**: Automatic fixed-lens auto-creation and cross-referencing for TLR and compact cameras. Full camera cross-referencing for Lenses, Viewfinders, Teleconverters, Filters, and Flashes.
*   **Film Stocks**: Manage ISO and expected exposures (12, 24, 36) per stock with 35mm half-frame doubling.
*   **3-Axis Tag Taxonomy (Subject • Content • Intent)**: Categorise shots by domain (*Subject*), detail (*Content*), and creative purpose (*Intent*: *Study*, *Series*, *Documentation*, *Commission*).
*   **Smart Kits**: Pre-configure favourite camera/lens/film/accessory combinations for one-tap quick fill during fieldwork.

### 🎞️ Live Session Tracking & Digital Contact Sheet
Intelligent "Film Roll.Frame" management and visual scene-matching for your cataloguing pipeline.
*   **Digital Contact Sheet View**: Interactive grid view of all frame thumbnails organised by film roll (6 columns for 35mm, 3 for 120 format).
*   **In-Grid Frame Gap Adjuster**: Interactively insert or remove frame gaps (+1 / -1 offset) to keep the contact sheet 1-to-1 aligned with physical film rebate numbers.
*   **Printable PDF Proof Sheet Exporter**: Generates styled A4 PDF contact proof sheets ready for printing or digital archival binders.
*   **Active Roll Counter**: Dedicated management screen to increment/decrement frames, set active frame numbers, or reset roll counts.
*   **Visual Simulations**: Pre-visualise your shots with B&W, C-41 (Colour Negative), and E-6 (Slide) film look previews in the meter.

### 📊 Professional Meta-Integrity & Camera Copy Exporter
Export your data directly into your professional post-processing workflow (Lightroom/Capture One).
*   **ISO 16684 (XMP)**: Generates industry-standard sidecar files with Creator, Copyright, Contact metadata, and `Intent|Study` / `Intent|Series` tag hierarchies.
*   **Camera Macro Copy Presets & Scanner Matching**: Auto-names exported `.xmp` sidecar files (e.g. `_DSF1024.xmp`) to match RAW scan file sequences from Fujifilm, Canon, Nikon, Sony, Leica, or flatbed scanners. Save custom presets in Settings for one-tap loading.
*   **Export Awareness**: Visual tracking in the Timeline (`[Z]`, `[C]`, `[J]`, `[X]`) to show exactly which logs have been archived.
*   **Interactive Maps**: One-tap navigation to your shoot locations via Google Maps integration.

---

## 🔒 Privacy & Security

**Privacy is the core of Fotologue's architecture.** See our full [Privacy Policy](PRIVACY_POLICY.md) for complete details.
*   **100% Local-First**: No accounts, no cloud syncing, and no external tracking. Your data never leaves your device.
*   **On-Device Intelligence**: Smart Face-Aware Tagging uses on-device ML; biometric data is processed transiently and never stored or transmitted.
*   **User Sovereignty**: All shooting data, locations, and notes belong to the user. System access is handled via standard Android Storage Access Framework (SAF).

---

## 🌍 Global Presence
Fotologue is localised for the international photography community, supporting 8 languages:
*   English (Australia / Primary)
*   German (Deutsch)
*   Spanish (Español)
*   French (Français)
*   Italian (Italiano)
*   Japanese (日本語)
*   Portuguese (Português - Brasil)
*   Chinese (Simplified - 简体中文)

---

## 🏗️ Technical Stack

*   **Language**: Kotlin 2.4.20
*   **Compile SDK**: 37 (Android 17) — required for latest Material You assets and system fonts
*   **Target SDK**: 36 (Android 16) — meets Google Play's requirement; see [Target API Roadmap](docs/target-api-roadmap.md)
*   **UI Framework**: Jetpack Compose (Material 3 Adaptive V2)
*   **Architecture**: MVVM Pattern with Hilt Dependency Injection
*   **Persistence**: Room Database (Schema Version 36) with DataStore for typed preferences
*   **Compliance**: Optimised for Android 15+ Edge-to-Edge and IME insets.

---

## 🧪 Quality Assurance

Fotologue is built to professional engineering standards.
*   **Unit Tests**: 58 core logic unit tests covering calibration math, session management, camera-gear cross-referencing, macro copy preset formatting, PDF proof sheet generation, and metadata integrity.
*   **Verification**: Field-tested on Pixel devices and modern system emulators.
*   **Test Plan**: See the [Testing Strategy](docs/testing_strategy.md) for detailed QA scripts and manual verification steps. 

---

## 🚀 Getting Started for Testers

1.  **Clone the Repository**: `git clone https://github.com/[your-repo]/Fotologue.git`
2.  **Environment**: Open in Android Studio (Quail 2026.1.4 or later).
3.  **Build**: Run `./gradlew :app:assembleDebug`. 
    *Note: Requires compileSdk 37. Android 16 / API 36 is targeted; see [Target API Roadmap](docs/target-api-roadmap.md).*
4.  **Initial Setup**: Use the "Pro Calibration Wizard" under Settings before your first shoot for maximum metering accuracy.

---

*Developed with a commitment to professional high-fidelity photography standards and long-term technical collaboration.*
