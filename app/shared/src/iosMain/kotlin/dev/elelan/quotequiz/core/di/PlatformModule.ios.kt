package dev.elelan.quotequiz.core.di

import com.russhwolf.settings.Settings
import dev.elelan.quotequiz.core.network.ApiConfig
import dev.elelan.quotequiz.core.network.defaultApiBaseUrl
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single<HttpClientEngineFactory<*>> { Darwin }
    single<Settings> { Settings() }
    single { ApiConfig(baseUrl = defaultApiBaseUrl()) }
}
