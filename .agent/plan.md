# Project Plan

An Android app named "Fotologue" for analog photographers. Refactoring the gear library to support many-to-many relationships between cameras and lenses.

## Project Brief

# Fotologue: Project Brief

An Android application designed for analog photographers to manage their gear library and log photo sessions with precision. The core focus is on refactoring the camera-lens relationship to support professional-grade gear flexibility.

## Features (MVP)
*   **Many-to-Many Gear Catalog**: A flexible library management system that allows a single lens to be associated with multiple camera bodies, while maintaining dedicated support for fixed-lens cameras.
*   **Intelligent Gear Filtering**: A streamlined "Add Log" experience that automatically filters and displays only compatible lenses based on direct camera associations or matching mount types (e.g., M-Mount, PL-Mount).
*   **Adaptive Gear Dashboard**: A responsive interface using canonical layouts that adapts from a single-pane list on phones to a multi-pane list-detail view on tablets and foldables.
*   **Mount-Aware Compatibility Engine**: A system that suggests pairings by analyzing mount specifications, ensuring photographers only select gear that physically fits their chosen camera.

## High-Level Technical Stack
*   **Kotlin**: The primary programming language for robust and concise development.
*   **Jetpack Compose**: The modern toolkit for building declarative and reactive UIs.
*   **Jetpack Navigation 3**: A state-driven navigation architecture that treats the backstack as a standard Compose list, providing full control over the app's flow.
*   **Compose Material Adaptive**: Essential for implementing adaptive scaffolds (like `ListDetailPaneScaffold`) and the `NavigationSuiteScaffold` to handle diverse device form factors.
*   **Kotlin Coroutines**: For managing asynchronous operations and state updates without blocking the UI thread.

> [!NOTE]
> The **UI Design Image** section was omitted as the image generation tool is not available in the current environment.

## Implementation Steps
**Total Duration:** 1h 10m 48s

### Task_25_BLECoreAndSettings: Implement the BLE infrastructure and configuration UI. Develop a BLE manager to scan for 'FotologueCameraTrigger', manage connections, and handle characteristic notifications. Create a Settings screen allowing users to define custom Service and Characteristic UUIDs, with defaults for Adafruit Feather and Nordic UART.
- **Status:** COMPLETED
- **Updates:** Implemented BLE core infrastructure and configuration UI.
- **Acceptance Criteria:**
  - BLE scanning successfully identifies 'FotologueCameraTrigger'
  - Connection to BLE peripheral is stable
  - Settings UI allows saving and applying custom UUIDs
  - Hardware defaults are pre-configured and functional
  - Build pass
- **Duration:** 4m 49s

### Task_26_TriggerIntegrationAndGPSUI: Integrate BLE notifications into the shot logging workflow and update UI displays. Automatically create LogEntry instances upon receiving BLE signals, capturing current GPS and gear state. Update the Timeline and Log Detail screens to display GPS coordinates. Perform a final run and verification of the entire application.
- **Status:** COMPLETED
- **Updates:** Integrated BLE triggers with automated logging and updated the GPS UI.
- **Acceptance Criteria:**
  - BLE signal triggers automatic shot logging with GPS and gear metadata
  - GPS coordinates are visible in Timeline and Log Detail screens
  - App does not crash during BLE connection or triggering
  - All existing tests pass
  - Critic_agent verifies application stability and requirement alignment
- **Duration:** 3m 51s

### Task_27_BackupRestoreImplementation: Implement the Backup & Restore feature. Define a serializable data model for all Gear Library entities and App Settings. Create an export mechanism to generate a JSON file and an import mechanism to restore data from a selected file. Integrate these actions into the Settings UI.
- **Status:** COMPLETED
- **Updates:** Backup & Restore feature implemented.
- **Acceptance Criteria:**
  - Export generates a valid JSON file containing all library data and settings
  - Import successfully restores data from a JSON file
  - Backup and Restore buttons are added to the Settings screen
  - Data integrity is maintained after an import/export cycle
  - Build pass
- **Duration:** 4m 3s

### Task_28_WelcomeScreenRestore: Add 'Restore from Backup' functionality to the Welcome screen. Update WelcomeScreen UI with a restore button and file picker, inject ConfigurationManager into OnboardingViewModel via MainActivity, and implement the restore logic in the ViewModel.
- **Status:** COMPLETED
- **Updates:** "Restore from Backup" added to the Welcome screen.
- **Acceptance Criteria:**
  - Restore from Backup button added to WelcomeScreen
  - File picker opens and allows selecting JSON files
  - OnboardingViewModel successfully triggers ConfigurationManager.importConfig
  - Build pass
