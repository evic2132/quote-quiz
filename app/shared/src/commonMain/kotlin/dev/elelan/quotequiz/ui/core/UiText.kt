package dev.elelan.quotequiz.ui.core

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    data class StringResourceId(
        val id: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText

    @Composable
    fun asComposeString(): String =
        when (this) {
            is DynamicString -> value
            is StringResourceId -> stringResource(id, *args.toTypedArray())
        }

    suspend fun asString(): String =
        when (this) {
            is DynamicString -> value
            is StringResourceId -> getString(id, *args.toTypedArray())
        }
}