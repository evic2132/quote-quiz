package dev.elelan.quotequiz.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.core.di.initKoin
import dev.elelan.quotequiz.core.network.defaultApiBaseUrl
import dev.elelan.quotequiz.core.session.SessionRepository
import dev.elelan.quotequiz.core.session.SessionState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppShell() {
    rememberKoinApp()

    val sessionRepository = koinInject<SessionRepository>()
    val sessionState by sessionRepository.sessionState.collectAsState()
    val rootNavigationState = rememberRootNavigationState()
    val rootNavigator = remember(rootNavigationState) { RootNavigator(rootNavigationState) }

    LaunchedEffect(sessionRepository) {
        sessionRepository.restoreSession()
    }

    LaunchedEffect(sessionState, rootNavigator) {
        rootNavigator.moveTo(sessionState.toRootRoute())
    }

    NavDisplay(
        backStack = rootNavigationState.backStack,
        onBack = rootNavigator::goBack,
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        entryProvider = entryProvider {
            entry<SplashRoute> { SplashScreen() }
            entry<LoginRoute> {
                LoginPlaceholderScreen(
                    onLogin = {
                        sessionRepository.persistSession(
                            token = PLACEHOLDER_SESSION_TOKEN,
                            user = PLACEHOLDER_USER,
                        )
                    },
                )
            }
            entry<HomeRoute> {
                val authenticatedState = sessionState as? SessionState.Authenticated
                if (authenticatedState == null) {
                    SplashScreen()
                } else {
                    MainShell(
                        authenticatedState = authenticatedState,
                        onLogout = { sessionRepository.logout() },
                    )
                }
            }
        }
    )
}

@Composable
private fun rememberKoinApp() {
    remember {
        if (!KoinHolder.started) {
            initKoin(apiBaseUrl = defaultApiBaseUrl())
            KoinHolder.started = true
        }
    }
}

@Composable
private fun MainShell(
    authenticatedState: SessionState.Authenticated,
    onLogout: suspend () -> Unit,
) {
    val navigationState = rememberMainShellNavigationState()
    val navigator = remember { MainShellNavigator(navigationState) }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            NavigationBar {
                MAIN_TABS.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == navigationState.selectedTab,
                        onClick = { navigator.selectTab(tab) },
                        icon = {},
                        label = { Text(tab.tabLabel()) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavDisplay(
            backStack = navigationState.currentBackStack,
            onBack = { navigator.goBack() },
            entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
            entryProvider = entryProvider {
            entry<QuizTab> { QuizPlaceholderScreen() }
            entry<SettingsTab> { SettingsPlaceholderScreen() }
            entry<ProfileTab> {
                ProfilePlaceholderScreen(
                    name = authenticatedState.user.name,
                        email = authenticatedState.user.email,
                        onLogout = onLogout,
                    )
                }
            },
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun SplashScreen() {
    PlaceholderScreen(
        title = "Loading session",
        body = "Checking whether a saved login can restore the app session.",
    )
}

@Composable
private fun LoginPlaceholderScreen(
    onLogin: suspend () -> Unit,
) {
    PlaceholderScreen(
        title = "Login",
        body = "Temporary Task 7 entry point. Task 8 will replace this with the real login form.",
        footer = {
            LoginButton(onLogin = onLogin)
        },
    )
}

@Composable
private fun QuizPlaceholderScreen() {
    PlaceholderScreen(
        title = "Quiz",
        body = "Task 9 will implement the binary and multiple-choice quiz experience here.",
    )
}

@Composable
private fun SettingsPlaceholderScreen() {
    PlaceholderScreen(
        title = "Settings",
        body = "Task 10 will add quiz mode controls and restart behavior here.",
    )
}

@Composable
private fun ProfilePlaceholderScreen(
    name: String,
    email: String,
    onLogout: suspend () -> Unit,
) {
    PlaceholderScreen(
        title = "Profile",
        body = "Signed in as $name\n$email",
        footer = {
            LogoutButton(onLogout = onLogout)
        },
    )
}

@Composable
private fun PlaceholderScreen(
    title: String,
    body: String,
    footer: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            footer?.invoke()
        }
    }
}

@Composable
private fun LogoutButton(onLogout: suspend () -> Unit) {
    val scope = rememberCoroutineScope()
    Button(onClick = { scope.launch { onLogout() } }) {
        Text("Logout")
    }
}

@Composable
private fun LoginButton(onLogin: suspend () -> Unit) {
    val scope = rememberCoroutineScope()
    Button(onClick = { scope.launch { onLogin() } }) {
        Text("Enter App")
    }
}

private fun NavKey.tabLabel(): String =
    when (this) {
        QuizTab -> "Quiz"
        SettingsTab -> "Settings"
        ProfileTab -> "Profile"
        else -> error("Unknown tab: $this")
    }

private object KoinHolder {
    var started: Boolean = false
}
private const val PLACEHOLDER_SESSION_TOKEN = "task-7-placeholder-token"

private val PLACEHOLDER_USER = UserDto(
    id = 1,
    name = "Demo User",
    email = "demo@example.com",
)
