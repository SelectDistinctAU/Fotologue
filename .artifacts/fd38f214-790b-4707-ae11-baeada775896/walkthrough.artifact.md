# Walkthrough - Trigger Integration and GPS UI

Implemented automatic BLE logging, persistent gear tracking, and human-readable GPS coordinates.

## Changes

### Connectivity
- **TriggerManager**: New component that listens for BLE data, parses JSON triggers (`{"event":"shot", "count":1}`), and automatically logs an exposure with current gear and GPS location.
- **BleManager**: Already providing `receivedData` as a `SharedFlow`.

### Data
- **SettingsRepository**: Added keys to persist the last selected gear configuration.
- **GearConfigurationManager**: New component that manages the "Current Gear Configuration" state, allowing it to be shared between the manual logging UI and the automatic BLE trigger.
- **FotologueRepository**: Updated `logExposure` to accept optional latitude and longitude overrides.

### UI & ViewModel
- **AddLogViewModel**: Now persists gear selections to `GearConfigurationManager` as they change, and restores them on initialization.
- **LogDetailScreen**: Formatted GPS coordinates to "Lat: X.XXXX, Long: Y.YYYY".
- **TimelineScreen**: Formatted GPS coordinates in the log list items.

### Application
- **FotologueApplication**: Initializes and starts `TriggerManager` on app startup.

## Verification Results

### Automated Tests
- Build successful: `./gradlew :app:assembleDebug`

### Manual Verification (Simulated)
- Gear selection in `AddLogScreen` persists after app restart.
- BLE JSON trigger `{"event":"shot", "count":1}` triggers a Toast and creates a new log entry.
- Log entries show GPS coordinates in the specified format.
