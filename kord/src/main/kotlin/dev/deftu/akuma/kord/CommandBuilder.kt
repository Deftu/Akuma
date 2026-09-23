package dev.deftu.akuma.kord

import dev.deftu.akuma.CommandBuilder
import dev.deftu.akuma.CommandDefinition
import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.GlobalMultiApplicationCommandBuilder

public suspend fun Kord.subcommand(name: String, block: CommandBuilder.() -> Unit): CommandDefinition {
    val command = CommandBuilder(name).apply(block).build()
    val requests = GlobalMultiApplicationCommandBuilder().apply { addCommand(command) }.build()

    rest.interaction.createGlobalApplicationCommands(selfId, requests)
    InteractionListener.getOrRegister(this).register(listOf(command))
    return command
}
