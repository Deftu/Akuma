package dev.deftu.akuma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserCommandBuilderTest {

    @Test
    fun `build produces a USER-typed definition with no description or options`() {
        val definition = UserCommandBuilder("ban").build()

        assertEquals(AkumaCommandType.USER, definition.type)
        assertNull(definition.description)
        assertTrue(definition.options.isEmpty())
        assertTrue(definition.children.isEmpty())
        assertTrue(definition.groups.isEmpty())
    }

    @Test
    fun `build carries over isNsfw, isGuildOnly, permissions, action and name localizations`() {
        val action: suspend CommandContext.() -> Unit = {}
        val definition = UserCommandBuilder("ban").apply {
            isNsfw = true
            isGuildOnly = true
            defaultGuildPermissions = AkumaDefaultPermissions.DISABLED
            nameLocalizations[AkumaLocale.GERMAN] = "Bannen"
            action(action)
        }.build()

        assertTrue(definition.isNsfw)
        assertTrue(definition.isGuildOnly)
        assertEquals(AkumaDefaultPermissions.DISABLED, definition.defaultGuildPermissions)
        assertEquals("Bannen", definition.nameLocalizations[AkumaLocale.GERMAN])
        assertSame(action, definition.action)
    }

}
