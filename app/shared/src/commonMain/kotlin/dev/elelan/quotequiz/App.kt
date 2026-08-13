package dev.elelan.quotequiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.elelan.quotequiz.app.AppContainer
import dev.elelan.quotequiz.core.di.appModules
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(
        configuration = koinConfiguration {
            modules(appModules)
        },
    ) {
        QuoteQuizTheme {
            AppContainer()
        }
    }
}
