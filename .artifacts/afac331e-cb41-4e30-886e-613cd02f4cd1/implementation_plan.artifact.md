# Implementation Plan - UI Refinements and Documentation Update

## User Review Required

> [!IMPORTANT]
> - The Filter row in the Timeline screen will be conditional (hidden when there are no logs at all) and will use `FlowRow` to wrap chips cleanly instead of clipping horizontally.
> - Viewfinders will be added as a sub-tab in the Accessories section of the Gear Library alongside TCs, Filters, and Flashes, making the accessory list vertical and complete.
> - The light meter calibration control will be reviewed/relocated or clarified according to user preferences.
> - Documentation files (`README.md`, `docs/`) will be updated to reflect the latest feature set.

## Proposed Changes

### Timeline Screen
#### [MODIFY] [TimelineScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/ui/screens/TimelineScreen.kt)
- Hide the Filter row entirely (`if (allLogsCount > 0 || logs.isNotEmpty())` or similar check based on whether logs exist) if there are no logs at all.
- Replace `LazyRow` with `FlowRow` for the Filter chips so they wrap when wrapping is requested/beneficial.

### Gear Library & Accessories Screen
#### [MODIFY] [GearLibraryScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/ui/screens/GearLibraryScreen.kt)
- Add Viewfinders tab to `AccessoriesGearScreen` so viewfinders appear correctly in the accessory list and are fully accessible vertically.
- Ensure all accessory lists (Teleconverters, Filters, Flashes, Viewfinders) use vertical layout and tabs correctly.

### Light Meter & Calibration
#### [MODIFY] [LightMeterScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/ui/screens/LightMeterScreen.kt)
- Review calibration button placement and meter exposure area.

### Documentation
#### [MODIFY] [README.md](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/README.md)
#### [MODIFY] [docs/store_listing_descriptions.md](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/docs/store_listing_descriptions.md)
- Update documentation to include Viewfinders, updated accessory lists, and light meter calibration guidelines.

## Verification Plan

### Automated Tests
- Run `./gradlew test` and `./gradlew lint` to ensure build integrity and clean code quality.

### Manual Verification
- Verify Timeline Screen filter row wrapping and hiding when no logs exist.
- Verify Viewfinders in Gear Library accessory list.
