package dev.elelan.quotequiz.feature.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.elelan.quotequiz.core.ui.UiText
import dev.elelan.quotequiz.ui.core.DefaultScaffoldBody
import dev.elelan.quotequiz.ui.core.QuoteQuizBackground
import dev.elelan.quotequiz.ui.theme.AssignmentValidationError
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.app_title
import quotequiz.app.shared.generated.resources.ic_format_quote
import quotequiz.app.shared.generated.resources.ic_lock
import quotequiz.app.shared.generated.resources.ic_lock_open
import quotequiz.app.shared.generated.resources.login_button
import quotequiz.app.shared.generated.resources.login_button_loading
import quotequiz.app.shared.generated.resources.login_email_label
import quotequiz.app.shared.generated.resources.login_error_password_required
import quotequiz.app.shared.generated.resources.login_error_unauthorized
import quotequiz.app.shared.generated.resources.login_footer_copyright
import quotequiz.app.shared.generated.resources.login_forgot_password
import quotequiz.app.shared.generated.resources.login_no_account
import quotequiz.app.shared.generated.resources.login_password_hide
import quotequiz.app.shared.generated.resources.login_password_label
import quotequiz.app.shared.generated.resources.login_password_show
import quotequiz.app.shared.generated.resources.login_quote
import quotequiz.app.shared.generated.resources.login_quote_author
import quotequiz.app.shared.generated.resources.login_remember_me
import quotequiz.app.shared.generated.resources.login_sign_up
import quotequiz.app.shared.generated.resources.login_title

@Composable
fun LoginRouteScreen(
    viewModel: LoginViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.uiEvent.collectLatest { effect ->
            when (effect) {
                is LoginUiEffect.ShowMessage -> snackbarHostState.showSnackbar(
                    effect.message.asString(),
                )
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (LoginAction) -> Unit,
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
            ) {
                LoginScreenContent(
                    uiState = uiState,
                    onAction = onAction,
                )
            }
        }
        if (uiState.isSubmitting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.28f))
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
private fun LoginScreenContent(
    uiState: LoginUiState,
    onAction: (LoginAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HeaderSection()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoginFormCard(
                uiState = uiState,
                onAction = onAction,
            )
        }

        FooterSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
    }
}

@Composable
private fun HeaderSection() {
    val spacing = QuoteQuizTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = spacing.stackSm),
        verticalArrangement = Arrangement.spacedBy(spacing.stackMd),
    ) {
        Text(
            text = stringResource(Res.string.app_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        LoginQuoteInline(
            quote = stringResource(Res.string.login_quote),
            author = stringResource(Res.string.login_quote_author),
        )
    }
}

private const val START_QUOTE_ID = "start_quote_icon"
private const val END_QUOTE_ID = "end_quote_icon"

@Composable
fun LoginQuoteInline(
    quote: String,
    author: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    quoteTextStyle: TextStyle = MaterialTheme.typography.headlineMedium,
) {
    val spacing = QuoteQuizTheme.spacing
    val quoteStyle = quoteTextStyle
    val iconStyle = MaterialTheme.typography.displayLargeEmphasized
    val primaryColor = MaterialTheme.colorScheme.primary
    val iconSize = iconStyle.fontSize.value.dp
    val annotatedQuote = remember(quote) {
        buildAnnotatedString {
            appendInlineContent(START_QUOTE_ID, "“")
            append(" ")
            append(quote)
            append(" ")
            appendInlineContent(END_QUOTE_ID, "”")
        }
    }

    val inlineContent = remember(primaryColor, iconSize) {
        mapOf(
            START_QUOTE_ID to InlineTextContent(
                Placeholder(
                    width = quoteStyle.fontSize,
                    height = quoteStyle.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                ),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_format_quote),
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier
                        .size(iconSize)
                        .graphicsLayer { scaleX = -1f },
                )
            },
            END_QUOTE_ID to InlineTextContent(
                Placeholder(
                    width = quoteStyle.fontSize,
                    height = quoteStyle.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                ),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_format_quote),
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(iconSize),
                )
            },
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.stackSm),
    ) {
        Text(
            text = annotatedQuote,
            style = quoteStyle,
            color = MaterialTheme.colorScheme.onBackground,
            inlineContent = inlineContent,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth(),
        )

        if (author.isNotBlank()) {
            Text(
                text = "— $author",
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = textAlign,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun LoginFormCard(
    uiState: LoginUiState,
    onAction: (LoginAction) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 440.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.login_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            LoginField(
                value = uiState.email,
                label = stringResource(Res.string.login_email_label),
                error = uiState.emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onValueChange = { onAction(LoginAction.EmailChanged(it)) },
            )
            PasswordField(
                value = uiState.password,
                label = stringResource(Res.string.login_password_label),
                error = uiState.passwordError,
                imeAction = ImeAction.Done,
                onValueChange = { onAction(LoginAction.PasswordChanged(it)) },
            )

            if (uiState.loginError != null) {
                Text(
                    text = uiState.loginError.asComposeString(),
                    color = AssignmentValidationError,
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = { onAction(LoginAction.RememberMeClicked) },
                    )
                    TextButton(onClick = { onAction(LoginAction.RememberMeClicked) }) {
                        Text(stringResource(Res.string.login_remember_me))
                    }
                }
                TextButton(onClick = { onAction(LoginAction.ForgotPasswordClicked) }) {
                    Text(stringResource(Res.string.login_forgot_password))
                }
            }

            Button(
                onClick = { onAction(LoginAction.SubmitClicked) },
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(),
            ) {
                Text(
                    if (uiState.isSubmitting) {
                        stringResource(Res.string.login_button_loading)
                    } else {
                        stringResource(Res.string.login_button)
                    },
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.login_no_account),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = { onAction(LoginAction.SignUpClicked) }) {
                    Text(stringResource(Res.string.login_sign_up))
                }
            }
        }
    }
}

