# Autonomous Quality Assurance & Verification Protocol

This document establishes the **Autonomous Quality Assurance (QA) & Verification Protocol** for Fotologue, shifting quality verification away from user manual testing to automated, proactive agent inspection before task completion.

---

## 1. Why Visual & Wiring Bugs Escaped Unit Tests

Unit tests (`:app:testDebugUnitTest`, `:feature:lab:testDebugUnitTest`) verify isolated Kotlin ViewModel and Repository logic (*"Does seedPresets() invoke insertSupplier()?"*). They cannot detect:
1. **Unwired Navigation Lambdas**: An `onClick` lambda assigned to `{}` in navigation hosts (`MainActivity.kt`).
2. **Compose Desktop Focus Loss**: Re-evaluating DB queries during Compose recomposition that steals text field focus.
3. **Visual Label Overlap & Line Wrapping**: Long label strings wrapping inside empty `OutlinedTextField` borders.
4. **Un-hydrated Edit Form State**: Opening an edit form where relational cross-reference sets (`selectedAssociatedCameraIds`) default to `emptySet()`.

---

## 2. The 4-Step Autonomous Verification Protocol

Before declaring any feature or refactoring `COMPLETED`, the agent MUST execute the following 4-step verification sequence:

```
[ STEP 1: LOGIC & UNIT TESTING ] ──> [ STEP 2: WIRING & STATE HYDRATION ]
  ./gradlew testDebugUnitTest            Verify onClick lambdas in MainActivity.kt
  (58/58 Tests Passed)                   Verify Edit forms hydrate ALL relational IDs

               │                                       │
               ▼                                       ▼
[ STEP 3: RUNTIME DEVICE INSPECTION ] ──> [ STEP 4: VISUAL & LAYOUT AUDIT ]
  ADB / ui_state / take_screenshot        Verify labels use placeholder for examples
  Mechanically tap new buttons/cards      Verify 0 text emoji or line wrapping
```

### Protocol Checklist
1. **Wiring Verification**: Check that every new button, portal card, or navigation item is connected to an active route action in `MainActivity.kt` and `AppNavHost.kt`.
2. **Edit Form State Hydration**: Verify that clicking `[ ✏️ Edit ]` on any camera, lens, film, consumable, or equipment card loads its existing associated camera IDs, storage locations, and relational tags.
3. **Interactive Device Testing**: On Mobile, use ADB / `ui_state` / `take_screenshot` to tap new buttons and inspect rendered screen transitions.
4. **Layout & Focus Audit**: Verify labels use `placeholder` for long examples, and text fields on Desktop maintain smooth focus without recomposition popup interruptions.

---

## 3. High-Leverage Prompting Phrases for the User

You do NOT need to change your prompting style or perform manual QA. When requesting new features or changes, including any of these phrases will activate maximum verification rigor:

- **"Verify this end-to-end on the running device before finishing."**
  - Triggers ADB / `ui_state` / `take_screenshot` inspection of button taps and screen transitions.
- **"Test the Edit flow with pre-existing data."**
  - Triggers verification of relational state hydration when editing existing items.
- **"Check for UI overflow, clipping, or focus loss."**
  - Triggers verification of Compose Desktop text field focus and label lengths.

---

*Protocol established: March 2026 for Fotologue v3.0 Quality Assurance.*
