package dev.elelan.quotequiz.app

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclassesOfSealed

@OptIn(ExperimentalSerializationApi::class)
object NavigationSerialization {
    val serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<AppRoute>()
        }
    }

    val configuration = SavedStateConfiguration {
        serializersModule = NavigationSerialization.serializersModule
    }
}


@Composable
fun rememberAppRouteBackStack(vararg routes: AppRoute): NavBackStack<AppRoute> =
    @Suppress("UNCHECKED_CAST")
    (rememberNavBackStack(NavigationSerialization.configuration, *routes) as NavBackStack<AppRoute>)
