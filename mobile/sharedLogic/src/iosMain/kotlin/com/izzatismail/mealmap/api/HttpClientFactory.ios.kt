package com.izzatismail.mealmap.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual object HttpClientFactory {
    actual fun create(tokenProvider: TokenProvider?): HttpClient {
        return HttpClient(io.ktor.client.engine.darwin.Darwin) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            if (tokenProvider != null) {
                install(Auth) {
                    bearer {
                        loadTokens {
                            val token = tokenProvider.getToken()
                            if (token != null) BearerTokens(token, "") else null
                        }
                        refreshTokens {
                            tokenProvider.clearToken()
                            null
                        }
                    }
                }
            }
            if (ApiConfig.isDebug) {
                install(Logging) {
                    level = LogLevel.INFO
                }
            }
        }
    }
}