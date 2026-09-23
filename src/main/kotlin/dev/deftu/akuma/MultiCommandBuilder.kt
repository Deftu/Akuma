package dev.deftu.akuma

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

    public fun userCommand(name: String, block: UserCommandBuilder.() -> Unit) {
        commands.add(UserCommandBuilder(name).apply(block).build())
    }

    public fun messageCommand(name: String, block: MessageCommandBuilder.() -> Unit) {
        commands.add(MessageCommandBuilder(name).apply(block).build())
    }

    public fun register(contributor: CommandContributor<MultiCommandBuilder>) {
        contributor.register(this)
    }

    public fun build(): List<CommandDefinition> {
        return commands.toList()
    }
}

public fun commands(block: MultiCommandBuilder.() -> Unit): MultiCommandBuilder {
    return MultiCommandBuilder().apply(block)
}
