package dev.elelan.quotequiz.feature.splash

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.elelan.quotequiz.app.AppPlaceholderScreen
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import org.jetbrains.compose.resources.stringResource
import quotequiz.app.shared.generated.resources.*

@Composable
fun SplashScreen() {
    AppPlaceholderScreen(
        title = stringResource(Res.string.splash_title),
        body = stringResource(Res.string.splash_body),
    )
}

@Preview
@Composable
private fun SplashScreenPreview() {
    QuoteQuizTheme {
        SplashScreen()
    }
}
