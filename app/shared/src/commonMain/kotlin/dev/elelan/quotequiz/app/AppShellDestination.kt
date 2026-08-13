package dev.elelan.quotequiz.app

import androidx.navigation3.runtime.NavKey
import dev.elelan.quotequiz.core.session.SessionState
import kotlinx.serialization.Serializable

@Serializable
sealed interface RootRoute : NavKey

@Serializable
data object SplashRoute : RootRoute

@Serializable
data object LoginRoute : RootRoute

@Serializable
data object HomeRoute : RootRoute

fun SessionState.toRootRoute(): RootRoute =
    when (this) {
        SessionState.Loading -> SplashRoute
        SessionState.Unauthenticated -> LoginRoute
        is SessionState.Authenticated -> HomeRoute
    }
