package dev.elelan.quotequiz.core.di

import com.russhwolf.settings.Settings
import dev.elelan.quotequiz.core.network.ApiConfig
import dev.elelan.quotequiz.core.network.defaultApiBaseUrl
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single<Settings> { Settings() }
    single<HttpClientEngineFactory<*>> {
        error("JS platform HTTP engine is not configured because this target is not in active use")
    }
    single { ApiConfig(baseUrl = defaultApiBaseUrl()) }
}
