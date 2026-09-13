# Privacy Policy for Fotologue 🔒

**Version:** 1.0.17 (Build 18) | **Effective Date:** September 20, 2024 | **Audience:** End Users, App Store Reviewers & Public  
**Website:** [https://www.fotologue.au](https://www.fotologue.au) | **Online Policy:** [https://www.fotologue.au/privacy-policy](https://www.fotologue.au/privacy-policy)

Fotologue is built from the ground up with a **local-first, privacy-by-design** architecture. We believe your photography, equipment records, location history, and personal notes belong solely to you. Fotologue operates entirely on your device and does not collect, store, transmit, or monetise any user data.

---

## 1. Zero Data Collection & Local-First Storage

* **100% On-Device Persistence:** All data created or imported into Fotologue—including your camera and lens library, film rolls, exposure logs, EV calibration data, notes, GPS coordinates, and thumbnail images—is stored locally in your device's application-sandboxed database (Room) and local preferences (DataStore).
* **No User Accounts:** Fotologue does not require user registration, sign-in, email addresses, or accounts of any kind.
* **No Cloud Servers or External Analytics:** Fotologue does not operate cloud servers for user data storage and contains no third-party tracking, advertising, or telemetry SDKs (such as Firebase Analytics or Mixpanel). Your data never leaves your device unless you explicitly export or share it yourself.

---

## 2. Device Permissions & Usage

Fotologue requests Android system permissions strictly to provide local photography utility features:

### A. Location Permission (`ACCESS_FINE_LOCATION`)
* **Purpose:** Used on-device when creating an exposure log or taking a light meter reading to determine your current geographic coordinates (latitude and longitude).
* **Scope:** Location data is processed locally and stored exclusively inside your on-device database. It is never transmitted to the developer or any third parties. Location data is only included in CSV, JSON, or ISO 16684 XMP sidecar export files when you explicitly perform an export.

### B. Camera Permission (`CAMERA`)
* **Purpose:** Used locally for live-view spot/centre-weighted light metering and generating optional shot thumbnail previews.
* **Scope:** Live camera video frames are processed in-memory for exposure calculations. Camera images are saved as local thumbnails inside your app storage only if enabled in Settings. No photos, video streams, or camera metadata are ever transmitted off-device.

### C. Storage Access Framework (SAF)
* **Purpose:** Used when you manually choose to create a full JSON backup, export XMP sidecar files, or save CSV/ZIP archives.
* **Scope:** File destinations and directory permissions remain entirely under your control.

---

## 3. On-Device Machine Learning & Biometric Privacy

* **Face-Aware Tagging:** Fotologue integrates Google ML Kit's on-device Face Detection library to detect human faces in local thumbnail previews, suggesting "People" tags and assisting with exposure adjustments.
* **Privacy Assurance:** All machine learning inference occurs **100% on-device**. No facial models, biometric vectors, face meshes, or photo imagery are ever stored, created as biometric profiles, or transmitted off your device.

---

## 4. Data Ownership & Portability

You maintain complete sovereignty over your data:
* **Export Anything:** You can export your exposure logs and metadata at any time using open, industry-standard formats (ISO 16684 XMP sidecars, CSV, JSON, or ZIP archives containing thumbnails).
* **Backup & Restore:** Complete database backups can be generated and restored locally at any time via Settings.
* **Data Erasure:** Uninstalling Fotologue or clearing the application data via Android System Settings permanently erases all local database records and application files from your device.

---

## 5. Third-Party Links & Services

Fotologue does not embed third-party advertising SDKs or tracking services.
* **Location Map Links:** If you tap a location link within log details, the app opens the standard external Google Maps URI handler on your device.
* **Online Specs Lookup:** If you use the optional web search link for lens specs, the app launches your default system web browser to perform the search.

---

## 6. Children's Privacy

Because Fotologue does not collect personal information from any user, it does not collect or solicit personal information from children under the age of 13 (or 16 in certain jurisdictions).

---

## 7. Changes to This Privacy Policy

We may update this Privacy Policy from time to time to reflect app enhancements or legal requirements. Any changes will be reflected with a revised "Effective Date" at the top of this document.

---

## 8. Contact Us

If you have questions about Fotologue's privacy practices or architecture, please contact us:
* **Website:** [https://www.fotologue.au](https://www.fotologue.au)
* **Privacy Policy:** [https://www.fotologue.au/privacy-policy](https://www.fotologue.au/privacy-policy)
