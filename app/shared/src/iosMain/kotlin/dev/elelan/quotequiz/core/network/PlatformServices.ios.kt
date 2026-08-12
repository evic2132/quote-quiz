package dev.elelan.quotequiz.core.network

import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformHttpClientEngine(): HttpClientEngineFactory<*> = Darwin

actual fun createPlatformSettings(): Settings = Settings()
