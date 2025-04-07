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

public fun JDA.register(builder: MultiCommandBuilder) {
    builder.registerTo(this)
}

public fun JDA.register(commands: List<CommandDefinition>) {
    CommandListener.getOrRegister(this).commands.addAll(commands)
    this.updateCommands().addCommands(commands.map(CommandDefinition::asData)).queue()
}