- **Duration:** 1m 48s

### Task_29_FinalRunAndVerify: Perform a final run and verification of the application, focusing on the new restore functionality on the Welcome screen. Ensure the app transitions to the Timeline screen after a successful restore and that all data is correctly populated.
- **Status:** COMPLETED
- **Updates:** BLE features hidden from UI (Navigation and Settings) and background scanning deactivated. Fixed '0-byte backup' bug in ConfigurationManager by ensuring proper stream flushing during export. Build verified.
- **Acceptance Criteria:**
  - App does not crash
  - All existing tests pass
  - Application is stable
  - Critic_agent verifies application stability and alignment with user requirements
- **Duration:** 26m 37s

### Task_30_CameraLensLogicAndUI: Implement Camera Types (SLR, Medium Format, Compact, etc.) and 'Fixed Lens' vs 'Interchangeable' logic. Update the Camera and Lens profiling UI to dynamically hide irrelevant fields like 'Lens Mount' for fixed-lens cameras to streamline entry.
- **Status:** COMPLETED
- **Updates:** Camera Type logic and Gear UI refinement implemented.
- **Acceptance Criteria:**
  - Camera data model updated with CameraType enum
  - Logic correctly identifies fixed-lens cameras based on type
  - Lens entry UI dynamically hides 'Lens Mount' for fixed-lens cameras
  - Build pass
- **Duration:** 4m 49s

### Task_31_ZipExportFixAndVerification: Resolve thumbnail corruption in the ZIP export engine by ensuring proper stream handling and data integrity during packaging. Perform a final run to verify application stability and requirement alignment.
- **Status:** COMPLETED
- **Updates:** Fixed thumbnail corruption in ZIP export.
- **Acceptance Criteria:**
  - ZIP export generates uncorrupted thumbnails
  - Exported archives pass integrity checks
  - App does not crash
  - All existing tests pass
  - Critic_agent verifies stability and alignment with user requirements
- **Duration:** 2m 14s

### Task_32_StabilityAndGearAssociation: Fix DataStore singleton crash by ensuring single instance access across the app. Update the Room database schema to support linking lenses to specific cameras and update the Gear Library UI to manage these associations, including filtering and visibility logic.
- **Status:** COMPLETED
- **Updates:** DataStore singleton fix and Camera-Lens association implemented.
- **Acceptance Criteria:**
  - DataStore is implemented as a singleton to prevent file access conflicts
  - Lenses can be associated with specific cameras in Room
  - Gear Library UI allows linking lenses and reflects these associations
  - Build pass
- **Duration:** 5m 23s

### Task_33_LightMeterRefinementAndVerify: Refine the light meter algorithm for precision and add EV calibration offsets. Redesign the Light Meter screen to ensure the camera preview and control interface are non-overlapping. Perform a final run and verification of application stability and requirements.
- **Status:** COMPLETED
- **Updates:** Light Meter refined.
- **Acceptance Criteria:**
  - Light meter precision improved with user-defined calibration offsets
  - Light Meter UI redesigned for clarity and accessibility (non-overlapping)
  - App does not crash during metering or data operations
  - All existing tests pass
  - Critic_agent verifies stability and requirement alignment
  - Build pass
- **Duration:** 3m 59s

### Task_34_ManyToManyAndMountFiltering: Refine the gear catalog to fully support many-to-many Lens-Camera relationships and implement the mount-aware compatibility engine. Update the 'Add Log' workflow to filter lenses by direct association or matching mount types.
- **Status:** COMPLETED
- **Updates:** Many-to-many Lens-Camera relationships implemented.
- **Acceptance Criteria:**
  - Room schema supports many-to-many Camera-Lens relations
  - Lens filtering in 'Add Log' handles mount matches and direct associations
  - Gear management UI supports multi-association
  - Build pass
- **Duration:** 6m 54s

### Task_35_AdaptiveDashboardAndVerify: Implement the adaptive Gear Dashboard using ListDetailPaneScaffold for multi-pane support on tablets and foldables. Perform a final run and verification of application stability and requirement alignment.
- **Status:** COMPLETED
- **Updates:** Finalized Gear Library with adaptive ListDetailPaneScaffold. Improved Lens UI to show mount and associations. Refined fixed-lens logic and mount-aware filtering. Verified build success and app stability.
- **Acceptance Criteria:**
  - Gear Dashboard uses ListDetailPaneScaffold for adaptive UI
  - App does not crash
  - All existing tests pass
  - Critic_agent verifies stability and requirement alignment
  - Build pass
- **Duration:** 6m 21s

