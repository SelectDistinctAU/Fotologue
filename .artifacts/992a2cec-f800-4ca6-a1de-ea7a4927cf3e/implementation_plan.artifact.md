# Implementation Plan - Refine Lens Lookup and AddLogScreen

This plan addresses user feedback regarding missing Lens selection in phone layout and refining the lens lookup mechanism using model numbers.

## Proposed Changes

### Data Layer

#### [MODIFY] [Lens.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/Lens.kt)
- Add `modelNumber: String` field to the `Lens` entity.

#### [MODIFY] [LensSpec.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/LensSpec.kt)
- Add `modelNumber: String` field to the `LensSpec` data class.

#### [MODIFY] [LensDatabase.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/LensDatabase.kt)
- Add `modelNumber` to all existing entries.
- Add new lenses:
    - `EF100400LIS` (Canon EF 100-400mm f/4.5-5.6L IS USM)
    - `EF100400LIS2` (Canon EF 100-400mm f/4.5-5.6L IS II USM)
    - `EF10028LIS` (Canon EF 100mm f/2.8L Macro IS USM)
    - `EF5018STM` (Canon EF 50mm f/1.8 STM)
- Update `search` to check `modelNumber` first.

### ViewModel Layer

#### [MODIFY] [AddEditLensViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/AddEditLensViewModel.kt)
- Add `modelNumber` state.
- Update `init`, `onSelectSpec`, and `saveLens` to include `modelNumber`.
- Update `onSearch` to trigger on model number change.

### UI Layer

#### [MODIFY] [AddEditLensScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddEditLensScreen.kt)
- Add "Model Number" field at the top.
- Wire "Model Number" to search logic.
- Ensure the field is prominent.

#### [MODIFY] [AddLogScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddLogScreen.kt)
- Add `LensSelection` dropdown to the phone layout (non-wide screen).

## Verification Plan

### Automated Tests
- Build the project using `./gradlew assembleDebug`.

### Manual Verification
- Open `AddLogScreen` on a phone-sized device (or emulator) and verify the Lens dropdown is present.
- Open `AddEditLensScreen`, enter a model number (e.g., "EF10028LIS"), and verify that fields are auto-populated.
