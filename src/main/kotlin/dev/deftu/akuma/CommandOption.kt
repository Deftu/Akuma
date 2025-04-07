package dev.deftu.akuma

import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.util.EnumSet

public data class CommandOption(
    val type: OptionType,
    val name: String,
    val description: String?,
    val isRequired: Boolean,
    val choices: List<Command.Choice> = emptyList(),
    val isAutoComplete: Boolean = false,
    val autoComplete: (suspend (CommandAutoCompleteInteractionEvent) -> Unit)? = null,
    val channelTypes: EnumSet<ChannelType> = EnumSet.noneOf(ChannelType::class.java),
) {

    public companion object {

        public val autoCompletingTypes: Set<OptionType> = setOf(
            OptionType.NUMBER,
            OptionType.STRING,
            OptionType.INTEGER
        )

    }

    public fun asData(): OptionData {
        return OptionData(type, name, description ?: "No description provided", isRequired).apply {
            when (this@CommandOption.type) {
                in autoCompletingTypes -> {
                    if (this@CommandOption.isAutoComplete) {
                        this.isAutoComplete = true
                    }
                }

                OptionType.CHANNEL -> {
                    this.setChannelTypes(this@CommandOption.channelTypes)
                }

                else -> {} // no-op
            }
        }
    }

}
