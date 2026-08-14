package dev.elelan.quotequiz.home

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.elelan.quotequiz.app.AppRoute
import dev.elelan.quotequiz.app.MAIN_TABS
import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.feature.profile.ProfileScreen
import dev.elelan.quotequiz.feature.quiz.QuizAction
import dev.elelan.quotequiz.feature.quiz.QuizResultScreen
import dev.elelan.quotequiz.feature.quiz.QuizRouteScreen
import dev.elelan.quotequiz.feature.quiz.QuizViewModel
import dev.elelan.quotequiz.feature.settings.SettingsRouteScreen
import dev.elelan.quotequiz.ui.core.DefaultScaffoldBody
import dev.elelan.quotequiz.ui.core.QuoteQuizBackground
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.action_logout
import quotequiz.app.shared.generated.resources.ic_account_circle
import quotequiz.app.shared.generated.resources.ic_psychology
import quotequiz.app.shared.generated.resources.ic_settings
import quotequiz.app.shared.generated.resources.tab_profile
import quotequiz.app.shared.generated.resources.tab_quiz
import quotequiz.app.shared.generated.resources.tab_settings

private data class HomeTabItem(
    val route: AppRoute,
    val label: String,
    val iconRes: DrawableResource,
)

@Composable
fun HomeContainer(
    user: UserDto,
    onLogout: suspend () -> Unit,
) {
    val navigationState = rememberHomeNavigationState()
    val navigator = remember { HomeNavigator(navigationState) }
    val quizViewModel: QuizViewModel = koinViewModel()
    val saveableDecorator = rememberSaveableStateHolderNavEntryDecorator<AppRoute>()
    val entryDecorators = remember(saveableDecorator) {
        listOf(saveableDecorator)
    }
    val tabs = rememberHomeTabItems()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            HomeBottomBar(
                tabs = tabs,
                selectedTab = navigationState.selectedTab,
                onTabSelected = navigator::selectTab,
            )
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
                entry<AppRoute.QuizTab> {
                    QuizRouteScreen(
                        viewModel = quizViewModel,
                        onQuizCompleted = { result ->
                            navigator.navigate(AppRoute.QuizResult(result))
                        },
                    )
                }
                entry<AppRoute.QuizResult> { route ->
                    QuizResultScreen(
                        result = route.result,
                        onStartAgain = {
                            quizViewModel.onAction(QuizAction.RestartQuizClicked)
                            navigator.goBack()
                        },
                    )
                }
                entry<AppRoute.SettingsTab> { SettingsRouteScreen() }
                entry<AppRoute.ProfileTab> {
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
private fun rememberHomeTabItems(): List<HomeTabItem> = listOf(
    HomeTabItem(
        route = AppRoute.QuizTab,
        label = stringResource(Res.string.tab_quiz),
        iconRes = Res.drawable.ic_psychology,
    ),
    HomeTabItem(
        route = AppRoute.SettingsTab,
        label = stringResource(Res.string.tab_settings),
        iconRes = Res.drawable.ic_settings,
    ),
    HomeTabItem(
        route = AppRoute.ProfileTab,
        label = stringResource(Res.string.tab_profile),
        iconRes = Res.drawable.ic_account_circle,
    ),
)

@Composable
private fun HomeBottomBar(
    tabs: List<HomeTabItem>,
    selectedTab: AppRoute,
    onTabSelected: (AppRoute) -> Unit,
) {
    NavigationBar {
        tabs.forEach { tab ->
            MainNavigationBarItem(
                label = tab.label,
                iconRes = tab.iconRes,
                selected = tab.route == selectedTab,
                onClick = { onTabSelected(tab.route) },
            )
        }
    }
}

@Composable
private fun RowScope.MainNavigationBarItem(
    label: String,
    iconRes: DrawableResource,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp),
            )
        },
        label = {
            Text(label)
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.tertiary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
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
