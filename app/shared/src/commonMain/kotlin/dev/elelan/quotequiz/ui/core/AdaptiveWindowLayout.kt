package dev.elelan.quotequiz.ui.core

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND

enum class AdaptiveLayoutSize {
    Compact,
    Medium,
    Expanded,
}

@Composable
fun AdaptiveWindowLayout(
    compactContent: @Composable () -> Unit,
    mediumContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
) {
    BoxWithConstraints {
        when {
            maxWidth < WIDTH_DP_MEDIUM_LOWER_BOUND.dp -> compactContent()
            maxWidth < WIDTH_DP_EXPANDED_LOWER_BOUND.dp -> mediumContent()
            else -> expandedContent()
        }
    }
}
