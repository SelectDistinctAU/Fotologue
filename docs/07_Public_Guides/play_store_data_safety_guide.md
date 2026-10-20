# Google Play Store Data Safety Guide: Fotologue

**Version:** 1.0.17 (Build 18) | **Last Updated:** September 2026 | **Audience:** Developers & Play Store Compliance Reviewers

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
- **Data Type**: "Precise Location" (under Location).
- **Collection**: Select **Yes**.
- **Sharing**: Select **No**.
- **Processing**: Stored locally in the app's database; only included in export files requested explicitly by the user.
- **Purpose**: "App Functionality."
