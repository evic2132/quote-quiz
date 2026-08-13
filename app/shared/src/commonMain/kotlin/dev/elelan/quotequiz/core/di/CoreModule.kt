package dev.elelan.quotequiz.core.di

import dev.elelan.quotequiz.core.api.AuthApi
import dev.elelan.quotequiz.core.api.KtorAuthApi
import dev.elelan.quotequiz.core.api.KtorProfileApi
import dev.elelan.quotequiz.core.api.KtorQuizApi
import dev.elelan.quotequiz.core.api.ProfileApi
import dev.elelan.quotequiz.core.api.QuizApi
import dev.elelan.quotequiz.core.network.createHttpClient
import dev.elelan.quotequiz.core.session.DefaultSessionRepository
import dev.elelan.quotequiz.core.session.SessionRepository
import dev.elelan.quotequiz.core.storage.SettingsTokenStorage
import dev.elelan.quotequiz.core.storage.TokenStorage
import dev.elelan.quotequiz.feature.quiz.DefaultQuizRepository
import dev.elelan.quotequiz.feature.quiz.QuizRepository
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.dsl.module

val coreModule = module {
    single { createHttpClient(apiConfig = get(), engineFactory = get<HttpClientEngineFactory<*>>()) }
    single<TokenStorage> { SettingsTokenStorage(get()) }
    single<AuthApi> { KtorAuthApi(get()) }
    single<ProfileApi> { KtorProfileApi(get()) }
    single<QuizApi> { KtorQuizApi(get()) }
    single<SessionRepository> { DefaultSessionRepository(tokenStorage = get(), profileApi = get()) }
    single<QuizRepository> { DefaultQuizRepository(quizApi = get(), sessionRepository = get()) }
}
