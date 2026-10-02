# Friction Log — building Memory Lane for Fire TV

Honest notes on what fought back during the build, and what we did about it.
(Submitted for the Fire TV friction-log bonus.)

1. **tv-material3 components ignore plain touch on touch-screen devices.**
   `Card` and `Button` from `androidx.tv.material3` are D-pad-first: on a
   touch-capable screen taps were regularly dropped. Fix: hand-rolled
   `PhotoCard` (Box + `clickable`) and `PillButton` (focus scale + border),
   which respond to both touch and focused-ENTER.

2. **No INTERNET permission by default.** The app template's manifest shipped
   without it, and the first GLM calls failed silently. Added explicitly.

3. **Fire TV has no Google mobile services.** Anything that quietly assumes
   GMS (Play TTS engine availability, Google sign-in, Firebase) is a trap on
   Fire OS. We kept the whole AI layer on plain HTTPS JSON calls
   (Zhipu BigModel GLM-4V / GLM-ASR) with zero SDK dependencies, which also
   keeps the app 9.7 MB.

4. **Fire TV Stick hardware has no microphone.** The soul feature — recording
   a family memory — must therefore degrade: we detect
   `FEATURE_MICROPHONE` and fall back to a typed note, same screen, same
   persistence. On mic-less devices the recording button becomes a text flow.

5. **ASR is metered, the rest of GLM is not.** `glm-4v-flash` / `glm-4-flash`
   have free tiers; `glm-asr` needs account balance. The record flow treats
   "no balance / no network" as a first-class state: the transcript field
   simply stays editable, and the demo continues.

6. **No Fire TV hardware available in our region.** All TV verification ran on
   the Android TV emulator (API 36, 1080p) plus a physical touch phone.
   D-pad focus behaviour and touch behaviour had to be verified as separate
   paths — they fail in different ways (see 1).

7. **Emulator disk friction.** The Android TV system image creates a 6 GB
   userdata partition by default, which fails on space-constrained disks, and
   the emulator's `-partition-size` workaround caps at 2047 MB. Documenting
   here so the next builder does not lose an hour to it.

8. **AVD configuration traps.** A stale AVD pointing at an unsupported CPU
   architecture (arm) fails with an opaque QEMU error; deleting the AVD and
   recreating from the correct x86_64 image fixed it.
