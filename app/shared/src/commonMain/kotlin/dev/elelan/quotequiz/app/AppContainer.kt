package dev.elelan.quotequiz.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.elelan.quotequiz.core.session.SessionRepository
import dev.elelan.quotequiz.core.session.SessionState
import dev.elelan.quotequiz.feature.login.LoginRouteScreen
import dev.elelan.quotequiz.feature.splash.SplashScreen
import dev.elelan.quotequiz.home.HomeContainer
import dev.elelan.quotequiz.ui.core.DefaultScaffoldBody
import dev.elelan.quotequiz.ui.core.QuoteQuizBackground
import org.koin.compose.koinInject

@Composable
fun AppContainer() {
    val sessionRepository = koinInject<SessionRepository>()
    val sessionState by sessionRepository.sessionState.collectAsState()
    val rootNavigationState = rememberRootNavigationState()
    val rootNavigator = remember(rootNavigationState) { RootNavigator(rootNavigationState) }

    LaunchedEffect(sessionRepository) {
        sessionRepository.restoreSession()
    }

    LaunchedEffect(sessionState, rootNavigator) {
        rootNavigator.moveTo(sessionState.toAppRoute())
    }

    NavDisplay(
        backStack = rootNavigationState.backStack,
        onBack = rootNavigator::goBack,
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        entryProvider = entryProvider {
            entry<AppRoute.Splash> { SplashScreen() }
            entry<AppRoute.Login> { LoginRouteScreen() }
            entry<AppRoute.Home> {
                val authenticatedState = sessionState as? SessionState.Authenticated
                    ?: return@entry
                HomeContainer(
                    user = authenticatedState.user,
                    onLogout = { sessionRepository.logout() },
                )
            }
        },
    )
}