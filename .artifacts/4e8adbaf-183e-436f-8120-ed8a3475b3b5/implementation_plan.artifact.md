# Restore from Backup Implementation Plan

Allow users to restore their Fotologue configuration (gear library and settings) directly from the onboarding `WelcomeScreen`.

## User Review Required

> [!IMPORTANT]
> The "Restore from Backup" process will overwrite any current local data (though unlikely to exist during onboarding).
> A successful restore that includes at least one camera, one lens, and one film will automatically transition the user to the `Timeline` screen.

## Proposed Changes

### [Onboarding & Setup]

#### [MODIFY] [OnboardingViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/OnboardingViewModel.kt)
- Update constructor to accept `ConfigurationManager`.
- Add `restoreBackup(inputStream: InputStream)` method to handle the import process within a coroutine.
- Add `restoreError` to `OnboardingUiState` to handle and display any import failures.

#### [MODIFY] [WelcomeScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/WelcomeScreen.kt)
- Add a "Restore from Backup" button using `TextButton` or `OutlinedButton` style.
- Implement `rememberLauncherForActivityResult` with `ActivityResultContracts.GetContent()` to allow picking `.json` backup files.
- Integrate the file picker result with `viewModel.restoreBackup`.
- Add a `SnackbarHost` to display errors if the restore fails.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/MainActivity.kt)
- Update the `OnboardingViewModel` factory in `NavDisplay` to inject `configurationManager` from `FotologueApplication`.

## Verification Plan

### Automated Tests
- **Unit Test**: Create `OnboardingViewModelTest` to verify that `restoreBackup` calls `configurationManager.importConfig` and handles errors correctly.

### Manual Verification
1. Launch the app for the first time (or clear data) to reach the `WelcomeScreen`.
2. Tap "Restore from Backup".
3. Select a valid Fotologue backup JSON file.
4. Verify that the app transitions to the `Timeline` screen automatically.
5. Verify that the gear library is populated with the data from the backup.
6. Verify that an invalid file shows an error message.
