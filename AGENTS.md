# AGENTS.md — Universal Rules for AI Assistants
(Honor-and-verify: Project context overrides global defaults.)

## 1. Environment & Dependency Policy
- **Verify, Don't Guess**: Read ACTUAL versions from `gradle/libs.versions.toml` and `build.gradle.kts` before proposing changes.
- **Stability First**: Use stable, production-ready APIs. State uncertainty explicitly if an API cannot be verified in the current `compileSdk`.
- **Version Catalog**: All dependencies must live in `libs.versions.toml`. No hardcoded version strings.

## 2. Architecture Integrity
- **Respect Existing Patterns**: Do NOT refactor the project structure (e.g., Single-module to Multi-module) or switch core frameworks (e.g., View-based to Compose, or Koin to Hilt) without a specific architectural mandate.
- **MVVM/Clean**: Follow ViewModel + Repository patterns. Keep ViewModels free of Android framework dependencies.
- **Visibility**: Implementation details MUST be `internal`. Expose only necessary abstractions via `interface`.

## 3. Mandatory Verification Loop
Every change follows this cycle:
1. **Build**: `./gradlew assembleDebug` must pass.
2. **Quality**: `./gradlew lint` must be clean. 
3. **Tests**: Run existing tests (`./gradlew test`) before claiming "done."
4. **Report**: Explicitly list which tasks passed and which failed.

## 4. Documentation & Review
- **KDoc**: Document public interfaces and complex logic (explain WHY, not WHAT).
- **Small Diffs**: Produce focused, reviewable changes. Propose a plan for sweeping modifications.
