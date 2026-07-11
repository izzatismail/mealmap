package com.izzatismail.mealmap.api

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.*
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Security.*

@OptIn(ExperimentalForeignApi::class)
class IosTokenProvider : TokenProvider {
    private var cachedToken: String? = null

    override suspend fun getToken(): String? {
        if (cachedToken != null) return cachedToken
        cachedToken = readFromKeychain()
        return cachedToken
    }

    override suspend fun saveToken(token: String) {
        cachedToken = token
        saveToKeychain(token)
    }

    override suspend fun clearToken() {
        cachedToken = null
        deleteFromKeychain()
    }

    override suspend fun hasToken(): Boolean = getToken() != null

    private fun readFromKeychain(): String? {
        val query = mapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to SERVICE_NAME,
            kSecReturnData to true,
            kSecMatchLimit to kSecMatchLimitOne,
        )

        memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query.toCFDictionary(), result.ptr)
            if (status == errSecSuccess) {
                val data = result.value
                if (data != null) {
                    return NSString.create(data, encoding = NSUTF8StringEncoding) as? String
                }
            }
        }
        return null
    }

    private fun saveToKeychain(token: String) {
        deleteFromKeychain()
        val data = (token as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return
        val query = mapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to SERVICE_NAME,
            kSecValueData to data,
            kSecAttrAccessible to kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
        )

        SecItemAdd(query.toCFDictionary(), null)
    }

    private fun deleteFromKeychain() {
        val query = mapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to SERVICE_NAME,
        )

        SecItemDelete(query.toCFDictionary())
    }

    companion object {
        private const val SERVICE_NAME = "com.izzatismail.mealmap.auth"
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun Map<Any?, Any?>.toCFDictionary(): CFDictionaryRef? {
    val keys = this.keys.map { it as CFTypeRef }.toCValues().ptr
    val values = this.values.map { it as CFTypeRef }.toCValues().ptr
    return CFDictionaryCreate(
        null, keys, values, this.size.toLong(), null, null
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun <T> List<T>.toCValues(): CValuesRef<COpaquePointerVar>? = null