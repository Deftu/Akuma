package dev.deftu.akuma

// Discord user-context commands skip descriptions and options entirely -- kept as its own
// builder rather than a mode on `CommandBuilder` so that inapplicable surface (description,
// options, subcommands, groups) is structurally absent instead of silently ignored.
public class UserCommandBuilder(private val name: String) {

    private var action: (suspend CommandContext.() -> Unit)? = null

    public val nameLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()

    public var isNsfw: Boolean = false
    public var isGuildOnly: Boolean = false
    public var defaultGuildPermissions: AkumaDefaultPermissions = AkumaDefaultPermissions.ENABLED

    public fun action(block: suspend CommandContext.() -> Unit) {
        action = block
    }

    public fun build(): CommandDefinition {
        val definition = CommandDefinition(
            name = name,
            description = null,
            options = emptyList(),
            isNsfw = isNsfw,
            isGuildOnly = isGuildOnly,
            defaultGuildPermissions = defaultGuildPermissions,
            action = action,
            type = AkumaCommandType.USER
        )

        definition.nameLocalizations.putAll(nameLocalizations)

        return definition
    }

}
