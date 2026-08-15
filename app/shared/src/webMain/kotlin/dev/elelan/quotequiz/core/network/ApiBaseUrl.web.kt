package dev.elelan.quotequiz.core.network

import kotlinx.browser.document
import kotlinx.browser.window

private const val ApiBaseUrlMetaName = "quotequiz-api-base-url"
private const val LocalDevApiBaseUrl = "http://localhost:8080"

actual fun defaultApiBaseUrl(): String {
    val configuredBaseUrl = document
        .querySelector("meta[name='$ApiBaseUrlMetaName']")
        ?.getAttribute("content")
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

    return normalizeBrowserBaseUrl(
        configuredBaseUrl ?: browserDefaultApiBaseUrl(),
    )
}

private fun browserDefaultApiBaseUrl(): String {
    val location = window.location

    return when (location.hostname) {
        "localhost",
        "127.0.0.1" -> LocalDevApiBaseUrl
        else -> "${location.protocol}//${location.host}"
    }
}

private fun normalizeBrowserBaseUrl(value: String): String = value.removeSuffix("/")
