# Implementation Plan - Expressive M3 Design and Motion Update

Modernize the Fotologue app by implementing "Expressive" Material 3 design principles, including a refined color scheme, advanced motion, and adaptive UI improvements.

## User Review Required

> [!IMPORTANT]
> This plan introduces significant visual changes to the app's theme and component styling to align with the latest Android "Expressive" design guidelines.

## Proposed Changes

### Theme and Visual Identity

#### [MODIFY] [Theme.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/theme/Theme.kt)
- Update the fallback color palette to be more photography-focused (e.g., using rich blacks, refined grays, and a vibrant primary accent).
- Implement `dynamicLightColorScheme` and `dynamicDarkColorScheme` refinements for Android 12+.

#### [MODIFY] [Color.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/theme/Color.kt)
- Define new expressive color tokens.

### UI Components (Expressive Design)

#### [MODIFY] [TimelineScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/TimelineScreen.kt)
- Replace the standard FAB with a `LargeFloatingActionButton` that morphs or expands on scroll.
- Increase corner radii for `Card` items to match expressive guidelines.
- Add spring-based animations to the list items when they appear or are selected.

#### [MODIFY] [AddLogScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddLogScreen.kt)
- Implement a `Carousel` for selecting gear (Camera, Lens, Film) to make the selection process more visual and engaging.

### Motion and Transitions

#### [MODIFY] [MainActivity.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/MainActivity.kt)
- Refine screen transitions in `NavDisplay` to use expressive shared element-like transitions where applicable (e.g., transitioning from Timeline item to Log Detail).

## Verification Plan

### Automated Tests
- Build the project using `./gradlew :app:assembleDebug`.
- Run existing unit tests to ensure no regressions in data handling.

### Manual Verification
- Verify the new expressive theme in both light and dark modes.
- Verify that the `LargeFloatingActionButton` and `Carousel` function correctly.
- Observe screen transitions and item animations for smoothness and responsiveness.
