package dev.elelan.quotequiz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF15157D),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF2E3192),
    onPrimaryContainer = Color(0xFF9DA1FF),
    secondary = Color(0xFF5E5E5E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE4E2E2),
    onSecondaryContainer = Color(0xFF656464),
    tertiary = Color(0xFF252831),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF3B3E47),
    background = Color(0xFFFDF8FD),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFDF8FD),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE5E1E7),
    onSurfaceVariant = Color(0xFF464652),
    outline = Color(0xFF777683),
    outlineVariant = Color(0xFFC7C5D4),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFC0C1FF),
    onPrimary = Color(0xFF0E1267),
    primaryContainer = Color(0xFF2E3192),
    onPrimaryContainer = Color(0xFFE1E0FF),
    secondary = Color(0xFFC8C6C6),
    onSecondary = Color(0xFF2B2B2B),
    secondaryContainer = Color(0xFF474747),
    onSecondaryContainer = Color(0xFFE4E2E2),
    tertiary = Color(0xFFC4C6D2),
    onTertiary = Color(0xFF20242C),
    tertiaryContainer = Color(0xFF3B3E47),
    background = Color(0xFF16161B),
    onBackground = Color(0xFFF4EFF5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFF4EFF5),
    surfaceVariant = Color(0xFF464652),
    onSurfaceVariant = Color(0xFFC7C5D4),
    outline = Color(0xFF92909F),
    outlineVariant = Color(0xFF464652),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

private val QuoteQuizTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.64).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.24).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)

private val QuoteQuizShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
)

@Immutable
data class QuoteQuizSpacing(
    val marginMobile: Dp = 20.dp,
    val marginTablet: Dp = 40.dp,
    val gutter: Dp = 16.dp,
    val stackSm: Dp = 8.dp,
    val stackMd: Dp = 16.dp,
    val stackLg: Dp = 32.dp,
)

private val LocalSpacing = staticCompositionLocalOf { QuoteQuizSpacing() }

object QuoteQuizTheme {
    val spacing: QuoteQuizSpacing
        @Composable get() = LocalSpacing.current

    val validationError: Color
        @Composable get() = AssignmentValidationError

    val stageSurface: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
}

val AssignmentValidationError = Color(0xFFFF0000)

@Composable
expect fun ApplyPlatformTheme(
    darkTheme: Boolean,
)

@Composable
fun QuoteQuizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    ApplyPlatformTheme(darkTheme = darkTheme)

    CompositionLocalProvider(LocalSpacing provides QuoteQuizSpacing()) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = QuoteQuizTypography,
            shapes = QuoteQuizShapes,
            content = content,
        )
    }
}
