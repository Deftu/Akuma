package dev.deftu.akuma.jda

import dev.deftu.akuma.CommandBuilder
import dev.deftu.akuma.MessageCommandBuilder
import dev.deftu.akuma.UserCommandBuilder
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CommandDataMapperTest {

    @Test
    fun `string option choices are attached to the mapped JDA option data`() {
        val definition = CommandBuilder("cmd").apply {
            string("flavor") {
                choice("Vanilla", "vanilla")
                choice("Chocolate", "chocolate")
            }
        }.build()

        val option = (definition.toJdaData() as SlashCommandData).options.single { it.name == "flavor" }

        assertEquals(
            listOf("Vanilla" to "vanilla", "Chocolate" to "chocolate"),
            option.choices.map { it.name to it.asString }
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
            definition.toJdaData()
        }
    }

    @Test
    fun `a user command maps to a USER-typed JDA command with no description`() {
        val definition = UserCommandBuilder("Ban user").build()

        val data = definition.toJdaData()

        assertEquals(Command.Type.USER, data.type)
    }

    @Test
    fun `a message command maps to a MESSAGE-typed JDA command with no description`() {
        val definition = MessageCommandBuilder("Pin message").build()

        val data = definition.toJdaData()

        assertEquals(Command.Type.MESSAGE, data.type)
    }

}
