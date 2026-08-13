package dev.elelan.quotequiz.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.elelan.quotequiz.home.HomePlaceholderScreen
import dev.elelan.quotequiz.home.LogoutButton
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import org.jetbrains.compose.resources.stringResource
import quotequiz.app.shared.generated.resources.*

@Composable
fun ProfileScreen(
    name: String,
    email: String,
    onLogout: suspend () -> Unit,
) {
    HomePlaceholderScreen(
        title = stringResource(Res.string.profile_title),
        body = stringResource(Res.string.profile_body, name, email),
        footer = {
            LogoutButton(onLogout = onLogout)
        },
    )
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    QuoteQuizTheme {
        ProfileScreen(
            name = "Demo User",
            email = "demo@example.com",
            onLogout = {},
        )
    }
}
