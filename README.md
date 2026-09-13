# Fotologue 📸🎞️

**Professional Exposure Log & Light Meter companion for Analog Photographers.**

Fotologue is a high-precision, privacy-first mobile application designed to bridge the technical gap between physical film photography and modern digital archiving. Built for speed in the field and accuracy in the lab, Fotologue replaces paper shot lists with a robust, metadata-rich logging ecosystem.

---

## ✨ Key Features

### 🛠️ The Wizard's Calibration
A specialized 2-stage hardware tuning tool designed to transform your smartphone into a precision instrument.
*   **Stage 1: The Void**: Measures the sensor "Noise Floor" with a covered lens to eliminate electronic "ghost light."
*   **Stage 2: The Balance**: Calibrates signal gain against an 18% gray card for industrial-grade accuracy.
*   **Hardware Baseline**: Transparent monitoring of "Noise Floor" units and "Signal Gain" multipliers.

### 📐 Relational Gear Library
Maintain a professional database of your entire arsenal with intelligent automation.
*   **Cameras**: Full support for 35mm, 120 (Medium Format), and Half-Frame (e.g., Pentax 17) formats, plus modular gear accessories like interchangeable Viewfinders.
*   **Lenses**: Automated association between mounts and bodies; support for Prime and Zoom optics with focal length tracking.
*   **Film Stocks**: Manage ISO and expected exposures (12, 24, 36) per stock.
*   **Smart Kits**: One-tap selection for your favorite camera/lens/film combinations to speed up fieldwork.

### 🎞️ Live Session Tracking
Intelligent "Film Roll.Frame" management that mirrors your physical workflow.
*   **Sequential Suggestions**: Automatically predicts your next frame (e.g., 1.01 → 1.02).
*   **Active Roll Counter**: Dedicated management screen to increment/decrement frames or reset roll counts without editing history.
*   **End-of-Roll Alerts**: Proactively offers to start a new roll once you reach the physical limit (with buffer support).
*   **Visual Simulations**: Pre-visualize your shots with B&W, C-41 (Color Negative), and E-6 (Slide) film look previews in the meter.

### 📊 Professional Meta-Integrity & Export
Export your data directly into your professional post-processing workflow (Lightroom/Capture One).
*   **ISO 16684 (XMP)**: Generates industry-standard sidecar files with Creator, Copyright, and Contact metadata.
*   **Hierarchical Tagging**: Professional Subject > Content system (e.g., Nature > Wildlife) designed for desktop software organization.
*   **Export Awareness**: Visual tracking in the Timeline (`[Z]`, `[C]`, `[J]`) to show exactly which logs have been archived.
*   **Interactive Maps**: One-tap navigation to your shoot locations via Google Maps integration.

---

## 🔒 Privacy & Security

**Privacy is the core of Fotologue's architecture.**
*   **100% Local-First**: No accounts, no cloud syncing, and no external tracking. Your data never leaves your device.
*   **On-Device Intelligence**: Smart Face-Aware Tagging uses on-device ML; biometric data is processed transiently and never stored.
*   **User Sovereignty**: All shooting data, locations, and notes belong to the user. System access is handled via standard Android Storage Access Framework (SAF).

---

## 🌍 Global Presence
Fotologue is localized for the international photography community, supporting 8 languages:
*   English (Primary)
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
*   **Target SDK**: 36 (Android 16) — meets Google Play's 31 Aug 2026 requirement; see [Target API Roadmap](docs/target-api-roadmap.md)
*   **UI Framework**: Jetpack Compose (Material 3 Adaptive V2)
*   **Architecture**: MVVM Pattern with Hilt Dependency Injection
*   **Persistence**: Room Database (Schema Version 26) with DataStore for typed preferences
*   **Compliance**: Optimized for Android 15+ Edge-to-Edge and IME insets.

---

## 🧪 Quality Assurance

Fotologue is built to professional engineering standards.
*   **Unit Tests**: 44+ core logic tests covering calibration math, session management, and metadata integrity.
*   **Verification**: Field-tested on Pixel 8a and modern system emulators.
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
