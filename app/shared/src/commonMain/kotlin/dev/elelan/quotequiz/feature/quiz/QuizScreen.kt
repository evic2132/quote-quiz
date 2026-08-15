package dev.elelan.quotequiz.feature.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.QuizOptionDto
import dev.elelan.quotequiz.contract.quiz.QuizQuestionDto
import dev.elelan.quotequiz.contract.quiz.QuizResultDto
import dev.elelan.quotequiz.ui.core.AdaptiveWindowLayout
import dev.elelan.quotequiz.ui.core.DefaultScaffoldBody
import dev.elelan.quotequiz.ui.core.FormFactorPreviews
import dev.elelan.quotequiz.ui.core.QuoteQuizBackground
import dev.elelan.quotequiz.ui.core.shimmerPlaceholder
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.app_title
import quotequiz.app.shared.generated.resources.quiz_action_no
import quotequiz.app.shared.generated.resources.quiz_action_retry
import quotequiz.app.shared.generated.resources.quiz_action_yes
import quotequiz.app.shared.generated.resources.quiz_binary_prompt
import quotequiz.app.shared.generated.resources.quiz_multiple_choice_prompt
import quotequiz.app.shared.generated.resources.quiz_progress
import quotequiz.app.shared.generated.resources.quiz_state_error

@Composable
fun QuizRouteScreen(
    onQuizCompleted: (QuizResultDto) -> Unit,
    viewModel: QuizViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.result) {
        uiState.result?.let(onQuizCompleted)
    }

    QuizScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
fun QuizScreen(
    uiState: QuizUiState,
    onAction: (QuizAction) -> Unit,
) {
    var showSubmittingOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSubmitting, uiState.feedbackDialog) {
        showSubmittingOverlay = false
        if (uiState.isSubmitting && uiState.feedbackDialog == null) {
            delay(140)
            showSubmittingOverlay = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        QuoteQuizBackground()
        DefaultScaffoldBody(
            modifier = Modifier
                .fillMaxSize(),
            shouldOverlayBlur = uiState.feedbackDialog != null || showSubmittingOverlay,
        ) {
            AdaptiveWindowLayout(
                compactContent = {
                    QuizScreenStateHost(
                        uiState = uiState,
                        onAction = onAction,
                        contentMaxWidth = 640.dp,
                        isTwoColumn = false,
                    )
                },
                mediumContent = {
                    QuizScreenStateHost(
                        uiState = uiState,
                        onAction = onAction,
                        contentMaxWidth = 900.dp,
                        isTwoColumn = true,
                    )
                },
                expandedContent = {
                    QuizScreenStateHost(
                        uiState = uiState,
                        onAction = onAction,
                        contentMaxWidth = 1100.dp,
                        isTwoColumn = true,
                    )
                },
            )
        }
        uiState.feedbackDialog?.let { feedbackDialog ->
            QuizFeedbackDialog(
                state = feedbackDialog,
                onConfirm = { onAction(QuizAction.FeedbackConfirmed) },
            )
        }
        if (uiState.isSubmitting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (showSubmittingOverlay) {
                            Color.Black.copy(alpha = 0.18f)
                        } else {
                            Color.Transparent
                        },
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            )
        }
    }
}

@Composable
private fun QuizScreenStateHost(
    uiState: QuizUiState,
    onAction: (QuizAction) -> Unit,
    contentMaxWidth: Dp,
    isTwoColumn: Boolean = false,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when {
            uiState.isLoading -> QuizLoadingState(
                quizMode = uiState.mode,
                contentMaxWidth = contentMaxWidth,
                isTwoColumn = isTwoColumn,
            )

            uiState.error != null -> QuizErrorState(
                message = uiState.error.asComposeString(),
                onRetry = { onAction(QuizAction.RetryClicked) },
                contentMaxWidth = contentMaxWidth,
            )

            uiState.currentQuestion != null -> QuizReadyState(
                uiState = uiState,
                onAction = onAction,
                contentMaxWidth = contentMaxWidth,
                isTwoColumn = isTwoColumn,
            )
        }
    }
}

