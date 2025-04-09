package dev.deftu.akuma

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

public open class CommandDefinition(
    public val name: String,
    public val description: String?,
    public val options: List<CommandOption>,
    public val children: List<CommandDefinition> = emptyList(),
    public val groups: List<CommandGroupDefinition> = emptyList(),
    public var isNsfw: Boolean = false,
    public var isGuildOnly: Boolean = false,
    public var defaultGuildPermissions: DefaultMemberPermissions = DefaultMemberPermissions.ENABLED,
    public val action: (suspend CommandContext.() -> Unit)? = null
) {

    private val sortedOptions: List<CommandOption>
        get() = options.sortedBy { !it.isRequired }

    public val nameLocalizations: MutableMap<DiscordLocale, String> = mutableMapOf()
    public val descriptionLocalizations: MutableMap<DiscordLocale, String> = mutableMapOf()

    public fun asData(): SlashCommandData {
        return Commands.slash(this.name, this.description ?: "No description provided").apply(::applyData)
    }

    public fun asSubData(): SubcommandData {
        return SubcommandData(this.name, this.description ?: "No description provided").apply(::applySubData)
    }

    protected open fun applyData(data: SlashCommandData) {
        data.isNSFW = this.isNsfw
        data.isGuildOnly = this.isGuildOnly
        data.defaultPermissions = this.defaultGuildPermissions

        data.setNameLocalizations(nameLocalizations)
        data.setDescriptionLocalizations(descriptionLocalizations)

        sortedOptions.map(CommandOption::asData).forEach(data::addOptions)
        children.map(CommandDefinition::asSubData).forEach(data::addSubcommands)
        groups.map(CommandGroupDefinition::asData).forEach(data::addSubcommandGroups)
    }

    protected open fun applySubData(data: SubcommandData) {
        data.setNameLocalizations(nameLocalizations)
        data.setDescriptionLocalizations(descriptionLocalizations)

        sortedOptions.map(CommandOption::asData).forEach(data::addOptions)
    }

}
