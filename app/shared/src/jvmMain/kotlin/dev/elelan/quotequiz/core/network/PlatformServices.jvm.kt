package dev.elelan.quotequiz.core.network

import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun createPlatformHttpClientEngine(): HttpClientEngineFactory<*> = CIO

actual fun createPlatformSettings(): Settings = Settings()
