package dev.elelan.quotequiz.feature.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.QuizQuestionDto
import dev.elelan.quotequiz.feature.login.LoginQuoteInline
import dev.elelan.quotequiz.ui.core.shimmerPlaceholder
import dev.elelan.quotequiz.ui.theme.QuoteQuizTheme

@Composable
fun QuizQuoteCard(
    question: QuizQuestionDto,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val cardShape = RoundedCornerShape(22.dp)
        val cardWidth = maxWidth.coerceAtMost(332.dp)

        Card(
            modifier = Modifier
                .width(cardWidth)
                .align(Alignment.Center)
                .aspectRatio(1f)
                .clip(cardShape),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            shape = cardShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                        shape = cardShape,
                    ),
            ) {
                QuizQuoteCardTopAccent(
                    modifier = Modifier.align(Alignment.TopCenter),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp, vertical = 34.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    LoginQuoteInline(
                        quote = question.quote,
                        author = "",
                        textAlign = TextAlign.Center,
                        quoteTextStyle = MaterialTheme.typography.displayMedium.copy(
                            color = MaterialTheme.colorScheme.tertiary,
                            lineHeight = MaterialTheme.typography.displayMedium.lineHeight * 1.3,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun QuizQuoteCardLoading(
    modifier: Modifier = Modifier,
) {
    val placeholderShape = RoundedCornerShape(22.dp)
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f)
    val highlightColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val cardWidth = maxWidth.coerceAtMost(332.dp)

        Box(
            modifier = Modifier
                .width(cardWidth)
                .align(Alignment.Center)
                .aspectRatio(1f)
                .clip(placeholderShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                    shape = placeholderShape,
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = placeholderShape,
                ),
        ) {
            QuizQuoteCardTopAccent(
                modifier = Modifier.align(Alignment.TopCenter),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 34.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                repeat(4) { index ->
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth(
                                when (index) {
                                    0 -> 0.78f
                                    1 -> 0.72f
                                    2 -> 0.66f
                                    else -> 0.54f
                                },
                            )
                            .height(28.dp)
                            .shimmerPlaceholder(
                                shape = RoundedCornerShape(12.dp),
                                baseColor = placeholderColor,
                                highlightColor = highlightColor,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizQuoteCardTopAccent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    ),
                ),
                shape = RoundedCornerShape(999.dp),
            ),
    )
}

@Preview
@Composable
private fun QuizQuoteCardPreview() {
    QuoteQuizTheme {
        QuizQuoteCard(
            question = QuizQuestionDto(
                id = "q4",
                quote = "Imagination is more important than knowledge. For knowledge is limited, whereas imagination embraces the entire world, stimulating progress, giving birth to evolution.",
                mode = QuizMode.BINARY,
                progress = 4,
                totalQuestions = 10,
                proposedAuthor = "Albert Einstein",
            ),
        )
    }
}

@Preview
@Composable
private fun QuizQuoteCardLoadingPreview() {
    QuoteQuizTheme {
        QuizQuoteCardLoading()
    }
}
