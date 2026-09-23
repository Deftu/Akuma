package dev.deftu.akuma.jda

import dev.deftu.akuma.CommandBuilder
import dev.deftu.akuma.CommandDefinition
import net.dv8tion.jda.api.JDA

public fun JDA.subcommand(name: String, block: CommandBuilder.() -> Unit): CommandDefinition {
    val command = CommandBuilder(name).apply(block).build()
    updateCommands().addCommands(command.toJdaData()).queue()
    InteractionListener.getOrRegister(this).register(listOf(command))
    return command
}
