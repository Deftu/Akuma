package dev.deftu.akuma

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

public data class CommandGroupDefinition(
    val name: String,
    val description: String?,
    val children: List<CommandDefinition> = emptyList(),
) {

    public fun asData(): SubcommandGroupData {
        return SubcommandGroupData(this.name, this.description ?: "No description provided").apply {
            this@CommandGroupDefinition.children.map(CommandDefinition::asSubData).forEach(this::addSubcommands)
        }
    }

}
