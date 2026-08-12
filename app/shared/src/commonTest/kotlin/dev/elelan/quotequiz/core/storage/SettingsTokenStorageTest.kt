package dev.elelan.quotequiz.core.storage

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettingsTokenStorageTest {
    @Test
    fun `storage saves overwrites and clears token`() {
        val storage = SettingsTokenStorage(MapSettings())

        storage.set("first-token")
        assertEquals("first-token", storage.get())

        storage.set("updated-token")
        assertEquals("updated-token", storage.get())

        storage.clear()
        assertNull(storage.get())
    }
}
