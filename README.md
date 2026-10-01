# Memory Lane

**Every photo has a story. Sit back and listen.**

Memory Lane is a Fire TV app that turns a family photo album into a couch
experience for people who find phones small and apps complicated — parents,
grandparents. Photos appear on the big screen; an AI narrator writes and reads
a short story about each one. No typing, no menus three levels deep: D-pad to
move, OK to open, back to return.

Built for the [Build, Ship, Shape: Amazon Developer Hackathon](https://amazonappdev2026.devpost.com/)
(Fire TV track, October 2026).

## Features

- **Photo wall** — a 12-photo sample album, D-pad navigable, TV-distance legible
- **AI narration** — each photo gets a warm 4-6 sentence story (Amazon Bedrock
  + Amazon Nova once AWS credentials are configured; an offline mock engine
  powers development builds)
- **Zero-setup demo** — the album is bundled, so the demo never depends on a
  network or an account

## Tech

- Kotlin + Jetpack Compose (`androidx.tv:tv-material`) — native Fire OS path
- Navigation Compose, two screens (album grid → photo detail)
- `StoryEngine` interface isolates the AI layer: `MockStoryEngine` (offline)
  today, `BedrockStoryEngine` (Amazon Nova) behind the same contract

## Build

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Requirements: JDK 17, Android SDK (compileSdk 34). Point `local.properties`
at your SDK, or set `ANDROID_HOME`.

## Roadmap

- [x] Project scaffold, photo grid, D-pad focus navigation
- [x] Story engine abstraction + offline mock
- [ ] Amazon Bedrock / Nova narration (AWS Builder mini challenge)
- [ ] Spoken narration via on-device TTS
- [ ] Voice questions: "when was this taken?" (multi-modal UX)
- [ ] Family album sync from phone (S3-backed)

## License

[MIT](LICENSE)
