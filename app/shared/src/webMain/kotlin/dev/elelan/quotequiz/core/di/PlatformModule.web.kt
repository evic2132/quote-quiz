package dev.elelan.quotequiz.core.di

import com.russhwolf.settings.Settings
import dev.elelan.quotequiz.core.network.ApiConfig
import dev.elelan.quotequiz.core.network.defaultApiBaseUrl
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val browserHttpClientEngineFactory: HttpClientEngineFactory<*>

internal actual val platformModule: Module = module {
    single<HttpClientEngineFactory<*>> { browserHttpClientEngineFactory }
    single<Settings> { Settings() }
    single { ApiConfig(baseUrl = defaultApiBaseUrl()) }
}
