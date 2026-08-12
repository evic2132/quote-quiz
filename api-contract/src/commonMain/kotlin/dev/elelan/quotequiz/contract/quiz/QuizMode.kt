package dev.elelan.quotequiz.contract.quiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class QuizMode {
    @SerialName("binary")
    BINARY,

    @SerialName("multiple_choice")
    MULTIPLE_CHOICE,
}
