package com.izzatismail.mealmap.api

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AndroidTokenProvider(context: Context) : TokenProvider {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "mealmap_auth",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override suspend fun getToken(): String? = prefs.getString(TOKEN_KEY, null)

    override suspend fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    override suspend fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }

    override suspend fun hasToken(): Boolean = prefs.contains(TOKEN_KEY)

    companion object {
        private const val TOKEN_KEY = "jwt_token"
    }
}