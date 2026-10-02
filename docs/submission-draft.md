# Devpost Submission Draft — Memory Lane
# (paste into https://devpost.com/submit-to/30992-build-ship-shape-amazon-developer-hackathon)
# Deadline: 2026-10-24 03:00 GMT+8 · internal deadline 10-20

## Project name
Memory Lane

## Tagline (short)
Every photo has a story. Every story starts with a real voice.

## Track
Fire TV (primary track)
Mini challenge: Open Source (new open-source project, MIT-0 license)

## What we built
Memory Lane turns a family photo album on Fire TV into a couch experience for
grandparents: photos appear on the big screen and an AI narrator retells the
TRUE memory behind each one, out loud.

The rule that makes it different from every AI photo-storytelling toy: the
story must be true. A family member's recorded memory (voice, any language)
is the only source of truth. GLM-4V retells it warmly — it may polish words,
never add facts. A photo nobody has told yet says so instead of inventing a
story.

Features demonstrated in the video:
1. Photo wall (D-pad + touch) → tap a photo → warm narration, read aloud by
   on-device TTS.
2. "Record a memory" — Dad speaks about a photo in his own language; GLM-ASR
   transcribes it; after a one-glance review it becomes the photo's memory
   note, and the narrator retells HIS story. Typed notes are the fallback on
   mic-less Fire TV Sticks (same screen).
3. "Our Family" — enroll family members with one reference photo; the
   narrator then recognises faces ("👤 Dad" chip) and uses their names in the
   story.
4. Honesty: a photo with no memory shows "This photo hasn't been told yet…
   the story must be yours, not mine."

## How we built it
- Kotlin + Jetpack Compose TV; zero third-party app dependencies (AI layer is
  plain HTTPS JSON over HttpURLConnection) — the APK is ~10 MB.
- Zhipu BigModel GLM family (reachable without Google services and without an
  international credit card): glm-4v-flash (vision narration + face
  identification), glm-4-flash (text fallback), glm-asr (voice→text).
- On-device TTS for spoken narration.
- Every cloud call sits behind a degradation ladder (vision → text → offline
  mock; voice → typed note) so the demo cannot die on stage.
- Family data (notes, member roster) is stored only on the device.

## Challenges (full details in FRICTION_LOG.md)
- tv-material3 components ignore plain touch → hand-rolled touch+D-pad
  components.
- Fire TV has no Google mobile services and AWS Bedrock needed a card we
  don't have → vendor-neutral HTTPS AI layer with offline mock.
- Fire TV Stick has no microphone → mic detection with a typed-note flow.
- No Fire TV hardware in our region → verified on the Android TV emulator
  and a touch tablet.

## Accomplishments we're proud of
- An honest-AI product in an era of hallucinating toys: the narration never
  invents facts about your family.
- The voice → transcript → story loop works in any language.
- A 10 MB app with zero dependencies that still feels warm and alive.

## What's next
- Photo import from the family's own library (the 1931 demo album stands in
  for it today).
- "Storytelling evening" mode: one continuous recording session annotated
  across dozens of photos via slide-show timestamps.
- Multi-language UI (the narrator already retells notes recorded in any
  language).

## Built with
kotlin, jetpack-compose-tv, fire-tv, glm-4v-flash, glm-asr, tts, android

## Repo
https://github.com/lypxjs/memory-lane (public, MIT-0)

## Demo video
docs/demo-video.mp4 (≤3:00; recorded on the Android TV simulator at 1080p)

## Checklist
- [x] Repo public + MIT-0 LICENSE
- [x] README (features/tech/build/credits)
- [x] FRICTION_LOG.md in official format (9 entries)
- [x] Demo video ≤3:00 — recorded on TV simulator
- [ ] (optional) add reviewers as repo collaborators at submission time:
      chris-trag, knmeiss, giolaq, anishamalde, mosesroth, emersonsklar
      (repo is public; collaborators only needed if we switch to private)
- [ ] Submit form on Devpost (needs user login) — submission URL:
      https://devpost.com/submit-to/30992-build-ship-shape-amazon-developer-hackathon
