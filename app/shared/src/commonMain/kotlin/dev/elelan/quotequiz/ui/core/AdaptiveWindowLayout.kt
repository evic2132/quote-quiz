package dev.elelan.quotequiz.ui.core

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND

@Composable
fun AdaptiveWindowLayout(
    compatMediumContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    BoxWithConstraints {
        if (maxWidth <= WIDTH_DP_EXPANDED_LOWER_BOUND.dp) {
            compatMediumContent()
        } else {
            expandedContent()
        }
    }
}