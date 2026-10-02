# Friction Log — Memory Lane on Fire TV

Entries follow the hackathon's requested format: task attempted → steps taken
→ expected vs. actual → severity → workaround → actionable suggestion.

---

### 1. tv-material3 buttons ignore plain touch

- **Task:** Build a touch-friendly photo grid and detail actions with
  `androidx.tv.material3`.
- **Steps:** Used `Card` / `Button` from tv-material3 on a touch-capable
  device (tablet) and tapped.
- **Expected:** Tap activates the component.
- **Actual:** Taps were regularly dropped; components only responded reliably
  to D-pad focus + OK.
- **Severity:** High (core navigation).
- **Workaround:** Hand-rolled `PhotoCard` (Box + `clickable`) and `PillButton`
  (focus scale + border) that respond to both touch and focused-ENTER.
- **Suggestion:** Ship touch-first defaults (or a touch-compatible variant)
  for tv-material interactive components.

### 2. INTERNET permission missing by default

- **Task:** Call a cloud narration API from the scaffolded app.
- **Steps:** Added the API client, ran the app, watched requests fail.
- **Expected:** Network calls succeed once a key is provided.
- **Actual:** Silent failures — the scaffold manifest lacked
  `android.permission.INTERNET`.
- **Severity:** Medium (confusing, silent).
- **Workaround:** Added the permission explicitly; logged all network errors.
- **Suggestion:** Project templates for connected-TV apps should include
  INTERNET by default.

### 3. Fire TV has no Google mobile services

- **Task:** Pick a speech/vision SDK for the AI layer.
- **Steps:** Evaluated GMS-dependent SDKs (Play TTS engine assumptions,
  Firebase), then AWS Bedrock.
- **Expected:** Standard Android ML/TTS stacks would just work.
- **Actual:** Fire OS lacks GMS; AWS Bedrock required credentials our team
  could not obtain (no international credit card).
- **Severity:** High (architectural).
- **Workaround:** Kept the entire AI layer on plain HTTPS JSON
  (`HttpURLConnection`) against a vendor reachable without GMS or an
  international card, with an offline mock engine behind the same interface.
- **Suggestion:** Document a "no-GMS, no-international-credit-card" path in
  the Fire TV onboarding docs; it is a real segment of global developers.

### 4. Fire TV Stick hardware has no microphone

- **Task:** Let family members record voice memories on the TV.
- **Steps:** Implemented in-app recording; tested against the Fire TV form
  factor.
- **Expected:** One recording flow everywhere.
- **Actual:** Fire TV Sticks have no microphone (voice lives in the remote
  and is owned by Alexa); the app cannot capture audio.
- **Severity:** High for the feature, contained by design.
- **Workaround:** Detect `FEATURE_MICROPHONE`; mic-less devices get the same
  screen with a typed-note flow. Transcript failures (balance, network) fall
  back to typing as well.
- **Suggestion:** Publish a documented way for third-party apps to capture
  remote-mic audio, or expose a companion-phone handoff API.

### 5. ASR is metered while the rest of the AI stack is not

- **Task:** Turn recorded voice into text.
- **Steps:** Integrated the vendor ASR endpoint; ran the first end-to-end
  test.
- **Expected:** Same free-tier behaviour as the vision/text models.
- **Actual:** ASR is billed separately (error 1113 without balance) while
  glm-4v-flash / glm-4-flash have free tiers.
- **Severity:** Low (design handled it).
- **Workaround:** Treat "no balance / no network" as a first-class state: the
  transcript field stays editable and the demo continues.
- **Suggestion:** A small monthly free ASR quota for hackathon builders would
  remove a whole fallback path.

### 6. No Fire TV hardware available in our region

- **Task:** Verify the app on target hardware.
- **Steps:** Attempted to source a Fire TV Stick locally.
- **Expected:** Standard retail availability.
- **Actual:** Fire TV is not retailed in our region; physical testing was not
  possible in the submission window.
- **Severity:** Medium.
- **Workaround:** Verified on the Android TV emulator (API 36, 1080p) plus a
  physical touch tablet, treating D-pad and touch as separate test paths.
- **Suggestion:** A purchasable developer kit, or wider regional availability
  of the official Fire TV simulator, would help global entrants.

### 7. Emulator disk friction

- **Task:** Create a TV emulator for verification.
- **Steps:** Created an AVD from the Android TV system image on a
  space-constrained disk.
- **Expected:** AVD boots like any phone AVD.
- **Actual:** The image creates a 6 GB userdata partition by default; boot
  fails with "Not enough space" even when the disk has just enough room, and
  the `-partition-size` workaround caps at 2047 MB.
- **Severity:** Low (one-time setup).
- **Workaround:** Freed disk space; documented the defaults.
- **Suggestion:** Let the first boot choose a smaller userdata size, or ship
  a "compact" TV image.

### 8. Stale AVD configurations fail with an opaque error

- **Task:** Boot a previously created TV AVD.
- **Steps:** Ran the emulator on an old AVD entry.
- **Expected:** Boot, or a clear "recreate the AVD" message.
- **Actual:** `FATAL: CPU Architecture 'arm' is not supported` with no hint
  about the stale configuration.
- **Severity:** Low.
- **Workaround:** Deleted the AVD and recreated it from the correct x86_64
  image.
- **Suggestion:** Detect stale/unsupported AVD configs and offer one-click
  recreation.

### 9. Focus restoration is lost after navigating back

- **Task:** Return from a photo's detail screen to the grid.
- **Steps:** Opened a photo, pressed back, expected focus on the same card.
- **Expected:** Focus restores to the last item (standard TV behaviour).
- **Actual:** Focus reset to the first card; users must re-navigate on every
  back-press.
- **Severity:** Medium (compounds over a browsing session).
- **Workaround:** Explicitly saving and restoring focus item ids per screen.
- **Suggestion:** `LazyVerticalGrid` on TV should restore focused item state
  across navigation by default.
