package dev.deftu.akuma

import net.dv8tion.jda.api.JDA

public class MultiCommandBuilder {

    private val commands = mutableListOf<CommandDefinition>()

    public fun command(name: String, block: CommandBuilder.() -> Unit) {
        commands.add(CommandBuilder(name).apply(block).build())
    }

    public fun command(name: String, description: String? = null, block: CommandBuilder.() -> Unit) {
        commands.add(CommandBuilder(name).apply {
            this.description = description
            block()
        }.build())
    }

    public fun register(contributor: CommandContributor<MultiCommandBuilder>) {
        contributor.register(this)
    }

    public fun registerTo(jda: JDA) {
        val commands = build()
        jda.updateCommands().addCommands(commands.map(CommandDefinition::asData)).queue()
        CommandListener.getOrRegister(jda).commands.addAll(commands)
    }

    public fun registerTo(jda: JDA, guildId: Long) {
        val guild = jda.getGuildById(guildId) ?: return
        guild.updateCommands().addCommands(commands.map(CommandDefinition::asData)).queue()
        CommandListener.getOrRegister(jda).commands.addAll(commands)
    }

    public fun build(): List<CommandDefinition> {
        return commands.toList()
    }

}

public fun commands(block: MultiCommandBuilder.() -> Unit): MultiCommandBuilder {
    return MultiCommandBuilder().apply(block)
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
    this.updateCommands().addCommands(commands.map(CommandDefinition::asData)).queue()
    CommandListener.getOrRegister(this).commands.addAll(commands)
}

public fun JDA.register(guildId: Long, commands: List<CommandDefinition>) {
    val guild = getGuildById(guildId) ?: return
    guild.updateCommands().addCommands(commands.map(CommandDefinition::asData)).queue()
    CommandListener.getOrRegister(this).commands.addAll(commands)
}
