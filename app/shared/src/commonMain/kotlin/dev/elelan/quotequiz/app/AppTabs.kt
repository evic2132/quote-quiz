package dev.elelan.quotequiz.app

import androidx.navigation3.runtime.NavKey
import dev.elelan.quotequiz.contract.quiz.QuizResultDto
import kotlinx.serialization.Serializable

@Serializable
data object QuizTab : NavKey

@Serializable
data object SettingsTab : NavKey

@Serializable
data object ProfileTab : NavKey

@Serializable
data class QuizResultRoute(
    val result: QuizResultDto,
) : NavKey

val MAIN_TABS: Set<NavKey> = linkedSetOf(QuizTab, SettingsTab, ProfileTab)
