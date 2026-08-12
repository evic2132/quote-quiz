package dev.elelan.quotequiz.core.storage

import com.russhwolf.settings.Settings

class SettingsTokenStorage(
    private val settings: Settings,
) : TokenStorage {
    override fun get(): String? = settings.getStringOrNull(KEY_AUTH_TOKEN)

    override fun set(token: String) {
        settings.putString(KEY_AUTH_TOKEN, token)
    }

    override fun clear() {
        settings.remove(KEY_AUTH_TOKEN)
    }

    private companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
    }
}
