package dev.deftu.akuma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MessageCommandBuilderTest {

    @Test
    fun `build produces a MESSAGE-typed definition with no description or options`() {
        val definition = MessageCommandBuilder("pin").build()

        assertEquals(AkumaCommandType.MESSAGE, definition.type)
        assertNull(definition.description)
        assertTrue(definition.options.isEmpty())
        assertTrue(definition.children.isEmpty())
        assertTrue(definition.groups.isEmpty())
    }

}
