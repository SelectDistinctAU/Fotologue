# Privacy Policy for Fotologue 🔒

**Version:** 3.0.0 | **Last Updated:** October 2026 | **Audience:** End Users, App Store Reviewers & Public  
**Website:** [https://www.selectdistinct.au](https://www.selectdistinct.au) | **Online Policy:** [https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md](https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md)

Fotologue is built from the ground up with a **local-first, privacy-by-design** architecture. We believe your photography, equipment records, location history, vendor contacts, and personal notes belong solely to you. Fotologue operates entirely on your device and does not collect, store, transmit, or monetise any user data.

---

## 1. Zero Data Collection & Local-First Storage

* **100% On-Device Persistence:** All data created or imported into Fotologue—including your camera and lens library, film rolls, exposure logs, darkroom recipes, contacts directory, notes, GPS coordinates, and thumbnail images—is stored locally in your device's application-sandboxed database (SQLite/Room) and local preferences (DataStore).
* **No User Accounts:** Fotologue does not require user registration, sign-in, email addresses, or accounts of any kind.
* **No Cloud Servers or External Analytics:** Fotologue does not operate cloud servers for user telemetry and contains no third-party tracking, advertising, or analytics SDKs. Your data never leaves your device unless you explicitly export or share it yourself.

---

## 2. Device Permissions & Privacy-First Contracts

Fotologue requests system permissions strictly to provide local photography utility features:

### A. Privacy-First Contact Picker (`PickContact`)
* **Purpose:** Used on-device when adding a contact in the Contacts Directory to select a vendor or friend's contact information.
* **Zero Address Book Harvesting:** Fotologue leverages Android's native `ActivityResultContracts.PickContact()` contract. Fotologue **NEVER** requests broad read access to your phone's address book (`READ_CONTACTS`). You select the single contact you wish to import, and only that contact's display name is read into local memory.

### B. Location Permission (`ACCESS_FINE_LOCATION`)
* **Purpose:** Used on-device when creating an exposure log or taking a light meter reading to determine your current geographic coordinates (latitude and longitude).
* **Scope:** Location data is processed locally and stored exclusively inside your on-device database. It is never transmitted to the developer or any third parties. Location data is only included in your Export Master Archives (ZIP) or ISO 16684 XMP sidecar export files when you explicitly perform an export.

### C. Camera Permission (`CAMERA`)
* **Purpose:** Used locally for live-view spot/centre-weighted light metering and generating optional shot thumbnail previews.
* **Scope:** Live camera video frames are processed in-memory for exposure calculations. Camera images are saved as local thumbnails inside your app storage only if enabled in Settings. No photos, video streams, or camera metadata are ever transmitted off-device.

### D. Optional Cloud Preset Refresh over HTTPS
* **Purpose:** Used when you manually tap **`[ 🔄 Refresh Master Presets from Cloud ]`** in Settings to update your camera, lens, film, and gear preset catalog.
* **Scope:** Fetches public, read-only JSON files over encrypted HTTPS directly from the official GitHub repository. **Zero user data, device identifiers, or telemetry are transmitted during this request.**

---

## 3. On-Device Machine Learning & Biometric Privacy

* **Face-Aware Tagging:** Fotologue integrates Google ML Kit's on-device Face Detection library to detect human faces in local thumbnail previews, suggesting tags and assisting with exposure adjustments.
* **Privacy Assurance:** All machine learning inference occurs **100% on-device**. No facial models, biometric vectors, face meshes, or photo imagery are ever stored, created as biometric profiles, or transmitted off your device.

---

## 4. Data Ownership & Portability

You maintain complete sovereignty over your data:
* **Export Anything:** You can export your exposure logs and metadata at any time using our comprehensive Master Archive format (ZIP), which encapsulates your thumbnails, notes, tags, and standard ISO 16684 XMP sidecars.
* **Backup & Restore:** Complete database backups can be generated and restored locally at any time, allowing seamless synchronisation between the Mobile Field app and Desktop.
* **Data Erasure:** Uninstalling Fotologue or clearing the application data via System Settings permanently erases all local database records and application files from your device.

---

## 5. Contact Us

If you have questions about Fotologue's privacy practices or architecture, please contact us:
* **Website:** [https://www.selectdistinct.au](https://www.selectdistinct.au)
* **GitHub Repository:** [https://github.com/SelectDistinctAU/Fotologue](https://github.com/SelectDistinctAU/Fotologue)
* **Privacy Policy:** [https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md](https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md)