@Composable
private fun QuizLoadingState(
    quizMode: QuizMode,
    contentMaxWidth: Dp,
    isTwoColumn: Boolean = false
) {
    val spacing = QuoteQuizTheme.spacing
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f)
    val highlightColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    val scrollState = rememberScrollState()

    if (isTwoColumn) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.Top,
        ) {
            QuizQuoteCardLoading(
                modifier = Modifier.weight(1f),
                maxCardWidth = 600.dp,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    spacing.stackMd,
                    alignment = Alignment.CenterVertically
                ),
            ) {
                QuizProgressLoadingSection(
                    placeholderColor = placeholderColor,
                    highlightColor = highlightColor,
                )
                QuizInteractionPanelLoading(
                    quizMode = quizMode,
                    placeholderColor = placeholderColor,
                    highlightColor = highlightColor,
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = contentMaxWidth)
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.marginMobile, vertical = spacing.stackMd),
            verticalArrangement = Arrangement.spacedBy(spacing.stackMd),
        ) {
            Text(
                text = stringResource(Res.string.app_title),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            QuizProgressLoadingSection(
                placeholderColor = placeholderColor,
                highlightColor = highlightColor,
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    spacing.stackMd,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                QuizQuoteCardLoading()
                QuizInteractionPanelLoading(
                    quizMode = quizMode,
                    placeholderColor = placeholderColor,
                    highlightColor = highlightColor,
                )
            }
        }
    }
}

@Composable
private fun QuizProgressLoadingSection(
    placeholderColor: Color,
    highlightColor: Color,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Spacer(
                modifier = Modifier
                    .width(186.dp)
                    .height(20.dp)
                    .shimmerPlaceholder(
                        shape = RoundedCornerShape(999.dp),
                        baseColor = placeholderColor,
                        highlightColor = highlightColor,
                    ),
            )
            Spacer(
                modifier = Modifier
                    .width(42.dp)
                    .height(20.dp)
                    .shimmerPlaceholder(
                        shape = RoundedCornerShape(999.dp),
                        baseColor = placeholderColor,
                        highlightColor = highlightColor,
                    ),
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .shimmerPlaceholder(
                    shape = RoundedCornerShape(999.dp),
                    baseColor = placeholderColor,
                    highlightColor = highlightColor,
                ),
        )
    }
}

@Composable
private fun QuizErrorState(
    message: String,
    onRetry: () -> Unit,
    contentMaxWidth: Dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = contentMaxWidth)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.quiz_state_error),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) {
            Text(text = stringResource(Res.string.quiz_action_retry))
        }
    }
}

@Composable
private fun QuizReadyState(
    uiState: QuizUiState,
    onAction: (QuizAction) -> Unit,
    contentMaxWidth: Dp,
    isTwoColumn: Boolean = false,
) {
    val question = uiState.currentQuestion ?: return
    val spacing = QuoteQuizTheme.spacing
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = contentMaxWidth)
            .verticalScroll(scrollState)
            .padding(horizontal = spacing.marginMobile, vertical = spacing.stackMd),
        verticalArrangement = Arrangement.spacedBy(spacing.stackMd),
    ) {

        Text(
            text = stringResource(Res.string.app_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(
                spacing.stackMd,
                alignment = Alignment.CenterVertically
            ),
        ) {

            if (isTwoColumn) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.Top,
                ) {

                    // Quote Card on Left
                    QuizQuoteCard(
                        question = question,
                        modifier = Modifier.weight(1f),
                        maxCardWidth = 600.dp,
                    )

                    // Right Column: Progress Section top + Interaction Panel underneath
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {

                        QuizProgressSection(question = question)

                        QuizInteractionPanel(
                            question = question,
                            onAction = onAction,
                        )

                    }
                }
            } else {

                QuizProgressSection(question = question)
                QuizQuoteCard(question = question)
                QuizInteractionPanel(
                    question = question,
                    onAction = onAction,
                )
            }
        }
    }

}

