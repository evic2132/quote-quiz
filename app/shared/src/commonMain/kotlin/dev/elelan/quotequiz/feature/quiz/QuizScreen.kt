package dev.elelan.quotequiz.feature.quiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.elelan.quotequiz.home.HomePlaceholderScreen
import androidx.compose.material3.TextButton
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import org.jetbrains.compose.resources.stringResource
import quotequiz.app.shared.generated.resources.*

@Composable
fun QuizScreen(
    onOpenResult: () -> Unit,
) {
    HomePlaceholderScreen(
        title = stringResource(Res.string.tab_quiz),
        body = stringResource(Res.string.quiz_placeholder_body),
        footer = {
            TextButton(onClick = onOpenResult) {
                androidx.compose.material3.Text(
                    text = stringResource(Res.string.quiz_open_result),
                )
            }
        },
    )
}

@Preview
@Composable
private fun QuizScreenPreview() {
    QuoteQuizTheme {
        QuizScreen(onOpenResult = {})
    }
}
