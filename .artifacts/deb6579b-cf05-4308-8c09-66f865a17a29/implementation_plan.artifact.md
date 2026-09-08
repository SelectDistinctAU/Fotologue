# Refinement Plan for Fotologue

Address final feedback items including icon updates, logic fixes, and navigation improvements.

## Proposed Changes

### Icon Update
Replace the old lens icon with the new `lensd` vector drawable.

#### [MODIFY] [GearLibraryScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/GearLibraryScreen.kt)
- Update `GearCategory("Lenses", ...)` to use `R.drawable.lensd`.

#### [MODIFY] [WelcomeScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/WelcomeScreen.kt)
- Update `OnboardingStep` for Lenses to use `R.drawable.lensd`.

### Camera Type Logic Fix
#### [MODIFY] [CameraType.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/CameraType.kt)
- Set `isInterchangeable = false` for `MEDIUM_FORMAT`.

### Navigation Fix (Gear Library Tab Persistence)
Ensure the selected tab in the Gear Library is persisted when returning from other screens.

#### [MODIFY] [GearLibraryViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/GearLibraryViewModel.kt)
- Add `selectedCategoryIndex: Int` to `GearLibraryUiState`.
- Add `onCategorySelected(Int)` method to update the state.

#### [MODIFY] [GearLibraryScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/GearLibraryScreen.kt)
- Use `uiState.selectedCategoryIndex` instead of local `remember`ed state.
- Update the selection via `viewModel.onCategorySelected(index)`.

## Verification Plan

### Automated Tests
- Run existing unit tests to ensure no regressions.

### Manual Verification
- **Icon Check**: Verify `lensd` icon appears in `WelcomeScreen` and `GearLibraryScreen`.
- **Camera Type Check**: Verify that selecting "Medium Format" in `AddEditCameraScreen` sets the mount to "Fixed" (if not already set) or at least respects the automation.
- **Automation Check**: Add "Yashica Mat-124G" and verify it auto-creates a lens.
- **Navigation Check**: Go to Gear Library -> Lenses -> Add Lens -> Save. Verify it returns to the "Lenses" tab in Gear Library.
