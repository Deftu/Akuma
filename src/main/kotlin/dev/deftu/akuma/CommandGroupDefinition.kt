package dev.deftu.akuma

public open class CommandGroupDefinition(
    public val name: String,
    public val description: String?,
    public val children: List<CommandDefinition> = emptyList(),
) {
    public val nameLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()
    public val descriptionLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()
}
