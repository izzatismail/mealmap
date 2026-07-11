package com.izzatismail.mealmap.api

import platform.Foundation.NSUserDefaults

/**
 * iOS TokenProvider using NSUserDefaults for MVP.
 *
 * NOTE: For production, replace with Keychain via a Swift helper exposed to Kotlin/Native.
 * See: https://developer.apple.com/documentation/security/keychain_services
 * Kotlin 2.4.0 cinterop changes make direct CFDictionary construction unreliable.
 * Solution: Write a small Swift helper in iosApp/ and expose via @ObjCName.
 */
class IosTokenProvider : TokenProvider {
    private var cachedToken: String? = null

    override suspend fun getToken(): String? {
        if (cachedToken != null) return cachedToken
        cachedToken = NSUserDefaults.standardUserDefaults.stringForKey(KEY)
        return cachedToken
    }

    override suspend fun saveToken(token: String) {
        cachedToken = token
        NSUserDefaults.standardUserDefaults.setObject(token, forKey = KEY)
        NSUserDefaults.standardUserDefaults.synchronize()
    }

    override suspend fun clearToken() {
        cachedToken = null
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY)
        NSUserDefaults.standardUserDefaults.synchronize()
    }

    override suspend fun hasToken(): Boolean = getToken() != null

    companion object {
        private const val KEY = "com.izzatismail.mealmap.auth.token"
    }
}