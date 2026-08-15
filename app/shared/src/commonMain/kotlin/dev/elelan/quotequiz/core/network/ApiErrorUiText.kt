package dev.elelan.quotequiz.core.network

import dev.elelan.quotequiz.ui.core.UiText

fun ApiError.toUiText(
    network: UiText,
    unauthorized: UiText,
    http: UiText,
    unknown: UiText,
): UiText =
    when (this) {
        ApiError.Network -> network
        ApiError.Unauthorized -> unauthorized
        is ApiError.Http -> http
        is ApiError.Unknown -> unknown
    }
