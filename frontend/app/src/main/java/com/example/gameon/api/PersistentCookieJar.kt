package com.example.gameon.api

import android.annotation.SuppressLint
import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar (private val context: Context) : CookieJar {
    val prefsName = "cookies_prefs"
    private val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookieStrings = cookies.map { it.toString() }.toSet()
        prefs.edit().putStringSet(url.host, cookieStrings).apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieStrings = prefs.getStringSet(url.host, emptySet()) ?: return emptyList()
        return cookieStrings.map { Cookie.parse(url, it)!! }
    }

    @SuppressLint("ApplySharedPref")
    fun clear() {
        prefs.edit().clear().commit()
    }
}