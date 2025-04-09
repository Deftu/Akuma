package dev.deftu.akuma

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

public class CommandBuilder(private val name: String) {

    private var parent: CommandBuilder? = null
    private val children = mutableListOf<CommandBuilder>()
    private val groups = mutableListOf<CommandGroupDefinition>()

    private val options = mutableListOf<CommandOption>()
    private var action: (suspend CommandContext.() -> Unit)? = null

    public var description: String? = null

    public val nameLocalizations: MutableMap<DiscordLocale, String> = mutableMapOf()

    public val descriptionLocalizations: MutableMap<DiscordLocale, String> = mutableMapOf()

    public var isNsfw: Boolean = false
        set(value) {
            if (parent != null) {
                throw IllegalStateException("Cannot set isNsfw on a child command")
            }

            field = value
        }

    public var isGuildOnly: Boolean = false
        set(value) {
            if (parent != null) {
                throw IllegalStateException("Cannot set isGuildOnly on a child command")
            }

            field = value
        }

    public var defaultGuildPermissions: DefaultMemberPermissions = DefaultMemberPermissions.ENABLED
        set(value) {
            if (parent != null) {
                throw IllegalStateException("Cannot set defaultGuildPermissions on a child command")
            }

            field = value
        }

    public fun subcommand(name: String, block: CommandBuilder.() -> Unit) {
        if (parent != null) {
            throw IllegalStateException("Cannot add a child command to a child command")
        }

        children.add(CommandBuilder(name).apply {
            this.parent = this@CommandBuilder
        }.apply(block))
    }

    public fun subcommand(name: String, description: String? = null, block: CommandBuilder.() -> Unit) {
        if (parent != null) {
            throw IllegalStateException("Cannot add a child command to a child command")
        }

        children.add(CommandBuilder(name).apply {
            this.parent = this@CommandBuilder.parent
            this.description = description
            block()
        })
    }

    public fun group(name: String, block: CommandGroupBuilder.() -> Unit) {
        groups.add(CommandGroupBuilder(name).apply(block).build())
    }

    public fun group(name: String, description: String? = null, block: CommandGroupBuilder.() -> Unit) {
        groups.add(CommandGroupBuilder(name).apply {
            this.description = description
            block()
        }.build())
    }

    public fun register(contributor: CommandContributor<CommandBuilder>) {
        contributor.register(this)
    }

    public fun action(block: suspend CommandContext.() -> Unit) {
        action = block
    }

    public fun string(name: String, description: String? = null, block: OptionBuilder.StringOption.() -> Unit) {
        options.add(OptionBuilder.StringOption(name, description).apply(block).build())
    }

    public fun integer(name: String, description: String? = null, block: OptionBuilder.IntOption.() -> Unit) {
        options.add(OptionBuilder.IntOption(name, description).apply(block).build())
    }

    public fun boolean(name: String, description: String? = null, block: OptionBuilder.BooleanOption.() -> Unit) {
        options.add(OptionBuilder.BooleanOption(name, description).apply(block).build())
    }

    public fun user(name: String, description: String? = null, block: OptionBuilder.UserOption.() -> Unit) {
        options.add(OptionBuilder.UserOption(name, description).apply(block).build())
    }

    public fun role(name: String, description: String? = null, block: OptionBuilder.RoleOption.() -> Unit) {
        options.add(OptionBuilder.RoleOption(name, description).apply(block).build())
    }

    public fun channel(name: String, description: String? = null, block: OptionBuilder.ChannelOption.() -> Unit) {
        options.add(OptionBuilder.ChannelOption(name, description).apply(block).build())
    }

    public fun mentionable(name: String, description: String? = null, block: OptionBuilder.MentionableOption.() -> Unit) {
        options.add(OptionBuilder.MentionableOption(name, description).apply(block).build())
    }

    public fun number(name: String, description: String? = null, block: OptionBuilder.NumberOption.() -> Unit) {
        options.add(OptionBuilder.NumberOption(name, description).apply(block).build())
    }

    public fun attachment(name: String, description: String? = null, block: OptionBuilder.AttachmentOption.() -> Unit) {
        options.add(OptionBuilder.AttachmentOption(name, description).apply(block).build())
    }

    public fun build(): CommandDefinition {
        val definition = CommandDefinition(
            name = name,
            description = description,
            options = options,
            children = children.map(CommandBuilder::build),
            groups = groups,
            isNsfw = isNsfw,
            isGuildOnly = isGuildOnly,
            defaultGuildPermissions = defaultGuildPermissions,
            action = action
        )

        definition.nameLocalizations.putAll(nameLocalizations)
        definition.descriptionLocalizations.putAll(descriptionLocalizations)

        return definition
    }

}

public fun JDA.subcommand(name: String, block: CommandBuilder.() -> Unit): CommandDefinition {
    val command = CommandBuilder(name).apply(block).build()
    updateCommands().addCommands(command.asData()).queue()
    CommandListener.getOrRegister(this).commands.add(command)
    return command
}
