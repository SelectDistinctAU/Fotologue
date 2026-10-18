# Native Windows Desktop Installers Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 08:30 AM UTC

---

This plan configures the native Windows packaging toolchain in `desktop/build.gradle.kts` and adds a convenience `:desktop:packageWindows` Gradle task generating standalone **Windows MSI** and **Windows EXE** native installer packages.

---

## 1. Technical Specifications

### A. Windows Installer Packaging Configuration (`desktop/build.gradle.kts`)
- Configured JDK 21 `jpackage.exe` runtime path (`C:/Users/geewy/.jdks/jbr-21.0.11`).
- Configured `nativeDistributions` properties:
  - `packageName`: `FotologueDesktop`
  - `packageVersion`: `1.0.0`
  - `targetFormats`: `TargetFormat.Msi`, `TargetFormat.Exe`, `TargetFormat.Deb`
- Registered convenience task `:desktop:packageWindows` depending on `:desktop:packageMsi` and `:desktop:packageExe`.

---

## 2. Generated Deliverables

| File Name | Installer Type | File Size | Absolute File Location |
| :--- | :--- | :--- | :--- |
| **`FotologueDesktop-1.0.0.msi`** | Windows MSI Installer | 122.5 MB | `C:\Users\geewy\AndroidStudioProjects\Fotologue\desktop\build\compose\binaries\main\msi\FotologueDesktop-1.0.0.msi` |
| **`FotologueDesktop-1.0.0.exe`** | Windows Executable Installer | 123.2 MB | `C:\Users\geewy\AndroidStudioProjects\Fotologue\desktop\build\compose\binaries\main\exe\FotologueDesktop-1.0.0.exe` |

---

## 3. Verification Plan
1. Run `./gradlew :desktop:packageWindows`.
2. Verify build finishes with status `BUILD SUCCESSFUL`.
3. Verify `FotologueDesktop-1.0.0.msi` and `FotologueDesktop-1.0.0.exe` exist in `desktop/build/compose/binaries/main/`.
