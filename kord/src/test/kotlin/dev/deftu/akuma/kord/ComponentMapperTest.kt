package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaButtonStyle
import dev.deftu.akuma.AkumaChannelType
import dev.deftu.akuma.AkumaEntitySelectType
import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.kord.common.entity.ButtonStyle
import dev.kord.rest.builder.component.ChannelSelectBuilder
import dev.kord.rest.builder.component.LabelComponentBuilder
import dev.kord.rest.builder.component.MentionableSelectBuilder
import dev.kord.rest.builder.component.TextInputBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ComponentMapperTest {

    @Test
    fun `button maps id, label, style and disabled state`() {
        val button = ComponentBuilder.Button("btn:danger", "Delete").apply {
            style = AkumaButtonStyle.DANGER
            isDisabled = true
        }.build().toKord()

        assertEquals("btn:danger", button.customId)
        assertEquals("Delete", button.label)
        assertEquals(ButtonStyle.Danger, button.style)
        assertEquals(true, button.disabled)
    }

    @Test
    fun `string select maps options in order`() {
        val select = ComponentBuilder.StringSelect("select:flavor", "Pick one").apply {
            option("Vanilla", "vanilla")
            option("Chocolate", "chocolate", description = "Rich and dark", isDefault = true)
        }.build().toKord()

        assertEquals("select:flavor", select.customId)
        assertEquals("Pick one", select.placeholder)
        assertEquals(
            listOf("Vanilla" to "vanilla", "Chocolate" to "chocolate"),
            select.options.map { it.label to it.value },
        )
        assertEquals(true, select.options[1].default)
    }

    @Test
    fun `entity select MENTIONABLE maps to Kord's dedicated mentionable select builder`() {
        val select = ComponentBuilder.EntitySelect(
            "select:target",
            entityTypes = setOf(AkumaEntitySelectType.MENTIONABLE),
        ).build().toKord()

        assertTrue(select is MentionableSelectBuilder)
    }

    @Test
    fun `entity select USER+ROLE maps to the same mentionable select builder as MENTIONABLE`() {
        val select = ComponentBuilder.EntitySelect(
            "select:target",
            entityTypes = setOf(AkumaEntitySelectType.USER, AkumaEntitySelectType.ROLE),
        ).build().toKord()

        assertTrue(select is MentionableSelectBuilder)
    }

    @Test
    fun `entity select carries channel type restrictions`() {
        val select = ComponentBuilder.EntitySelect(
            "select:target",
            entityTypes = setOf(AkumaEntitySelectType.CHANNEL),
        ).apply {
            channelTypes.add(AkumaChannelType.TEXT)
        }.build().toKord() as ChannelSelectBuilder

        assertEquals(1, select.channelTypes?.size)
    }

    @Test
    fun `modal wraps each text input in a label component with the input's own custom id`() {
        val definition = ModalBuilder("modal:feedback", "Send feedback").apply {
            textInput("summary", "Summary")
        }.build()

        val builder = dev.kord.rest.builder.interaction.ModalBuilder("Send feedback", "modal:feedback")
        definition.applyTo(builder)

        val label = builder.components.single() as LabelComponentBuilder
        assertEquals("Summary", label.label)
        assertEquals("summary", (label.component as TextInputBuilder).customId)
    }

    @Test
    fun `button without a label or emoji fails fast instead of building an invalid button`() {
        val exception = assertThrows(IllegalStateException::class.java) {
            ComponentBuilder.Button("btn:empty").build().toKord()
        }

        assertFalse(exception.message.isNullOrBlank())
    }

}
