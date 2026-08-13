package dev.elelan.quotequiz.core.di

import dev.elelan.quotequiz.feature.login.LoginViewModel
import dev.elelan.quotequiz.feature.quiz.QuizViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::QuizViewModel)
}
