package dev.elelan.quotequiz.home

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.elelan.quotequiz.app.MAIN_TABS
import dev.elelan.quotequiz.app.ProfileTab
import dev.elelan.quotequiz.app.QuizResultRoute
import dev.elelan.quotequiz.app.QuizTab
import dev.elelan.quotequiz.app.SettingsTab
import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.feature.profile.ProfileScreen
import dev.elelan.quotequiz.feature.quiz.QuizScreen
import dev.elelan.quotequiz.feature.quiz.QuizResultScreen
import dev.elelan.quotequiz.feature.settings.SettingsScreen
import dev.elelan.quotequiz.ui.core.DefaultScaffoldBody
import dev.elelan.quotequiz.ui.core.QuoteQuizBackground
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.action_logout
import quotequiz.app.shared.generated.resources.tab_profile
import quotequiz.app.shared.generated.resources.tab_quiz
import quotequiz.app.shared.generated.resources.tab_settings

@Composable
fun HomeContainer(
    user: UserDto,
    onLogout: suspend () -> Unit,
) {
    val navigationState = rememberHomeNavigationState()
    val navigator = remember { HomeNavigator(navigationState) }
    val saveableDecorator = rememberSaveableStateHolderNavEntryDecorator<NavKey>()
    val entryDecorators = remember(saveableDecorator) {
        listOf(saveableDecorator)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
            onBack = navigator::goBack,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 180)) togetherWith
                    ExitTransition.KeepUntilTransitionsFinished
            },
            popTransitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 180)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 120))
            },
            predictivePopTransitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 180)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 120))
            },
            entryDecorators = entryDecorators,
            entryProvider = entryProvider {
                entry<QuizTab> {
                    QuizScreen(
                        onOpenResult = { navigator.navigate(QuizResultRoute) },
                    )
                }
                entry<QuizResultRoute> {
                    QuizResultScreen()
                }
                entry<SettingsTab> { SettingsScreen() }
                entry<ProfileTab> {
                    ProfileScreen(
                        name = user.name,
                        email = user.email,
                        onLogout = onLogout,
                    )
                }
            },
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun NavKey.tabLabel(): String =
    when (this) {
        QuizTab -> stringResource(Res.string.tab_quiz)
        SettingsTab -> stringResource(Res.string.tab_settings)
        ProfileTab -> stringResource(Res.string.tab_profile)
        else -> error("Unknown tab: $this")
    }

@Composable
internal fun HomePlaceholderScreen(
    title: String,
    body: String,
    footer: @Composable (() -> Unit)? = null,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        QuoteQuizBackground()
        DefaultScaffoldBody(
            modifier = Modifier.fillMaxSize(),
            enableEdgeToEdge = true,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
    }
}

@Composable
internal fun LogoutButton(onLogout: suspend () -> Unit) {
    val scope = rememberCoroutineScope()
    Button(onClick = { scope.launch { onLogout() } }) {
        Text(stringResource(Res.string.action_logout))
    }
}
