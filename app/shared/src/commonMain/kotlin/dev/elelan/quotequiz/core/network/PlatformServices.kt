package dev.elelan.quotequiz.core.network

import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngineFactory

expect fun createPlatformHttpClientEngine(): HttpClientEngineFactory<*>

expect fun createPlatformSettings(): Settings
