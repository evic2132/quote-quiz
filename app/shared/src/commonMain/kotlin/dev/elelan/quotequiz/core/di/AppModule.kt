package dev.elelan.quotequiz.core.di

import com.russhwolf.settings.Settings
import dev.elelan.quotequiz.core.api.AuthApi
import dev.elelan.quotequiz.core.api.KtorAuthApi
import dev.elelan.quotequiz.core.api.KtorProfileApi
import dev.elelan.quotequiz.core.api.KtorQuizApi
import dev.elelan.quotequiz.core.api.ProfileApi
import dev.elelan.quotequiz.core.api.QuizApi
import dev.elelan.quotequiz.core.network.ApiConfig
import dev.elelan.quotequiz.core.network.createHttpClient
import dev.elelan.quotequiz.core.network.createPlatformSettings
import dev.elelan.quotequiz.core.session.DefaultSessionRepository
import dev.elelan.quotequiz.core.session.SessionRepository
import dev.elelan.quotequiz.core.storage.SettingsTokenStorage
import dev.elelan.quotequiz.core.storage.TokenStorage
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin(apiBaseUrl: String): KoinApplication =
    startKoin {
        modules(sharedModule(apiBaseUrl))
    }

fun sharedModule(apiBaseUrl: String) =
    module {
        single { ApiConfig(baseUrl = apiBaseUrl) }
        single<Settings> { createPlatformSettings() }
        single { createHttpClient(get()) }

        single<TokenStorage> { SettingsTokenStorage(get()) }

        single<AuthApi> { KtorAuthApi(get()) }
        single<ProfileApi> { KtorProfileApi(get()) }
        single<QuizApi> { KtorQuizApi(get()) }
        single<SessionRepository> { DefaultSessionRepository(tokenStorage = get(), profileApi = get()) }
    }
