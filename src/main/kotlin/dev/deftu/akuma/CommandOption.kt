package dev.deftu.akuma

import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.util.EnumSet

public open class CommandOption(
    public val type: OptionType,
    public val name: String,
    public val description: String?,
    public val isRequired: Boolean,
) {
    public fun asData(): OptionData {
        return OptionData(type, name, description ?: "No description provided", isRequired).apply(::applyData)
    }

    protected open fun applyData(data: OptionData) {
    }

    public open class AutoCompletingCommandOption(
        type: OptionType,
        name: String,
        description: String?,
        isRequired: Boolean,
        public val choices: List<Command.Choice> = emptyList(),
        public val isAutoComplete: Boolean = false,
        public val autoComplete: (suspend (CommandAutoCompleteInteractionEvent) -> Unit)? = null,
    ) : CommandOption(type, name, description, isRequired) {

        override fun applyData(data: OptionData) {
            super.applyData(data)
            data.isAutoComplete = true
        }

    }

    public class NumberCommandOption(
        type: OptionType,
        name: String,
        description: String?,
        isRequired: Boolean,
        choices: List<Command.Choice> = emptyList(),
        isAutoComplete: Boolean,
        autoComplete: (suspend (CommandAutoCompleteInteractionEvent) -> Unit)? = null,
        public val minValue: Double? = null,
        public val maxValue: Double? = null
    ) : AutoCompletingCommandOption(type, name, description, isRequired, choices, isAutoComplete, autoComplete) {

        override fun applyData(data: OptionData) {
            super.applyData(data)
            minValue?.let(data::setMinValue)
            maxValue?.let(data::setMaxValue)
        }

    }

    public class ChannelCommandOption(
        name: String,
        description: String?,
        isRequired: Boolean,
        public val channelTypes: EnumSet<ChannelType> = EnumSet.noneOf(ChannelType::class.java),
    ) : CommandOption(OptionType.CHANNEL, name, description, isRequired) {

        override fun applyData(data: OptionData) {
            super.applyData(data)
            data.setChannelTypes(channelTypes)
        }

    }

}
