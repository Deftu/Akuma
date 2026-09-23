package dev.deftu.akuma.kord

import dev.deftu.akuma.CommandDefinition
import dev.deftu.akuma.MultiCommandBuilder
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.GlobalMultiApplicationCommandBuilder
import dev.kord.rest.builder.interaction.GuildMultiApplicationCommandBuilder

public suspend fun MultiCommandBuilder.registerTo(kord: Kord) {
    val commands = build()
    val requests = GlobalMultiApplicationCommandBuilder().apply {
        commands.forEach { addCommand(it) }
    }.build()

    kord.rest.interaction.createGlobalApplicationCommands(kord.selfId, requests)
    InteractionListener.getOrRegister(kord).register(commands)
}

public suspend fun MultiCommandBuilder.registerTo(kord: Kord, guildId: Snowflake) {
    val commands = build()
    val requests = GuildMultiApplicationCommandBuilder().apply {
        commands.forEach { addCommand(it) }
    }.build()

    kord.rest.interaction.createGuildApplicationCommands(kord.selfId, guildId, requests)
    InteractionListener.getOrRegister(kord).register(commands)
}

public suspend fun Kord.commands(block: MultiCommandBuilder.() -> Unit) {
    return MultiCommandBuilder().apply(block).registerTo(this)
}

public suspend fun Kord.commands(guildId: Snowflake, block: MultiCommandBuilder.() -> Unit) {
    return MultiCommandBuilder().apply(block).registerTo(this, guildId)
}

public suspend fun Kord.register(builder: MultiCommandBuilder) {
    builder.registerTo(this)
}

public suspend fun Kord.register(guildId: Snowflake, builder: MultiCommandBuilder) {
    builder.registerTo(this, guildId)
}

public suspend fun Kord.register(commands: List<CommandDefinition>) {
    val requests = GlobalMultiApplicationCommandBuilder().apply {
        commands.forEach { addCommand(it) }
    }.build()

    rest.interaction.createGlobalApplicationCommands(selfId, requests)
    InteractionListener.getOrRegister(this).register(commands)
}

public suspend fun Kord.register(guildId: Snowflake, commands: List<CommandDefinition>) {
    val requests = GuildMultiApplicationCommandBuilder().apply {
        commands.forEach { addCommand(it) }
    }.build()

    rest.interaction.createGuildApplicationCommands(selfId, guildId, requests)
    InteractionListener.getOrRegister(this).register(commands)
}
