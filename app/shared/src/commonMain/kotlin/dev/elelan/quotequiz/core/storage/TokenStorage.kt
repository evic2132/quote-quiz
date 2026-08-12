package dev.elelan.quotequiz.core.storage

interface TokenStorage {
    fun get(): String?

    fun set(token: String)

    fun clear()
}
