package dev.elelan.quotequiz.ui.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp

@Composable
private fun shimmerBrush(
    baseColor: Color,
    highlightColor: Color,
): Brush {
    val transition = rememberInfiniteTransition(label = "quote_quiz_shimmer")
    val translate by transition.animateFloat(
        initialValue = -240f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1150,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "quote_quiz_shimmer_translate",
    )

    return Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor,
        ),
        start = Offset(translate, translate * 0.35f),
        end = Offset(translate + 320f, translate + 220f),
    )
}

fun Modifier.shimmerPlaceholder(
    shape: Shape = RoundedCornerShape(16.dp),
    baseColor: Color,
    highlightColor: Color,
): Modifier = composed {
    background(
        brush = shimmerBrush(
            baseColor = baseColor,
            highlightColor = highlightColor,
        ),
        shape = shape,
    ).clip(shape)
}
