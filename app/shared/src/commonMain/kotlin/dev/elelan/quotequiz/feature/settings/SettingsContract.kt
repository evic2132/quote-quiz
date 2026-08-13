package dev.elelan.quotequiz.feature.settings

import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.core.ui.UiText

data class SettingsUiState(
    val selectedMode: QuizMode = QuizMode.BINARY,
    val dailyChallengeEnabled: Boolean = false,
)

sealed interface SettingsAction {
    data class ModeSelected(val mode: QuizMode) : SettingsAction
    data object DifficultyClicked : SettingsAction
    data object DailyChallengeClicked : SettingsAction
}

sealed interface SettingsUiEffect {
    data class ShowMessage(val message: UiText) : SettingsUiEffect
}
