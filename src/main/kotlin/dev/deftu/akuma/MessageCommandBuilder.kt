package dev.deftu.akuma

// See `UserCommandBuilder` -- same reasoning, targets a message instead of a user.
public class MessageCommandBuilder(private val name: String) {

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
            type = AkumaCommandType.MESSAGE
        )

        definition.nameLocalizations.putAll(nameLocalizations)

        return definition
    }

}
