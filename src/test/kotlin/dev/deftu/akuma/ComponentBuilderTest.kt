package dev.deftu.akuma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ComponentBuilderTest {

    @Test
    fun `button builds with defaults`() {
        val button = ComponentBuilder.Button("btn:confirm", "Confirm").build()

        assertEquals("btn:confirm", button.customId)
        assertEquals("Confirm", button.label)
        assertEquals(AkumaButtonStyle.SECONDARY, button.style)
        assertFalse(button.isDisabled)
    }

    @Test
    fun `button builds with overrides`() {
        val button = ComponentBuilder.Button("btn:danger", "Delete").apply {
            style = AkumaButtonStyle.DANGER
            isDisabled = true
            emoji = "🗑"
        }.build()

        assertEquals(AkumaButtonStyle.DANGER, button.style)
        assertTrue(button.isDisabled)
        assertEquals("🗑", button.emoji)
    }

    @Test
    fun `string select collects options in order`() {
        val select = ComponentBuilder.StringSelect("select:flavor", "Pick one").apply {
            option("Vanilla", "vanilla")
            option("Chocolate", "chocolate", description = "Rich and dark", isDefault = true)
        }.build()

        assertEquals("select:flavor", select.customId)
        assertEquals("Pick one", select.placeholder)
        assertEquals(1, select.minValues)
        assertEquals(1, select.maxValues)
        assertEquals(
            listOf("Vanilla" to "vanilla", "Chocolate" to "chocolate"),
            select.options.map { it.label to it.value }
        )
        assertTrue(select.options[1].isDefault)
        assertEquals("Rich and dark", select.options[1].description)
    }

    @Test
    fun `entity select carries entity and channel type restrictions`() {
        val select = ComponentBuilder.EntitySelect(
            "select:target",
            entityTypes = setOf(AkumaEntitySelectType.CHANNEL),
        ).apply {
            channelTypes.add(AkumaChannelType.TEXT)
            channelTypes.add(AkumaChannelType.VOICE)
            maxValues = 3
        }.build()

        assertEquals(setOf(AkumaEntitySelectType.CHANNEL), select.entityTypes)
        assertEquals(setOf(AkumaChannelType.TEXT, AkumaChannelType.VOICE), select.channelTypes)
        assertEquals(3, select.maxValues)
    }

}
