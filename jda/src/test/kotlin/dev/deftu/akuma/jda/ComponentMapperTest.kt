package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaButtonStyle
import dev.deftu.akuma.AkumaChannelType
import dev.deftu.akuma.AkumaEntitySelectType
import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ModalBuilder
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.components.selections.EntitySelectMenu
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
        }.build().toJda()

        assertEquals("btn:danger", button.customId)
        assertEquals("Delete", button.label)
        assertEquals(ButtonStyle.DANGER, button.style)
        assertTrue(button.isDisabled)
    }

    @Test
    fun `string select maps options in order`() {
        val select = ComponentBuilder.StringSelect("select:flavor", "Pick one").apply {
            option("Vanilla", "vanilla")
            option("Chocolate", "chocolate", description = "Rich and dark", isDefault = true)
        }.build().toJda()

        assertEquals("select:flavor", select.customId)
        assertEquals("Pick one", select.placeholder)
        assertEquals(
            listOf("Vanilla" to "vanilla", "Chocolate" to "chocolate"),
            select.options.map { it.label to it.value }
        )
        assertTrue(select.options[1].isDefault)
    }

    @Test
    fun `entity select maps MENTIONABLE to both USER and ROLE targets`() {
        val select = ComponentBuilder.EntitySelect(
            "select:target",
            entityTypes = setOf(AkumaEntitySelectType.MENTIONABLE),
        ).build().toJda()

        assertEquals(setOf(EntitySelectMenu.SelectTarget.USER, EntitySelectMenu.SelectTarget.ROLE), select.entityTypes)
    }

    @Test
    fun `entity select carries channel type restrictions`() {
        val select = ComponentBuilder.EntitySelect(
            "select:target",
            entityTypes = setOf(AkumaEntitySelectType.CHANNEL),
        ).apply {
            channelTypes.add(AkumaChannelType.TEXT)
        }.build().toJda()

        assertEquals(1, select.channelTypes.size)
    }

    @Test
    fun `modal wraps each text input in a Label with the input's own custom id`() {
        val modal = ModalBuilder("modal:feedback", "Send feedback").apply {
            textInput("summary", "Summary")
        }.build().toJda()

        assertEquals("modal:feedback", modal.id)
        assertEquals("Send feedback", modal.title)

        val label = modal.components.single().asLabel()
        assertEquals("Summary", label.label)
        assertEquals("summary", label.child.asTextInput().customId)
    }

    @Test
    fun `button without a label or emoji fails fast instead of building an invalid button`() {
        val exception = assertThrows(IllegalStateException::class.java) {
            ComponentBuilder.Button("btn:empty").build().toJda()
        }

        assertFalse(exception.message.isNullOrBlank())
    }

}
