package dev.deftu.akuma.kord

import dev.deftu.akuma.CommandBuilder
import dev.deftu.akuma.MessageCommandBuilder
import dev.deftu.akuma.UserCommandBuilder
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.orEmpty
import dev.kord.rest.builder.interaction.GlobalMultiApplicationCommandBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CommandDataMapperTest {

    @Test
    fun `string option choices are attached to the mapped Kord request`() {
        val definition = CommandBuilder("cmd").apply {
            string("flavor") {
                choice("Vanilla", "vanilla")
                choice("Chocolate", "chocolate")
            }
        }.build()

        val request = GlobalMultiApplicationCommandBuilder().apply { addCommand(definition) }.build().single()
        val option = request.options.orEmpty().single { it.name == "flavor" }

        assertEquals(
            listOf("Vanilla" to "vanilla", "Chocolate" to "chocolate"),
            option.choices.orEmpty().map { (it as Choice.StringChoice).name to it.value },
        )
    }

    @Test
    fun `an option that is both auto-complete and has static choices is rejected`() {
        val definition = CommandBuilder("cmd").apply {
            string("flavor") {
                choice("Vanilla", "vanilla")
                autocomplete { }
            }
        }.build()

        assertThrows(IllegalStateException::class.java) {
            GlobalMultiApplicationCommandBuilder().apply { addCommand(definition) }
        }
    }

    @Test
    fun `a user command maps to a USER-typed Kord request with no description`() {
        val definition = UserCommandBuilder("Ban user").build()

        val request = GlobalMultiApplicationCommandBuilder().apply { addCommand(definition) }.build().single()

        assertEquals(ApplicationCommandType.User, request.type)
    }

    @Test
    fun `a message command maps to a MESSAGE-typed Kord request with no description`() {
        val definition = MessageCommandBuilder("Pin message").build()

        val request = GlobalMultiApplicationCommandBuilder().apply { addCommand(definition) }.build().single()

        assertEquals(ApplicationCommandType.Message, request.type)
    }

}