@Composable
private fun QuizProgressSection(question: QuizQuestionDto) {
    val progressFraction = question.progress.toFloat() / question.totalQuestions.toFloat()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = stringResource(
                    Res.string.quiz_progress,
                    question.progress,
                    question.totalQuestions
                ).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${(progressFraction * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun QuizInteractionPanel(
    question: QuizQuestionDto,
    modifier: Modifier = Modifier,
    onAction: (QuizAction) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (question.mode) {
                QuizMode.BINARY -> {
                    val proposedAuthor = question.proposedAuthor.orEmpty()
                    Text(
                        text = stringResource(Res.string.quiz_binary_prompt, proposedAuthor),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    QuizBinaryAnswerButtons(
                        labels = listOf(
                            stringResource(Res.string.quiz_action_yes),
                            stringResource(Res.string.quiz_action_no),
                        ),
                        onSelect = { index ->
                            onAction(QuizAction.SubmitBinaryAnswer(index == 0))
                        },
                    )
                }

                QuizMode.MULTIPLE_CHOICE -> {
                    Text(
                        text = stringResource(Res.string.quiz_multiple_choice_prompt),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    QuizMultipleChoiceAnswerButtons(
                        options = question.options,
                        onSelect = { option ->
                            onAction(QuizAction.SubmitMultipleChoiceAnswer(option.id))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizInteractionPanelLoading(
    quizMode: QuizMode,
    placeholderColor: Color,
    highlightColor: Color,
) {
    val buttonCount = remember(quizMode) {
        when (quizMode) {
            QuizMode.BINARY -> 2
            QuizMode.MULTIPLE_CHOICE -> 3
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Prompt text shimmer placeholder
            Spacer(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(352.dp)
                    .height(30.dp)
                    .shimmerPlaceholder(
                        shape = RoundedCornerShape(999.dp),
                        baseColor = placeholderColor,
                        highlightColor = highlightColor,
                    ),
            )
            repeat(buttonCount) {
                Spacer(
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth()
                        .height(58.dp)
                        .shimmerPlaceholder(
                            shape = RoundedCornerShape(8.dp),
                            baseColor = placeholderColor,
                            highlightColor = highlightColor,
                        ),
                )
            }
        }
    }
}

@FormFactorPreviews
@Composable
private fun QuizScreenReadyPreview() {
    QuoteQuizTheme {
        QuizScreen(
            uiState = QuizUiState(
                currentQuestion = QuizQuestionDto(
                    id = "q1",
                    quote = "The only true wisdom is in knowing you know nothing.",
                    mode = QuizMode.BINARY,
                    progress = 1,
                    totalQuestions = 10,
                    proposedAuthor = "Socrates",
                ),
            ),
            onAction = {},
        )
    }
}

@FormFactorPreviews
@Composable
private fun QuizScreenMultiPreview() {
    QuoteQuizTheme {
        QuizScreen(
            uiState = QuizUiState(
                currentQuestion = QuizQuestionDto(
                    id = "q7",
                    quote = "The unexamined life is not worth living.",
                    mode = QuizMode.MULTIPLE_CHOICE,
                    progress = 7,
                    totalQuestions = 10,
                    options = listOf(
                        QuizOptionDto(id = "A", label = "Plato"),
                        QuizOptionDto(id = "B", label = "Socrates"),
                        QuizOptionDto(id = "C", label = "Aristotle"),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}


@FormFactorPreviews
@Composable
private fun QuizScreenLoadingPreview() {
    QuoteQuizTheme {
        QuizScreen(
            uiState = QuizUiState(isLoading = true),
            onAction = {},
        )
    }
}
