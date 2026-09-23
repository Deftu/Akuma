package dev.deftu.akuma.jda

import dev.deftu.akuma.CommandDefinition
import dev.deftu.akuma.MultiCommandBuilder
import net.dv8tion.jda.api.JDA

public fun MultiCommandBuilder.registerTo(jda: JDA) {
    val commands = build()
    jda.updateCommands().addCommands(commands.map(CommandDefinition::toJdaData)).queue()
    InteractionListener.getOrRegister(jda).register(commands)
}

public fun MultiCommandBuilder.registerTo(jda: JDA, guildId: Long) {
    val guild = jda.getGuildById(guildId) ?: return
    val commands = build()
    guild.updateCommands().addCommands(commands.map(CommandDefinition::toJdaData)).queue()
    InteractionListener.getOrRegister(jda).register(commands)
}

public fun JDA.commands(block: MultiCommandBuilder.() -> Unit) {
    return MultiCommandBuilder().apply(block).registerTo(this)
}

public fun JDA.commands(guildId: Long, block: MultiCommandBuilder.() -> Unit) {
    return MultiCommandBuilder().apply(block).registerTo(this, guildId)
}

public fun JDA.register(builder: MultiCommandBuilder) {
    builder.registerTo(this)
}

public fun JDA.register(guildId: Long, builder: MultiCommandBuilder) {
    builder.registerTo(this, guildId)
}

public fun JDA.register(commands: List<CommandDefinition>) {
    this.updateCommands().addCommands(commands.map(CommandDefinition::toJdaData)).queue()
    InteractionListener.getOrRegister(this).register(commands)
}

public fun JDA.register(guildId: Long, commands: List<CommandDefinition>) {
    val guild = getGuildById(guildId) ?: return
    guild.updateCommands().addCommands(commands.map(CommandDefinition::toJdaData)).queue()
    InteractionListener.getOrRegister(this).register(commands)
}
