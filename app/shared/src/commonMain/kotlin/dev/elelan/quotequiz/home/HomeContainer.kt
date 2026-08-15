package dev.elelan.quotequiz.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.elelan.quotequiz.app.AppRoute
import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.feature.profile.ProfileScreen
import dev.elelan.quotequiz.feature.quiz.QuizAction
import dev.elelan.quotequiz.feature.quiz.QuizResultScreen
import dev.elelan.quotequiz.feature.quiz.QuizRouteScreen
import dev.elelan.quotequiz.feature.quiz.QuizViewModel
import dev.elelan.quotequiz.feature.settings.SettingsRouteScreen
import dev.elelan.quotequiz.ui.core.AdaptiveWindowLayout
import dev.elelan.quotequiz.ui.core.DefaultScaffoldBody
import dev.elelan.quotequiz.ui.core.QuoteQuizBackground
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.action_logout
import quotequiz.app.shared.generated.resources.app_title
import quotequiz.app.shared.generated.resources.ic_account_circle
import quotequiz.app.shared.generated.resources.ic_dock_to_left
import quotequiz.app.shared.generated.resources.ic_psychology
import quotequiz.app.shared.generated.resources.ic_settings
import quotequiz.app.shared.generated.resources.ic_view_sidebar
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
    val tabs = rememberHomeTabItems()

    AdaptiveWindowLayout(
        compactContent = {
            HomeCompactShell(
                tabs = tabs,
                selectedTab = navigationState.selectedTab,
                onTabSelected = navigator::selectTab,
                content = {
                    HomeNavHost(
                        navigationState = navigationState,
                        navigator = navigator,
                        quizViewModel = quizViewModel,
                        user = user,
                        onLogout = onLogout,
                        modifier = it,
                    )
                },
            )
        },
        mediumContent = {
            HomeWideShell(
                tabs = tabs,
                selectedTab = navigationState.selectedTab,
                onTabSelected = navigator::selectTab,
                canToggleSidebar = false,
                initiallyExpanded = false,
                content = {
                    HomeNavHost(
                        navigationState = navigationState,
                        navigator = navigator,
                        quizViewModel = quizViewModel,
                        user = user,
                        onLogout = onLogout,
                        modifier = it,
                    )
                },
            )
        },
        expandedContent = {
            HomeWideShell(
                tabs = tabs,
                selectedTab = navigationState.selectedTab,
                onTabSelected = navigator::selectTab,
                canToggleSidebar = true,
                initiallyExpanded = true,
                content = {
                    HomeNavHost(
                        navigationState = navigationState,
                        navigator = navigator,
                        quizViewModel = quizViewModel,
                        user = user,
                        onLogout = onLogout,
                        modifier = it,
                    )
                },
            )
        },
    )
}

@Composable
private fun HomeCompactShell(
    tabs: List<HomeTabItem>,
    selectedTab: AppRoute,
    onTabSelected: (AppRoute) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        bottomBar = {
            HomeBottomBar(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
            )
        },
    ) { innerPadding ->
        HomeContentShell(
            modifier = Modifier.padding(innerPadding),
            horizontalPadding = QuoteQuizTheme.spacing.marginMobile,
        ) {
            content(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun HomeWideShell(
    tabs: List<HomeTabItem>,
    selectedTab: AppRoute,
    onTabSelected: (AppRoute) -> Unit,
    canToggleSidebar: Boolean = false,
    initiallyExpanded: Boolean = true,
    content: @Composable (Modifier) -> Unit,
) {

    var userExpanded by rememberSaveable(canToggleSidebar) {
        mutableStateOf(initiallyExpanded)
    }

    val isExpanded = canToggleSidebar && userExpanded

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        QuoteQuizBackground()
        Row(
            modifier = Modifier
                .fillMaxSize().navigationBarsPadding(),
        ) {

            HomeSidebar(
                tabs = tabs,
                selectedTab = selectedTab,
                canToggle = canToggleSidebar,
                isExpanded = isExpanded,
                onExpandedChange = {
                    userExpanded = !userExpanded
                },
                onTabSelected = onTabSelected,
            )

            //content(Modifier.weight(1f).fillMaxHeight())
            HomeContentShell(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalPadding = QuoteQuizTheme.spacing.marginTablet,
            ) {
                content(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun HomeSidebar(
    tabs: List<HomeTabItem>,
    selectedTab: AppRoute,
    canToggle: Boolean,
    isExpanded: Boolean,
    onExpandedChange: () -> Unit,
    onTabSelected: (AppRoute) -> Unit,
) {
    val sidebarWidth by animateDpAsState(
        targetValue = if (isExpanded) 220.dp else 80.dp,
        label = "sidebarWidth",
    )
    Box(
        modifier = Modifier
            .width(sidebarWidth)
            .fillMaxHeight(),
    ) {

        Surface(
            modifier = Modifier
                .width(sidebarWidth)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
            ) {

                Spacer(
                    Modifier.height(
                        if (canToggle) 24.dp else 48.dp
                    )
                )

                if (canToggle) {
                    SidebarToggle(
                        isExpanded = isExpanded,
                        onClick = onExpandedChange,
                    )

                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tabs.forEach { tab ->
                        SidebarItem(
                            tab = tab,
                            selected = tab.route == selectedTab,
                            expanded = isExpanded,
                            onClick = {
                                onTabSelected(tab.route)
                            },
                        )
                    }
                }
            }
        }

        VerticalDivider(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun SidebarItem(
    tab: HomeTabItem,
    selected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (expanded) {
                Arrangement.Start
            } else {
                Arrangement.Center
            },
        ) {
            Icon(
                painter = painterResource(tab.iconRes),
                contentDescription = tab.label,
                tint = contentColor,
                modifier = Modifier.size(24.dp),
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
            ) {
                Row {
                    Spacer(Modifier.width(16.dp))

                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarToggle(
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    val painter =
        if (isExpanded) painterResource(Res.drawable.ic_dock_to_left) else painterResource(Res.drawable.ic_view_sidebar)
    Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        contentAlignment = if (isExpanded) {
            Alignment.CenterEnd
        } else {
            Alignment.Center
        },
    ) {
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)

            )
        }
    }
}

@Composable
private fun HomeContentShell(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp,
    content: @Composable () -> Unit,
) {
    val spacing = QuoteQuizTheme.spacing

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(Res.string.app_title),
            style = MaterialTheme.typography.displayMedium.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = spacing.stackLg,
                bottom = spacing.stackMd,
            ),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            content()
        }
    }
}

@Composable
private fun HomeNavHost(
    navigationState: HomeNavigationState,
    navigator: HomeNavigator,
    quizViewModel: QuizViewModel,
    user: UserDto,
    onLogout: suspend () -> Unit,
    modifier: Modifier = Modifier,
) {
    val entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator<AppRoute>())

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
        modifier = modifier,
    )
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