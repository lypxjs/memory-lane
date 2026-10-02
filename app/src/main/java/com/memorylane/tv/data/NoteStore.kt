package com.memorylane.tv.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import org.json.JSONObject
import java.io.File

/**
 * Family-recorded memory notes, persisted locally.
 *
 * SampleAlbum notes stand in for demo content; a note recorded (or typed) by
 * the family overrides the sample for that photo and becomes the single
 * source of truth the story engine retells. Observable via Compose state so
 * the grid/detail screens refresh the moment a note is saved.
 */
object NoteStore {

    class Entry(val note: String, val audioPath: String?)

    private val loaded = mutableStateOf(false)
    private val byId = mutableStateMapOf<String, Entry>()

    private lateinit var file: File

    fun init(context: Context) {
        if (loaded.value) return
        file = File(context.filesDir, "family_notes.json")
        try {
            if (file.exists()) {
                val obj = JSONObject(file.readText())
                for (key in obj.keys()) {
                    val e = obj.getJSONObject(key)
                    byId[key] = Entry(e.getString("note"), e.optString("audio", null))
                }
            }
        } catch (_: Exception) {
        }
        loaded.value = true
    }

    /** The note the story engine must retell: family-recorded first, sample fallback. */
    fun effectiveNote(photo: Photo): String? {
        val own = byId[photo.id]?.note
        return own?.takeIf { it.isNotBlank() } ?: photo.memoryNote
    }

    fun isFamilyRecorded(photoId: String): Boolean = byId.containsKey(photoId)

    fun audioPath(photoId: String): String? = byId[photoId]?.audioPath

    fun set(context: Context, photoId: String, note: String, audioPath: String?) {
        byId[photoId] = Entry(note, audioPath)
        persist()
    }

    private fun persist() {
        if (!this::file.isInitialized) return
        try {
            val obj = JSONObject()
            for ((k, e) in byId) {
                obj.put(k, JSONObject().put("note", e.note).put("audio", e.audioPath ?: ""))
            }
            file.writeText(obj.toString())
        } catch (_: Exception) {
        }
    }
}
