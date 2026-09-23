package dev.deftu.akuma

public class CommandGroupBuilder(private val name: String) {
    private val children = mutableListOf<CommandDefinition>()
    public var description: String? = null
    public val nameLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()
    public val descriptionLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()

    public fun subcommand(name: String, block: CommandBuilder.() -> Unit) {
        children.add(CommandBuilder(name).apply(block).build())
    }

    public fun subcommand(name: String, description: String? = null, block: CommandBuilder.() -> Unit) {
        children.add(CommandBuilder(name).apply {
            this.description = description
            block()
        }.build())
    }

    public fun register(contributor: CommandContributor<CommandGroupBuilder>) {
        contributor.register(this)
    }

    public fun build(): CommandGroupDefinition {
        val definition = CommandGroupDefinition(
            name = name,
            description = description,
            children = children
        )

        definition.nameLocalizations.putAll(nameLocalizations)
        definition.descriptionLocalizations.putAll(descriptionLocalizations)

        return definition
    }
}
