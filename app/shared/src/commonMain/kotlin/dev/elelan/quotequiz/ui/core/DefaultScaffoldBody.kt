package dev.elelan.quotequiz.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuoteQuizBackground(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "”",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f),
                fontSize = 640.sp,
                lineHeight = 520.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun DefaultScaffoldBody(
    modifier: Modifier = Modifier,
    enableEdgeToEdge: Boolean = false,
    shouldOverlayBlur: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // This Box holds the main content and blurs it when loading (only when not using pull-to-refresh)
        Box(
            modifier = Modifier
                .then(
                    if (enableEdgeToEdge) {
                        Modifier
                    } else {
                        Modifier.windowInsetsPadding(WindowInsets.statusBars)
                    }
                )
                .then(
                    if (shouldOverlayBlur) Modifier.blur(2.dp) else Modifier
                )
        ) {
            content()
        }
    }

}
