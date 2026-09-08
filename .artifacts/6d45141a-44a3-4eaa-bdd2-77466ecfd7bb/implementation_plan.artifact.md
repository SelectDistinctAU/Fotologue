# Refactor Gear Library Navigation to Vertical Rail

The goal is to move the category navigation in `GearLibraryScreen` from a horizontal bar (currently `NavigationBar` on phones and `NavigationRail` on tablets) to a vertical `NavigationRail` along the left side of the screen for all (or most) form factors, as requested.

## Proposed Changes

### [MODIFY] [GearLibraryScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/GearLibraryScreen.kt)

- **Always show `NavigationRail`**: Remove the `isMediumScreen` check for the `NavigationRail` so it appears on the left even on smaller screens.
- **Remove `NavigationBar`**: Delete the `bottomBar` from the `Scaffold` as navigation is moved to the left rail.
- **Update Icons**: Replace existing category icons with more descriptive ones:
    - Kits: `Inventory2`
    - Cameras: `PhotoCamera`
    - Lenses: `Camera`
    - TCs: `ZoomIn`
    - Filters: `FilterAlt`
    - Films: `CameraRoll`
    - Tags: `Label`
- **Adaptive Improvements**:
    - Ensure the `TopAppBar` `navigationIcon` is only shown when the rail is not present or if specifically needed.
    - Adjust the `NavigationRail` to be more compact on narrow screens if necessary, but start with the standard one.
    - Since there are 7 categories, `NavigationRail` is much better than `NavigationBar` even on phones.

## Verification Plan

### Automated Tests
- Run existing UI tests to ensure no regressions in functionality (adding/editing gear).
- Build the project to verify no compilation errors with new icons.

### Manual Verification
- Verify the layout on different screen sizes (phone and tablet).
- Confirm that clicking icons in the rail correctly switches the displayed gear list.
- Check that the back button in the rail header works correctly.
