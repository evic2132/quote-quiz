package dev.elelan.quotequiz.feature.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.ui.core.DefaultScaffoldBody
import dev.elelan.quotequiz.ui.core.QuoteQuizBackground
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.app_title
import quotequiz.app.shared.generated.resources.settings_daily_challenge_description
import quotequiz.app.shared.generated.resources.settings_daily_challenge_label
import quotequiz.app.shared.generated.resources.settings_description
import quotequiz.app.shared.generated.resources.settings_difficulty_description
import quotequiz.app.shared.generated.resources.settings_difficulty_label
import quotequiz.app.shared.generated.resources.settings_mode_binary
import quotequiz.app.shared.generated.resources.settings_mode_label
import quotequiz.app.shared.generated.resources.settings_mode_multiple_choice
import quotequiz.app.shared.generated.resources.settings_mode_note
import quotequiz.app.shared.generated.resources.settings_mode_standard
import quotequiz.app.shared.generated.resources.settings_mode_subtitle
import quotequiz.app.shared.generated.resources.settings_title

@Composable
fun SettingsRouteScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is SettingsUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message.asString())
            }
        }
    }

    SettingsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (SettingsAction) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        QuoteQuizBackground()
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
        ) { innerPadding ->
            DefaultScaffoldBody(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding(),
                enableEdgeToEdge = true,
            ) {
                SettingsContent(
                    uiState = uiState,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
) {
    val spacing = QuoteQuizTheme.spacing
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .widthIn(max = 640.dp)
            .padding(horizontal = spacing.marginMobile, vertical = spacing.stackLg),
        verticalArrangement = Arrangement.spacedBy(spacing.stackLg),
    ) {
        Text(
            text = stringResource(Res.string.app_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.stackSm),
        ) {
            Text(
                text = stringResource(Res.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(Res.string.settings_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column {
                SettingSection(
                    title = stringResource(Res.string.settings_mode_label),
                    description = stringResource(Res.string.settings_mode_subtitle),
                ) {
                    ModeSegmentedControl(
                        selectedMode = uiState.selectedMode,
                        onModeSelected = { onAction(SettingsAction.ModeSelected(it)) },
                    )
                    WarningNote(text = stringResource(Res.string.settings_mode_note))
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                SettingRow(
                    title = stringResource(Res.string.settings_difficulty_label),
                    description = stringResource(Res.string.settings_difficulty_description),
                    trailing = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(Res.string.settings_mode_standard),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "›",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    onClick = { onAction(SettingsAction.DifficultyClicked) },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                SettingRow(
                    title = stringResource(Res.string.settings_daily_challenge_label),
                    description = stringResource(Res.string.settings_daily_challenge_description),
                    trailing = {
                        Switch(
                            checked = uiState.dailyChallengeEnabled,
                            onCheckedChange = { onAction(SettingsAction.DailyChallengeClicked) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        )
                    },
                    onClick = { onAction(SettingsAction.DailyChallengeClicked) },
                )
            }
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = QuoteQuizTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(spacing.stackMd),
    ) {
        SettingTexts(title = title, description = description)
        content()
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    trailing: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SettingTexts(title = title, description = description)
        }
        trailing()
    }
}

@Composable
private fun SettingTexts(
    title: String,
    description: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ModeSegmentedControl(
    selectedMode: QuizMode,
    onModeSelected: (QuizMode) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ModeSegmentButton(
                label = stringResource(Res.string.settings_mode_binary),
                selected = selectedMode == QuizMode.BINARY,
                onClick = { onModeSelected(QuizMode.BINARY) },
                modifier = Modifier.weight(1f),
            )
            ModeSegmentButton(
                label = stringResource(Res.string.settings_mode_multiple_choice),
                selected = selectedMode == QuizMode.MULTIPLE_CHOICE,
                onClick = { onModeSelected(QuizMode.MULTIPLE_CHOICE) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ModeSegmentButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun WarningNote(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.10f),
                shape = RoundedCornerShape(10.dp),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "i",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    QuoteQuizTheme {
        SettingsScreen(
            uiState = SettingsUiState(selectedMode = QuizMode.BINARY),
            snackbarHostState = SnackbarHostState(),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun SettingsScreenDarkPreview() {
    QuoteQuizTheme(darkTheme = true) {
        SettingsScreen(
            uiState = SettingsUiState(selectedMode = QuizMode.MULTIPLE_CHOICE),
            snackbarHostState = SnackbarHostState(),
            onAction = {},
        )
    }
}
