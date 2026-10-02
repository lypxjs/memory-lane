# Memory Lane

**Every photo has a story. Every story starts with a real voice.**

Memory Lane is a Fire TV app that turns a family photo album into a couch
experience for people who find phones small and apps complicated — parents
and grandparents. Photos appear on the big screen, and an AI narrator retells
the true memory behind each one, out loud, in a warm voice. No typing, no
menus three levels deep: D-pad to move, OK to open, back to return.

Built for the [Build, Ship, Shape: Amazon Developer Hackathon](https://amazonappdev2026.devpost.com/)
(Fire TV track, October 2026).

## The rule that makes it different

AI photo-storytelling toys invent charming nonsense: fictional grandfathers,
imagined beaches, dialogue nobody ever said. Memory Lane is built around one
strict rule:

> **The story must be true. A family member's recorded memory is the only
> source of truth. The AI is a ghostwriter, never an author.**

- Every photo carries a **memory note** — what a family member actually said
  or wrote about it.
- The narrator retells that note warmly and reads it aloud — it may polish
  words, never add facts.
- A photo nobody has told yet **says so** instead of making something up.

Every story screen carries the caption: *"Retold from a family recording —
the AI only polished the words."*

## Features

- **Photo wall** — D-pad navigable, TV-distance legible, 26 bundled photos
  including *The 1931 Album*: ten real photographs from a single family's
  summer, scanned and donated to the public domain (see Credits).
- **Spoken narration** — stories are written by GLM-4V (photo + note in view)
  and read aloud by on-device TTS. "Tell me again" for a fresh telling;
  "Quiet" for a silent house.
- **Record a memory** — the soul of the app. A family member speaks about a
  photo in any language; GLM-ASR turns the voice into text; the text (after a
  quick review) becomes the photo's memory note. On devices without a
  microphone — like the Fire TV Stick — the same screen offers typing. If the
  voice service is unavailable, typing is the fallback. The demo never dies.
- **Our Family** — enroll family members with a name and one clear reference
  photo. From then on the narrator recognises faces in the album: a "👤 Dad"
  chip appears under the story, and the retelling says *Dad* instead of
  *a man*.
- **Honesty by default** — no invented people, events, or dialogue. The app
  would rather stay silent than fib to your grandmother.

## Tech

- Kotlin + Jetpack Compose TV (`androidx.tv:tv-material`), Navigation Compose
- Zero third-party app dependencies: the AI layer is plain HTTPS JSON over
  `HttpURLConnection`
- AI services (Zhipu BigModel, RMB billing, no card needed):
  - `glm-4v-flash` — vision narration and family-face identification
  - `glm-4-flash` — text-only fallback
  - `glm-asr` — speech-to-text for recorded memories
- `StoryEngine` / `GlmAsrEngine` interfaces isolate every cloud call behind a
  degradation ladder: vision → text → offline mock, voice → typed note.
  The demo cannot die on stage.
- API keys are injected at build time from `local.properties`
  (`glm.api.key=…`, gitignored). Without a key the app runs on the offline
  mock engine.

## Build

```
git clone https://github.com/lypxjs/memory-lane
# put your key in local.properties:  glm.api.key=<your BigModel key>
./gradlew assembleDebug        # JAVA_HOME must point at JDK 17
```

Install on a Fire TV stick (Settings → Developer options → ADB debugging) or
any Android TV / phone. See [`FRICTION_LOG.md`](FRICTION_LOG.md) for the
honest list of things that fought back while building for Fire TV.

## Credits

- *The 1931 Album* (photos p17–p26): scans of a Hungarian family photo album,
  Wikimedia Commons, Public Domain Mark.
- Portrait sample photos p14–p16: [Pexels](https://www.pexels.com), free to
  use.
- AI: Zhipu BigModel GLM family. This project is not affiliated with Amazon;
  it is simply built for their Fire TV track.

## License

MIT-0 — see [LICENSE](LICENSE).
