package dev.elelan.quotequiz.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.core.settings.QuizPreferencesRepository
import dev.elelan.quotequiz.ui.core.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.settings_daily_challenge_not_implemented
import quotequiz.app.shared.generated.resources.settings_difficulty_not_implemented

class SettingsViewModel(
    private val quizPreferencesRepository: QuizPreferencesRepository,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState>
        field = MutableStateFlow(SettingsUiState())

    private val uiEventChannel = Channel<SettingsUiEffect>()
    val uiEffect: Flow<SettingsUiEffect> = uiEventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            quizPreferencesRepository.selectedMode.collectLatest { mode ->
                uiState.update { it.copy(selectedMode = mode) }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.DailyChallengeClicked -> emitNotImplementedMessage(
                UiText.StringResourceId(Res.string.settings_daily_challenge_not_implemented),
            )
            SettingsAction.DifficultyClicked -> emitNotImplementedMessage(
                UiText.StringResourceId(Res.string.settings_difficulty_not_implemented),
            )
            is SettingsAction.ModeSelected -> updateMode(action.mode)
        }
    }

    private fun updateMode(mode: QuizMode) {
        if (uiState.value.selectedMode == mode) return
        viewModelScope.launch {
            quizPreferencesRepository.updateSelectedMode(mode)
        }
    }

    private fun emitNotImplementedMessage(message: UiText) {
        viewModelScope.launch {
            uiEventChannel.send(SettingsUiEffect.ShowMessage(message))
        }
    }
}
