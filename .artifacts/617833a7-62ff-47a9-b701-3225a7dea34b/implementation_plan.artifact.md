# Implementation Plan - Fix Onboarding Progress Tracking

The onboarding progress for "Lens" and "Film" is not updating correctly. This plan addresses the issue by optimizing the repository's status tracking and refining the `OnboardingViewModel` to use a explicit `UiState`.

## Proposed Changes

### [Data Layer]

#### [MODIFY] [Daos.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/Daos.kt)
- Add `hasCameras()`, `hasFilms()`, and `hasLenses()` methods to their respective DAOs using `SELECT COUNT(*) > 0` for better performance and reliability.

#### [MODIFY] [FotologueRepository.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/FotologueRepository.kt)
- Update `hasCamera`, `hasFilm`, and `hasLens` flows to use the new DAO methods.

### [UI Layer]

#### [MODIFY] [OnboardingViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/OnboardingViewModel.kt)
- Introduce `OnboardingUiState` data class.
- Update `OnboardingViewModel` to expose a single `uiState: StateFlow<OnboardingUiState>`.
- Collect the status flows from the repository and update the `uiState`.

#### [MODIFY] [WelcomeScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/WelcomeScreen.kt)
- Update `WelcomeScreen` to use the new `uiState` from `OnboardingViewModel`.

## Verification Plan

### Automated Tests
- N/A (Focusing on fixing the reported issue via direct implementation check)

### Manual Verification
1. Build and run the app.
2. Navigate to the `WelcomeScreen`.
3. Click "Add your first Lens" and save a lens.
4. Verify the "Add your first Lens" step is marked as complete in `WelcomeScreen`.
5. Click "Add your first Film" and save a film.
6. Verify the "Add your first Film" step is marked as complete in `WelcomeScreen`.
7. Verify that once all steps are complete, the app navigates to the `Timeline` screen.
