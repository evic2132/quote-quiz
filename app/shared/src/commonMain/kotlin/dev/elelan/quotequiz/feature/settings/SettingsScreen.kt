package dev.elelan.quotequiz.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.elelan.quotequiz.home.HomePlaceholderScreen
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import org.jetbrains.compose.resources.stringResource
import quotequiz.app.shared.generated.resources.*

@Composable
fun SettingsScreen() {
    HomePlaceholderScreen(
        title = stringResource(Res.string.tab_settings),
        body = stringResource(Res.string.settings_placeholder_body),
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    QuoteQuizTheme {
        SettingsScreen()
    }
}
