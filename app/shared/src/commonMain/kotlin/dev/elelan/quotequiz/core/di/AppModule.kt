package dev.elelan.quotequiz.core.di

import org.koin.core.module.Module

val appModules: List<Module>
    get() = listOf(
        platformModule,
        coreModule,
        featureModule,
    )
