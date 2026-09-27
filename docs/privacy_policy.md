# Fotologue Privacy Policy

**Effective Date:** September 2026

## 1. Local-First Architecture
Fotologue is designed with a strict **Local-First, Offline-First** architecture. We believe that your photography, field logs, darkroom recipes, and physical geolocation data belong exclusively to you. 

Fotologue **does not** use cloud synchronisation by default. All data—including your database graphs, exposure settings, GPS coordinates, and image thumbnails—is stored locally in the secure SQLite sandbox on your device (Android / Desktop).

## 2. Zero Analytics & Zero Tracking
Fotologue contains:
*   **NO** third-party analytics trackers.
*   **NO** advertising SDKs.
*   **NO** crash reporting telemetry that sends your data to remote servers without explicit consent.

## 3. Location Data (GPS)
Fotologue requests location permissions (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`) solely for the purpose of appending latitude and longitude coordinates to your personal exposure logs. 

This data is used exclusively to generate ISO 16684 XMP sidecars and Exif metadata for your personal digital archive (e.g., digiKam, Lightroom). **Your location data is never transmitted to us or any third party.**

## 4. Cloud Backup Integrations (Opt-In Only)
Fotologue offers integrations with Google Drive and Network Attached Storage (NAS) via the Android Storage Access Framework (SAF) and native desktop file pickers.

These integrations are strictly **opt-in** and user-initiated. When you export a Master Archive (ZIP) or mount a directory:
*   Fotologue only reads and writes to the specific directory you explicitly authorise.
*   The connection is direct between your device and your chosen storage provider (e.g., your local Synology NAS or your personal Google Drive account). Fotologue does not intercept, relay, or process this data through any intermediate servers.

## 5. Camera & Local Network Access
*   **Camera:** Accessed locally to provide the CameraX Viewfinder for optical zoom emulation and spot-metering. No image pixel data is transmitted off-device.
*   **Network:** Used solely to retrieve map tiles (e.g., web map intent) or to communicate with your specified NAS via SMB/WebDAV.

## 6. Changes to This Policy
Because Fotologue is local-first, our data practices are fundamentally static. However, if any new features require external data transfer, we will update this policy and notify users in the release notes.

If you have questions about your privacy, you can audit the open SQLite database (`fotologue_database`) stored on your device at any time using the in-app backup exporter.
