package dev.elelan.quotequiz.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object QuizTab : NavKey

@Serializable
data object SettingsTab : NavKey

@Serializable
data object ProfileTab : NavKey

@Serializable
data object QuizResultRoute : NavKey

val MAIN_TABS: Set<NavKey> = linkedSetOf(QuizTab, SettingsTab, ProfileTab)
