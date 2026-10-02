package com.memorylane.tv.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * The family roster: who the app should recognise in photos.
 *
 * A member is a name plus one reference photo (from the album) whose face
 * shows them clearly. Identification happens in the cloud together with
 * narration - the reference photos travel with the request, nothing else
 * about the family is stored anywhere but this file on the device.
 */
object FamilyStore {

    data class Member(val id: String, val name: String, val refPhotoId: String)

    val members = mutableStateListOf<Member>()
    private lateinit var file: File

    fun init(context: Context) {
        if (this::file.isInitialized) return
        file = File(context.filesDir, "family.json")
        try {
            if (file.exists()) {
                val arr = JSONArray(file.readText())
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    members.add(Member(o.getString("id"), o.getString("name"), o.getString("ref")))
                }
            }
        } catch (_: Exception) {
        }
    }

    fun add(name: String, refPhotoId: String) {
        members.add(Member(System.currentTimeMillis().toString(16), name.trim(), refPhotoId))
        persist()
    }

    fun remove(id: String) {
        members.removeAll { it.id == id }
        persist()
    }

    fun refPhoto(): Photo? = null // resolved by caller against the album

    fun snapshot(): List<Member> = members.toList()

    private fun persist() {
        if (!this::file.isInitialized) return
        try {
            val arr = JSONArray()
            for (m in members) {
                arr.put(JSONObject().put("id", m.id).put("name", m.name).put("ref", m.refPhotoId))
            }
            file.writeText(arr.toString())
        } catch (_: Exception) {
        }
    }
}
