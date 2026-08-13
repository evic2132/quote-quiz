package dev.elelan.quotequiz.feature.quiz

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import org.jetbrains.compose.resources.stringResource
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.quiz_feedback_body
import quotequiz.app.shared.generated.resources.quiz_feedback_correct_title
import quotequiz.app.shared.generated.resources.quiz_feedback_incorrect_title
import quotequiz.app.shared.generated.resources.quiz_feedback_ok

@Composable
fun QuizFeedbackDialog(
    state: QuizFeedbackDialogState,
    onConfirm: () -> Unit,
) {

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        QuizFeedbackDialogContent(
            state = state,
            onConfirm = onConfirm
        )
    }
}

@Composable
fun QuizFeedbackDialogContent(
    state: QuizFeedbackDialogState,
    onConfirm: () -> Unit,
) {

    val pulseTransition = rememberInfiniteTransition(label = "quiz_feedback_pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "quiz_feedback_pulse_scale",
    )

    val isSuccess = state.correct
    val iconContainerColor =
        if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error
    val iconTint =
        if (isSuccess) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onError
    val titleColor =
        if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val decorativeColor =
        if (isSuccess) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else MaterialTheme.colorScheme.error.copy(alpha = 0.08f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.44f)),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 18.dp,
        ) {
            Box {
                if (!isSuccess) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(132.dp)
                            .background(
                                color = decorativeColor,
                                shape = RoundedCornerShape(bottomStart = 28.dp),
                            ),
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(width = 92.dp, height = 122.dp)
                            .background(
                                color = decorativeColor,
                                shape = RoundedCornerShape(topEnd = 28.dp),
                            ),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    Surface(
                        modifier = Modifier.scale(pulseScale),
                        shape = RoundedCornerShape(20.dp),
                        color = iconContainerColor,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (isSuccess) "✓" else "✕",
                                color = iconTint,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            )
                        }
                    }

                    Text(
                        text = if (isSuccess) {
                            stringResource(Res.string.quiz_feedback_correct_title)
                        } else {
                            stringResource(Res.string.quiz_feedback_incorrect_title)
                        },
                        style = MaterialTheme.typography.displayMedium,
                        color = titleColor,
                        textAlign = TextAlign.Center,
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.quiz_feedback_body),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = state.correctAuthor,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text(
                            text = stringResource(Res.string.quiz_feedback_ok),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }

}

@Preview
@Composable
private fun QuizFeedbackDialogSuccessPreview() {
    QuoteQuizTheme {
        QuizFeedbackDialogContent(
            state = QuizFeedbackDialogState(
                correct = true,
                correctAuthor = "Winston Churchill",
                nextQuestion = null,
                result = null,
            ),
            onConfirm = {},
        )
    }
}

@Preview
@Composable
private fun QuizFeedbackDialogErrorPreview() {
    QuoteQuizTheme {
        QuizFeedbackDialogContent(
            state = QuizFeedbackDialogState(
                correct = false,
                correctAuthor = "Maya Angelou",
                nextQuestion = null,
                result = null,
            ),
            onConfirm = {},
        )
    }
}
