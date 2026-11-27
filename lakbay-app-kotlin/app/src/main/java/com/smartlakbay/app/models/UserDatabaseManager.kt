package com.smartlakbay.app.models

import android.content.Context
import java.io.File

object UserDatabaseManager {
    private const val SESSION_PREFS = "smart_lakbay_session"

    fun sanitizeEmail(email: String): String {
        return email.lowercase().replace(Regex("[^a-z0-9]"), "_")
    }

    fun getUserFolder(context: Context, email: String): File {
        val rootUserDir = File(context.filesDir, "user")
        if (!rootUserDir.exists()) {
            rootUserDir.mkdirs()
        }
        val userDir = File(rootUserDir, sanitizeEmail(email))
        if (!userDir.exists()) {
            userDir.mkdirs()
        }
        return userDir
    }

    fun saveUserData(context: Context, email: String, name: String, phone: String, avatar: String, lang: String, notifs: Boolean) {
        if (email.isBlank()) return
        val userDir = getUserFolder(context, email)
        val profileFile = File(userDir, "profile.json")
        val content = "name=$name\nemail=$email\nphone=$phone\navatar=$avatar\nlang=$lang\nnotifs=$notifs"
        profileFile.writeText(content)
    }

    fun loadUserData(context: Context, email: String): Map<String, String> {
        if (email.isBlank()) return emptyMap()
        val userDir = getUserFolder(context, email)
        val profileFile = File(userDir, "profile.json")
        if (!profileFile.exists()) return emptyMap()

        val map = mutableMapOf<String, String>()
        profileFile.readLines().forEach { line ->
            val parts = line.split("=", limit = 2)
            if (parts.size == 2) {
                map[parts[0]] = parts[1]
            }
        }
        return map
    }

    fun getCurrentUserEmail(context: Context): String {
        val prefs = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
        return prefs.getString("email", "") ?: ""
    }

    fun syncCurrentSessionToUserFolder(context: Context) {
        val prefs = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
        val email = prefs.getString("email", "") ?: ""
        if (email.isBlank() || email == "guest@lakbay.app") return

        val name = prefs.getString("name", "Traveler") ?: "Traveler"
        val phone = prefs.getString("phone", "+63 912 345 6789") ?: "+63 912 345 6789"
        val avatar = prefs.getString("profile_avatar", "👤") ?: "👤"
        val lang = prefs.getString("lang", "en") ?: "en"
        val notifs = prefs.getBoolean("notifications", true)

        saveUserData(context, email, name, phone, avatar, lang, notifs)
    }

    fun restoreUserSessionFromFolder(context: Context, email: String) {
        if (email.isBlank() || email == "guest@lakbay.app") return
        val data = loadUserData(context, email)
        if (data.isNotEmpty()) {
            val prefs = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            data["name"]?.let { editor.putString("name", it) }
            data["phone"]?.let { editor.putString("phone", it) }
            data["avatar"]?.let { editor.putString("profile_avatar", it) }
            data["lang"]?.let { editor.putString("lang", it) }
            data["notifs"]?.let { editor.putBoolean("notifications", it.toBoolean()) }
            editor.apply()
        }
    }
}
