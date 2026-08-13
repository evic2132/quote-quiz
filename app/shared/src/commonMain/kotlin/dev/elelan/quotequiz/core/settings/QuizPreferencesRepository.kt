package dev.elelan.quotequiz.core.settings

import com.russhwolf.settings.Settings
import dev.elelan.quotequiz.contract.quiz.QuizMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface QuizPreferencesRepository {
    val selectedMode: StateFlow<QuizMode>

    suspend fun updateSelectedMode(mode: QuizMode)
}

class SettingsQuizPreferencesRepository(
    private val settings: Settings,
) : QuizPreferencesRepository {
    private val mutableSelectedMode = MutableStateFlow(settings.getStoredQuizMode())

    override val selectedMode: StateFlow<QuizMode> = mutableSelectedMode.asStateFlow()

    override suspend fun updateSelectedMode(mode: QuizMode) {
        settings.putString(KEY_SELECTED_MODE, mode.storageValue)
        mutableSelectedMode.value = mode
    }

    private companion object {
        const val KEY_SELECTED_MODE = "selected_quiz_mode"
    }
}

private fun Settings.getStoredQuizMode(): QuizMode =
    when (getStringOrNull("selected_quiz_mode")) {
        QuizMode.MULTIPLE_CHOICE.storageValue -> QuizMode.MULTIPLE_CHOICE
        else -> QuizMode.BINARY
    }

private val QuizMode.storageValue: String
    get() =
        when (this) {
            QuizMode.BINARY -> "binary"
            QuizMode.MULTIPLE_CHOICE -> "multiple_choice"
        }
