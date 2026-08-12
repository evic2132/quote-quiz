package dev.elelan.quotequiz.core.network

import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

actual fun createPlatformHttpClientEngine(): HttpClientEngineFactory<*> = Android

actual fun createPlatformSettings(): Settings = Settings()
