# Fotologue: Target API Roadmap

This document outlines the strategy for targeting Android SDK versions to ensure compliance with Google Play requirements while maintaining application stability.

## Current Configuration (Release v1.0.14)
- **Compile SDK**: 37 (Android 17 Preview)
- **Target SDK**: 36 (Android 16)
- **Minimum SDK**: 24 (Android 7.0)

## Rational for SDK 37 Compilation
We compile against **SDK 37** to utilize the latest features of the **Material 3 Adaptive** and **Navigation 3** libraries. These libraries have strict compile-time requirements for the latest Android APIs to ensure proper layout behavior on foldables and multi-window environments.

## Targeting Strategy
As of September 2026, **Target SDK 36** meets the Google Play requirement (which typically requires targeting the previous year's major release by August 31st).

### Future Milestones:
- **Q4 2026**: Conduct stability testing on Android 17 (API 37) stable release.
- **Q1 2027**: Bump `targetSdk` to 37 once runtime behaviors are verified.
- **Ongoing**: Maintain **Min SDK 24** to support the widest range of physical analog photography equipment connected via older devices.

---
*Last Updated: September 2026 | Verified on Android Studio Quail 4 (2026.1.4)*
