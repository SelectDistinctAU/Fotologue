# Fotologue 📸🎞️

**Professional Exposure Log & Light Meter companion for Analog Photographers.**

Fotologue is a high-precision, privacy-first mobile application designed to bridge the technical gap between physical film photography and modern digital archiving. Built for speed in the field and accuracy in the lab, Fotologue replaces paper shot lists with a robust, metadata-rich logging ecosystem.

---

## ✨ Key Features

### 🛠️ The Wizard's Calibration
A specialized 2-stage hardware tuning tool designed to transform your smartphone into a precision instrument.
*   **Stage 1: The Void**: Measures the sensor "Noise Floor" with a covered lens to eliminate electronic "ghost light."
*   **Stage 2: The Balance**: Calibrates signal gain against an 18% gray card for industrial-grade accuracy.

### 📐 Relational Gear Library
Maintain a professional database of your entire arsenal.
*   **Cameras**: Support for 35mm, 120, and Half-Frame (e.g., Pentax 17) formats.
*   **Lenses**: Automated association between mounts and bodies; support for Prime and Zoom optics.
*   **Kits**: One-tap selection for your favorite camera/lens/film combinations.

### 🎞️ Live Session Tracking
Intelligent "Film Roll.Frame" management that mirrors your physical workflow.
*   **Sequential Suggestions**: Automatically predicts your next frame (1.01 → 1.02).
*   **End-of-Roll Alerts**: Proactively offers to start a new roll once you reach the physical limit.
*   **Visual Simulations**: Pre-visualize your shots with B&W, C-41, and E-6 film look previews in the meter.

### 📊 Professional Meta-Integrity
Export your data directly into your professional post-processing workflow.
*   **ISO 16684 (XMP)**: Generates industry-standard sidecar files for Adobe Lightroom and Capture One.
*   **Export Badges**: Visual tracking in the Timeline (`[Z]`, `[C]`, `[J]`) to show archived status.
*   **Interactive Maps**: One-tap navigation to your shoot locations via Google Maps.

---

## 🔒 Privacy & Security

**Privacy is the core of Fotologue's architecture.**
*   **100% Local**: No accounts, no cloud syncing, and no external tracking.
*   **On-Device Intelligence**: Face-aware tagging uses on-device ML; no biometric data ever leaves the device.
*   **Transient Metadata**: Your location and shooting data belong to you. Exports are handled via standard Android SAF (Storage Access Framework).

---

## 🏗️ Technical Stack

*   **Language**: Kotlin 2.2+
*   **UI Framework**: Jetpack Compose (Material 3 Adaptive V2)
*   **Architecture**: MVVM with Hilt Dependency Injection
*   **Persistence**: Room DB (Version 26+) with DataStore for preferences
*   **Compliance**: Android 15 Edge-to-Edge & IME optimized

---

## 🧪 Quality Assurance

Fotologue is built for reliability.
*   **Unit Tests**: 44+ core logic tests covering calibration math and session management.
*   **Verification**: Tested on Pixel 8a and modern system emulators.
*   **Test Plan**: See the [Testing Strategy](.artifacts/testing_strategy.artifact.md) for detailed QA scripts.

---

## 🚀 Getting Started for Testers

1.  **Clone the Repository**: `git clone https://github.com/[your-repo]/Fotologue.git`
2.  **Open in Android Studio**: Use the Latest Ladybug/Meerkat version.
3.  **Build**: Run `:app:assembleDebug`.
4.  **Calibrate**: Use the "Wizard" under Settings before your first shoot for maximum accuracy.

---

*Developed with 20 years of collaboration and a commitment to professional photography standards.*
