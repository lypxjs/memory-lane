# Memory Lane — Demo Video Script (3:00)

> Voiceover: calm, warm English. Screen: Android TV / Fire TV 1080p recording.
> One line appears on screen as text where marked [TEXT].

| Time | Screen | Voiceover |
|------|--------|-----------|
| 0:00–0:15 | Album grid, slow D-pad drift across photo cards | "This is a family photo album on the TV. Every photo has a story — but most of those stories live only in someone's head. When they're gone, the stories are gone." |
| 0:15–0:35 | Open *Golden Hour by the Bay*; story types in; TTS reads it aloud | "Memory Lane turns the album into a storyteller. Open any photo and it retells the memory behind it — out loud, in a warm voice, from the couch." |
| 0:35–0:55 | Hold on the caption **[TEXT] "Retold from a family recording — the AI only polished the words."** | "And here is the rule that makes this different from every AI photo toy out there: the story must be TRUE. The app never invents anything. It only retells what a family member actually recorded." |
| 0:55–1:30 | Press *Record a memory* → recording screen → speak (any language, e.g. Chinese) → *Turn voice into words* → transcript appears in the editable note | "Recording a memory is one button. Dad speaks — in his own language — and his words become the photo's memory note. He can fix a word before saving, or simply type the note if there is no microphone. The family's voice is the source of truth. The AI is only the ghostwriter." |
| 1:30–2:00 | Back on the detail screen: a new story retells Dad's note; TTS reads it | "And now the album retells HIS story — his facts, his little joke, nothing added. What Grandma hears from the couch is still her family's memory. Polished, but true." |
| 2:00–2:20 | Open *Waiting for a Story* — the photo nobody recorded | "And when nobody has told a photo's story yet? Memory Lane says so. It will not make one up. That honesty is the product." **[TEXT] "The story must be yours."** |
| 2:20–2:45 | Tech b-roll: code slides or split screen of the three engines | "Under the hood: GLM-4V reads the photo, GLM-ASR transcribes the family's voice, and on-device TTS reads the story aloud — all behind a degradation ladder, so the demo never dies. Vision falls back to text, network falls back to local. The album lives on your TV; only the photo being narrated travels to the AI." |
| 2:45–3:00 | End card: Memory Lane + tagline | "Memory Lane. Every photo has a story. Every story starts with a real voice." |

## Recording notes
- TV screen capture: `adb screenrecord` on the FireTV36 emulator (1080p), audio captured separately.
- Voiceover track: record after the screen cut is final; if the live ASR demo is unavailable (glm-asr balance), use the typed-note path on camera — the script line already covers it.
- Chinese voice line in scene 4: a real family recording is ideal; fallback is a TTS line played through speakers into the mic.
