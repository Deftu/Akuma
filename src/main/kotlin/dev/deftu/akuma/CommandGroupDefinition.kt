package dev.deftu.akuma

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

public open class CommandGroupDefinition(
    public val name: String,
    public val description: String?,
    public val children: List<CommandDefinition> = emptyList(),
) {

    public val nameLocalizations: MutableMap<DiscordLocale, String> = mutableMapOf()
    public val descriptionLocalizations: MutableMap<DiscordLocale, String> = mutableMapOf()

    public fun asData(): SubcommandGroupData {
        return SubcommandGroupData(this.name, this.description ?: "No description provided").apply(::applyData)
    }

    protected open fun applyData(data: SubcommandGroupData) {
        data.setNameLocalizations(nameLocalizations)
        data.setDescriptionLocalizations(descriptionLocalizations)

        this@CommandGroupDefinition.children.map(CommandDefinition::asSubData).forEach(data::addSubcommands)
    }

}
