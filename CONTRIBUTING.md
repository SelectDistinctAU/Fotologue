# Contributing to Fotologue

Thank you for your interest in contributing to Fotologue! We are building a professional-grade, analogue & hybrid photography ecosystem. To ensure the project remains robust, cohesive, and aligned with our core values, please read the following guidelines before submitting a Pull Request.

## 1. Core Philosophy: Privacy-by-Design & Local-First

Fotologue is architected with a strict **Local-First, Offline-First** philosophy.
*   **No Third-Party Trackers:** Do not introduce analytics SDKs, crash reporters, or advertising modules (e.g., Firebase Analytics, Mixpanel, Flurry).
*   **User Sovereignty:** Data belongs to the photographer. Syncing and backups must rely entirely on local, user-controlled methods (e.g., ZIP archives, JSON sidecars, native SAF to NAS/Google Drive). Do not introduce proprietary cloud sync backends.
*   **On-Device Processing:** Any machine learning (such as face detection) must run 100% on-device.

## 2. Ubiquitous Language & Naming Conventions

To minimize cognitive load, we maintain a strict **Ubiquitous Language** across the codebase, database schemas, and UI copy.

Before naming a new variable, database table, or UI button, you **must** consult the [Fotologue Lexicon](docs/lexicon.md).
*   *Example:* We use `Fotologue Desktop` and `Fotologue Mobile`, never "CCC" or "Field Device".
*   *Example:* A photo is a `Frame`, never an "Exposure", "Shot", or "Log".
*   *Example:* A physical purchased bottle of developer is a `Stock Item`, never a "Stock Lot" or "Batch".

## 3. Language & Tone: Australian English

Fotologue is proudly Australian. All UI copy, documentation, and user-facing strings must be written in **Australian English (en-AU)**.
*   Use `-ise` instead of `-ize` (e.g., `Standardise`, `Synchronise`, `Digitise`).
*   Use `-our` instead of `-or` (e.g., `Colour`).
*   Keep the tone professional, warm, and geared toward photographers and archivists.

## 4. Development Workflow

1.  **Fork & Branch:** Create a feature branch from `main`. Use descriptive names (e.g., `feat/add-print-recipe-ui` or `fix/exiftool-path-resolution`).
2.  **Write Tests:** We maintain a high standard for test coverage. If you are modifying ViewModels or Data Repositories, please update or add corresponding Unit Tests.
3.  **Respect the Architecture:**
    *   **Mobile (`:app`, `:core:data`, `:feature:lab`):** UI is built with Jetpack Compose. Use Room for persistence. Use Hilt for Dependency Injection.
    *   **Desktop (`:desktop`):** UI is built with Compose Multiplatform. Use JetBrains Exposed and SQLite JDBC for persistence.

## 5. Pull Request Process

When submitting a PR:
*   Ensure `./gradlew test` passes locally.
*   Ensure `./gradlew assembleDebug` and `./gradlew :desktop:assemble` build cleanly.
*   Provide a clear summary of the problem and the proposed solution.
*   Include screenshots or screen recordings if the PR introduces UI changes.

Thank you for helping us build the ultimate command center for analogue photographers!
