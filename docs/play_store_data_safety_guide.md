# Google Play Store Data Safety Guide: Fotologue

To ensure a smooth approval process and compliance with Google Play policies regarding "On-device Face Tagging," follow this guide when filling out the **Data Safety** section in the Google Play Console.

---

## 1. Biometric Data (Face Detection)
Even though Fotologue processes face detection **100% on-device** and **transitively** (meaning images are never saved to a disk or uploaded), Google requires this to be declared if the ML model "collects" pixel data to function.

### Console Selection:
- **Data Type**: "Biometrics" (under Personal Info).
- **Collection**: Select **Yes**, this data is collected.
- **Sharing**: Select **No**, this data is NOT shared with third parties.
- **Processing**: Select **Ephemeral**. (This is critical: it tells Google the data is processed in memory and immediately discarded).
- **Purpose**: "App Functionality" and "Personalization."

### Description for Reviewers:
*"The app includes an optional Face-Aware Tagging feature. This uses on-device Machine Learning (Google ML Kit) to detect the presence of faces in the camera viewfinder solely to suggest relevant 'People' tags from the user's local library. No images, facial templates, or biometric identifiers are stored, recorded, or transmitted off the device. All processing is ephemeral and occurs in-memory only."*

---

## 2. Location Data
Fotologue allows users to log the GPS coordinates of their shots.

### Console Selection:
- **Data Type**: "Precise Location."
- **Collection**: Select **Yes**, this data is collected.
- **Sharing**: Select **No**, this data is NOT shared.
- **Processing**: Select **Stored**. (The data is stored in the local SQLite database).
- **Purpose**: "App Functionality."

---

## 3. Photos and Videos
If the user enables "Automatic Thumbnail Capture."

### Console Selection:
- **Data Type**: "Photos."
- **Collection**: Select **Yes**, this data is collected.
- **Sharing**: Select **No**, this data is NOT shared.
- **Processing**: Select **Stored**. (Thumbnails are stored in the app's private internal storage).
- **Purpose**: "App Functionality."

---

## 4. Privacy Policy Link
Ensure your privacy policy at `https://www.fotologue.au/privacy-policy` explicitly mentions:
1.  **Local-Only Storage**: All logging data remains on the device.
2.  **No Cloud Sync**: The app does not have a backend or cloud component.
3.  **On-Device ML**: Face detection is local and non-identifiable.

---
*Generated for Fotologue Release v1.0.12*
