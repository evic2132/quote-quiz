package dev.elelan.quotequiz.feature.quiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.elelan.quotequiz.home.HomePlaceholderScreen
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import org.jetbrains.compose.resources.stringResource
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.quiz_result_body
import quotequiz.app.shared.generated.resources.quiz_result_title

@Composable
fun QuizResultScreen() {
    HomePlaceholderScreen(
        title = stringResource(Res.string.quiz_result_title),
        body = stringResource(Res.string.quiz_result_body),
    )
}

@Preview
@Composable
private fun QuizResultScreenPreview() {
    QuoteQuizTheme {
        QuizResultScreen()
    }
}