@Composable
private fun FooterSection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.login_footer_copyright),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoginField(
    value: String,
    label: String,
    error: UiText?,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    onValueChange: (String) -> Unit,
) {
    val focusedColor =
        if (error != null) AssignmentValidationError else MaterialTheme.colorScheme.primary
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(
                    text = error.asComposeString(),
                    color = AssignmentValidationError,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = focusedColor,
            unfocusedBorderColor = if (error != null) AssignmentValidationError else MaterialTheme.colorScheme.outline,
            focusedLabelColor = focusedColor,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorBorderColor = AssignmentValidationError,
            errorLabelColor = AssignmentValidationError,
            errorSupportingTextColor = AssignmentValidationError,
        ),
    )
}

@Composable
private fun PasswordField(
    value: String,
    label: String,
    error: UiText?,
    imeAction: ImeAction,
    onValueChange: (String) -> Unit,
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    val focusedColor =
        if (error != null) AssignmentValidationError else MaterialTheme.colorScheme.primary
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (passwordHidden) {
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        } else {
            androidx.compose.ui.text.input.VisualTransformation.None
        },
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(
                    text = error.asComposeString(),
                    color = AssignmentValidationError,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                Icon(
                    painter = if (passwordHidden) {
                        painterResource(Res.drawable.ic_lock)
                    } else {
                        painterResource(Res.drawable.ic_lock_open)
                    },
                    contentDescription = if (passwordHidden) {
                        stringResource(Res.string.login_password_show)
                    } else {
                        stringResource(Res.string.login_password_hide)
                    },
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = focusedColor,
            unfocusedBorderColor = if (error != null) AssignmentValidationError else MaterialTheme.colorScheme.outline,
            focusedLabelColor = focusedColor,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorBorderColor = AssignmentValidationError,
            errorLabelColor = AssignmentValidationError,
            errorSupportingTextColor = AssignmentValidationError,
        ),
    )
}

@Preview
@Composable
private fun LoginScreenPreview() {
    QuoteQuizTheme {
        LoginScreen(
            uiState = LoginUiState(
            ),
            snackbarHostState = SnackbarHostState(),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun LoginScreenErrorPreview() {
    QuoteQuizTheme {
        LoginScreen(
            uiState = LoginUiState(
                email = "demo@example.com",
                password = "",
                passwordError = UiText.StringResourceId(Res.string.login_error_password_required),
                loginError = UiText.StringResourceId(Res.string.login_error_unauthorized),
            ),
            snackbarHostState = SnackbarHostState(),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun LoginScreenDarkPreview() {
    QuoteQuizTheme(darkTheme = true) {
        LoginScreen(
            uiState = LoginUiState(
                email = "demo@example.com",
                password = "password123",
            ),
            snackbarHostState = SnackbarHostState(),
            onAction = {},
        )
    }
}
