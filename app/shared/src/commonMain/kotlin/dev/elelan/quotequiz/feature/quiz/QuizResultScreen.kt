package dev.elelan.quotequiz.feature.quiz

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.QuizResultDto
import dev.elelan.quotequiz.ui.core.DefaultScaffoldBody
import dev.elelan.quotequiz.ui.core.QuoteQuizBackground
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.ic_restart_alt
import quotequiz.app.shared.generated.resources.quiz_action_start_again
import quotequiz.app.shared.generated.resources.quiz_result_accuracy
import quotequiz.app.shared.generated.resources.quiz_result_correct
import quotequiz.app.shared.generated.resources.quiz_result_incorrect
import quotequiz.app.shared.generated.resources.quiz_result_mode
import quotequiz.app.shared.generated.resources.quiz_result_mode_binary
import quotequiz.app.shared.generated.resources.quiz_result_mode_multiple_choice
import quotequiz.app.shared.generated.resources.quiz_result_title_complete

@Composable
fun QuizResultScreen(
    result: QuizResultDto,
    onStartAgain: () -> Unit,
) {
    val spacing = QuoteQuizTheme.spacing
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        QuoteQuizBackground()
        DefaultScaffoldBody(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = spacing.marginMobile, vertical = spacing.stackLg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.stackLg),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.stackSm),
                    ) {
                        Text(
                            text = stringResource(Res.string.quiz_result_title_complete),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(
                                Res.string.quiz_result_mode,
                                result.mode.displayName()
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }

                    ScoreRing(result = result)

                    ResultSummaryCard(result = result)
                }

                Button(
                    onClick = onStartAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 360.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(),
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_restart_alt),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(Res.string.quiz_action_start_again),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreRing(
    result: QuizResultDto,
) {
    var animateToTarget by remember { mutableStateOf(false) }
    val progressTarget = result.percentageScore.coerceIn(0, 100) / 100f
    val progress by animateFloatAsState(
        targetValue = if (animateToTarget) progressTarget else 0f,
        animationSpec = tween(durationMillis = 1100),
        label = "quiz_result_score_ring",
    )
    val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
    val progressColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(progressTarget) {
        animateToTarget = true
    }

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        // TODO: Add a special celebration animation for standout scores such as 10/10.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "${result.correctAnswers} / ${result.totalQuestions}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(Res.string.quiz_result_accuracy, result.percentageScore),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ResultSummaryCard(
    result: QuizResultDto,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.96f),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        shadowElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ResultStatRow(
                label = stringResource(Res.string.quiz_result_correct),
                value = result.correctAnswers.toString(),
                accentColor = MaterialTheme.colorScheme.primary,
            )
            ResultStatRow(
                label = stringResource(Res.string.quiz_result_incorrect),
                value = result.incorrectAnswers.toString(),
                accentColor = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
private fun ResultStatRow(
    label: String,
    value: String,
    accentColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(accentColor, CircleShape),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun QuizMode.displayName(): String =
    when (this) {
        QuizMode.BINARY -> stringResource(Res.string.quiz_result_mode_binary)
        QuizMode.MULTIPLE_CHOICE -> stringResource(Res.string.quiz_result_mode_multiple_choice)
    }

@Preview
@Composable
private fun QuizResultScreenPreview() {
    QuoteQuizTheme {
        QuizResultScreen(
            result = QuizResultDto(
                mode = QuizMode.MULTIPLE_CHOICE,
                totalQuestions = 10,
                correctAnswers = 8,
                incorrectAnswers = 2,
                percentageScore = 80,
            ),
            onStartAgain = {},
        )
    }
}
