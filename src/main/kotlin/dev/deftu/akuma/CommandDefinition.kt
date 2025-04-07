package dev.deftu.akuma

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

public data class CommandDefinition(
    val name: String,
    val description: String?,
    val options: List<CommandOption>,
    val children: List<CommandDefinition> = emptyList(),
    val groups: List<CommandGroupDefinition> = emptyList(),
    var isNsfw: Boolean = false,
    var isGuildOnly: Boolean = false,
    var defaultGuildPermissions: DefaultMemberPermissions = DefaultMemberPermissions.ENABLED,
    val action: (suspend CommandContext.() -> Unit)? = null
) {

    private val sortedOptions: List<CommandOption>
        get() = options.sortedBy { !it.isRequired }

    public fun asData(): SlashCommandData {
        return Commands.slash(this.name, this.description ?: "No description provided").apply {
            this.isNSFW = this@CommandDefinition.isNsfw
            this.isGuildOnly = this@CommandDefinition.isGuildOnly
            this.defaultPermissions = this@CommandDefinition.defaultGuildPermissions

            this@CommandDefinition.sortedOptions.map(CommandOption::asData).forEach(this::addOptions)
            this@CommandDefinition.children.map(CommandDefinition::asSubData).forEach(this::addSubcommands)
            this@CommandDefinition.groups.map(CommandGroupDefinition::asData).forEach(this::addSubcommandGroups)
        }
    }

    public fun asSubData(): SubcommandData {
        return SubcommandData(this.name, this.description ?: "No description provided").apply {
            this@CommandDefinition.sortedOptions.map(CommandOption::asData).forEach(this::addOptions)
        }
    }

}
