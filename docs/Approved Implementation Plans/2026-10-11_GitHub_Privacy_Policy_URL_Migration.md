# GitHub Privacy Policy URL Domain Migration Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 09:30 AM UTC

---

This plan migrates all privacy policy and website links across `PRIVACY_POLICY.md`, `docs/privacy_policy.md`, and `SettingsScreen.kt` to the official GitHub repository URL (`https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md`).

---

## 1. Migrated URLs

| Reference Location | Old Target URL | Updated GitHub Target URL |
| :--- | :--- | :--- |
| **`PRIVACY_POLICY.md`** | `https://www.fotologue.au/privacy-policy` | [`https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md`](https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md) |
| **`docs/privacy_policy.md`** | `https://www.fotologue.au/privacy-policy` | [`https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md`](https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md) |
| **`SettingsScreen.kt`** | `https://www.fotologue.au/privacy-policy` | [`https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md`](https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md) |

---

## 2. Verification Plan
1. Run `./gradlew :app:assembleDebug :app:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `Settings` $\to$ tap **`Privacy Policy`**.
3. Verify the Android `UriHandler` launches `https://github.com/SelectDistinctAU/Fotologue/blob/main/PRIVACY_POLICY.md` cleanly in the system browser!
