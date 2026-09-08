# Fix Aperture Constraint Issue in Light Meter

The goal is to ensure that the Light Meter only suggests apertures that the current gear (Camera + Lens) is capable of. Specifically, we need to filter out apertures wider than the lens's maximum physical opening (smallest f-number).

## User Review Required

> [!NOTE]
> The `ExposureSuggester.suggestSettings` method will be renamed to `generateExposureSuggestions` to align with the requirements.
> The `LightMeterViewModel` will be refactored to use reactive loading for gear data, preventing race conditions where suggestions are calculated before the gear details are fetched from the database.

## Proposed Changes

### [Component: Utilities]

#### [MODIFY] [ExposureSuggester.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/util/ExposureSuggester.kt)
- Rename `suggestSettings` to `generateExposureSuggestions`.
- Verify and refine the aperture filtering logic:
  - Base list comes from `camera.apertures` or `commonApertures`.
  - Filter by `lens.minAperture..lens.maxAperture`.
  - Ensure `lens.minAperture` and `lens.maxAperture` are added back if missing from the base list, as the lens is the primary limiting factor.
  - Exclude any `fNumber < lens.minAperture` (wider than lens capability).

### [Component: ViewModel]

#### [MODIFY] [LightMeterViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/LightMeterViewModel.kt)
- Refactor gear loading using `StateFlow` to ensure consistency.
- Update `onEvCalculated` to call the renamed `generateExposureSuggestions`.
- Ensure that if gear is loaded after the first EV calculation, the suggestions are updated immediately.

## Verification Plan

### Automated Tests
- Create a unit test for `ExposureSuggester` to verify:
  - Apertures below `lens.minAperture` are excluded.
  - `lens.minAperture` is included even if not in `commonApertures`.
  - Camera constraints are respected but lens range takes precedence for physical limits.

### Manual Verification
- Open the Light Meter with a lens that has a `minAperture` of e.g. f/2.8.
- Verify that f/1.4 and f/2.0 are NOT suggested.
- Verify that f/2.8 IS suggested even if it's not a "standard" stop for that specific gear combination.
