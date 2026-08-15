package dev.elelan.quotequiz.core.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

internal actual val browserHttpClientEngineFactory: HttpClientEngineFactory<*> = Js
