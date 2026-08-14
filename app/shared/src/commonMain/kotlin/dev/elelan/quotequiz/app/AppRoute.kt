package dev.elelan.quotequiz.app

import androidx.navigation3.runtime.NavKey
import dev.elelan.quotequiz.contract.quiz.QuizResultDto
import dev.elelan.quotequiz.core.session.SessionState
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object QuizTab : AppRoute

    @Serializable
    data object SettingsTab : AppRoute

    @Serializable
    data object ProfileTab : AppRoute

    @Serializable
    data class QuizResult(
        val result: QuizResultDto,
    ) : AppRoute
}

val MAIN_TABS: Set<AppRoute> = linkedSetOf(
    AppRoute.QuizTab,
    AppRoute.SettingsTab,
    AppRoute.ProfileTab,
)

fun SessionState.toAppRoute(): AppRoute =
    when (this) {
        SessionState.Loading -> AppRoute.Splash
        SessionState.Unauthenticated -> AppRoute.Login
        is SessionState.Authenticated -> AppRoute.Home
    }
