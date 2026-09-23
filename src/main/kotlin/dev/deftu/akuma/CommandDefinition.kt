package dev.deftu.akuma

public open class CommandDefinition(
    public val name: String,
    public val description: String?,
    public val options: List<CommandOption>,
    public val children: List<CommandDefinition> = emptyList(),
    public val groups: List<CommandGroupDefinition> = emptyList(),
    public var isNsfw: Boolean = false,
    public var isGuildOnly: Boolean = false,
    public var defaultGuildPermissions: AkumaDefaultPermissions = AkumaDefaultPermissions.ENABLED,
    public val action: (suspend CommandContext.() -> Unit)? = null,
    public val type: AkumaCommandType = AkumaCommandType.SLASH
) {
    public val sortedOptions: List<CommandOption>
        get() = options.sortedBy { !it.isRequired }

    public val nameLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()
    public val descriptionLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()
}
