# Fotologue: Professional QA Test Plan & Strategy
**Version:** 1.0.12 (Build 13)
**Audience:** New Testers (No prior knowledge required)

---

## 1. App Objective
**Fotologue** is a specialized digital companion for analog (film) photographers. In the analog world, cameras do not record metadata (EXIF) like digital ones. Fotologue fills this gap by allowing photographers to log the technical details of every shot—aperture, shutter speed, location, and gear used—creating a professional record for development and digital archiving.

### Design Intent
- **Speed**: Interactions must be fast enough to happen in the field between shots.
- **Privacy**: No cloud. No accounts. All data is processed on-device (local-first).
- **Precision**: Professional-grade metering that can be calibrated to match high-end hardware.

---

## 2. Core Feature Map
1. **The Gear Library**: A database to manage Cameras, Lenses, and Film stocks.
2. **The Light Meter**: Uses the phone's sensor to suggest exposure settings.
3. **The Timeline**: A chronological list of shots with interactive maps and export statuses.
4. **The Wizard**: A 2-stage hardware calibration tool for sensor accuracy.
5. **Data Export**: Generating CSV, JSON, or ZIP (thumbnails + XMP sidecars) packages.

---

## 3. Test Scripts

### Script A: First-Run & Permissions
**Steps:**
1. Clear App Data/Cache or perform a fresh install.
2. Launch the app. Observe the "Welcome" screen.
3. Attempt to add a Camera.
4. **Calibration Check**: On the Welcome screen, observe the "Advanced Configuration" section and verify the "Calibrate Light Meter" option is available.
5. **Permission Trigger**: Grant Location and Camera permissions when prompted.

**Expected Behaviour:**
- The app should not crash during the permission flow.
- "Equipment Setup" indicators on the Welcome screen should update to "Complete" as you add gear.

**Unexpected Behaviour:**
- App closes when granting permissions.
- "Save" button remains disabled even after gear is added.

---

### Script B: The "Fixed Lens" Logic
**Steps:**
1. Go to **Gear Library > Cameras**.
2. Add a new Camera and select "Compact" or "Point & Shoot" (Fixed Lens types).
3. Fill in the "Fixed Lens" specifications in the lookup.
4. Save the camera.
5. Go to the **Lenses** tab.

**Expected Behaviour:**
- A new lens should automatically appear in the library with the "Fixed" mount type.
- This lens should be automatically associated with your new camera.

**Unexpected Behaviour:**
- The lens is not created.
- The lens appears with an "Interchangeable" mount type.

---

### Script C: The Wizard's Calibration
**Steps:**
1. Go to **Settings > EV Calibration**.
2. Tap **Launch Pro Calibration Wizard**.
3. **Stage 1 (The Void)**: Cover the camera lens completely with your thumb. Tap "Measure Black Point."
4. **Stage 2 (The Balance)**: Point the camera at a neutral gray wall or gray card in daylight. Tap "Calibrate Sensitivity."
5. Complete the wizard.

**Expected Behaviour:**
- The app returns you to the **EV Calibration** settings page.
- "Hardware Baseline" values (Noise Floor and Signal Gain) should now be non-zero.

**Unexpected Behaviour:**
- App gets stuck in the Light Meter measure mode after the wizard.
- Calibration values do not update or show "NaN".

---

### Script D: The Logging Loop (Update vs. Create)
**Steps:**
1. Start a new Log from the Timeline (+).
2. Type a note: "Initial Note." Save the log.
3. Open that log from the Timeline and tap the **Edit (Pencil)** icon.
4. Change the note to "Updated Note" and tap **Save Changes**.
5. Return to the Timeline and check the log.

**Expected Behaviour:**
- There is only **ONE** log entry in the Timeline.
- The note is updated to "Updated Note."
- The detail view shows a "Last updated" timestamp under the main date.

**Unexpected Behaviour:**
- A second, duplicate log entry appears in the Timeline.
- The original note is lost.

---

### Script E: Export & Badges
**Steps:**
1. Long-press a log in the Timeline to select it.
2. Tap the **Export (Share)** icon and choose **ZIP**.
3. Save the file to your device's Downloads folder.
4. Observe the log entry in the Timeline.

**Expected Behaviour:**
- A small `[Z]` badge appears in the bottom-right corner of the log entry.
- The file saved contains a `.jpg` thumbnail and a `.xmp` metadata file.

**Unexpected Behaviour:**
- The badge does not appear after a successful save.
- Tapping the badge does nothing (it should be non-interactive).

---

## 4. System Red Flags
If you see any of the following, file a high-priority bug report:
- **IME Overlap**: The keyboard covers the text field you are typing in.
- **Race Condition**: The Light Meter "freezes" or flickers rapidly between two values.
- **Data Leak**: Being prompted to create an account or sign in (this app is local-only).
- **Locale Failure**: Dates appearing in English when the phone is set to German/Spanish/etc.
