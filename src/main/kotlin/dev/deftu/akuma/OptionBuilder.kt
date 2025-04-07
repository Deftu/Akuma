package dev.deftu.akuma

import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.EnumSet

public sealed class AutoCompletingOptionBuilder {

    internal val choices = mutableListOf<Command.Choice>()

    public abstract var isAutoComplete: Boolean
    public var autoComplete: (suspend (CommandAutoCompleteInteractionEvent) -> Unit)? = null

    public fun choices(vararg choices: Command.Choice): AutoCompletingOptionBuilder = apply {
        this.choices.addAll(choices)
    }

    public fun choices(choices: Collection<Command.Choice>): AutoCompletingOptionBuilder = apply {
        this.choices.addAll(choices)
    }

    public fun choice(choice: Command.Choice): AutoCompletingOptionBuilder = apply {
        this.choices.add(choice)
    }

    public fun choice(name: String, value: String): AutoCompletingOptionBuilder = apply {
        this.choices.add(Command.Choice(name, value))
    }

    public fun choice(name: String, value: Number): AutoCompletingOptionBuilder = apply {
        this.choices.add(Command.Choice(name, value.toString()))
    }

    public fun choice(name: String, value: Boolean): AutoCompletingOptionBuilder = apply {
        this.choices.add(Command.Choice(name, value.toString()))
    }

    public fun choice(value: String): AutoCompletingOptionBuilder = apply {
        this.choices.add(Command.Choice(value, value))
    }

    public fun choice(value: Number): AutoCompletingOptionBuilder = apply {
        this.choices.add(Command.Choice(value.toString(), value.toString()))
    }

    public fun choice(value: Boolean): AutoCompletingOptionBuilder = apply {
        this.choices.add(Command.Choice(value.toString(), value.toString()))
    }

    public fun autocomplete(
        block: suspend (CommandAutoCompleteInteractionEvent) -> Unit,
    ): AutoCompletingOptionBuilder = apply {
        isAutoComplete = true
        autoComplete = block
    }

}

public sealed interface OptionBuilder {

    public val name: String
    public val description: String?
    public var isRequired: Boolean

    public fun required(value: Boolean): OptionBuilder = apply {
        isRequired = value
    }

    public fun required(): OptionBuilder = apply {
        isRequired = true
    }

    public fun build(): CommandOption

    public sealed class BasicOptionBuilder(
        public val type: OptionType,
        override val name: String,
        override val description: String?,
        override var isRequired: Boolean = false,
    ) : OptionBuilder {

        override fun build(): CommandOption {
            return CommandOption(
                type = type,
                name = name,
                description = description,
                isRequired = isRequired,
            )
        }

    }

    public sealed class BasicAutoCompletingOptionBuilder(
        public val type: OptionType,
        override val name: String,
        override val description: String?,
        override var isAutoComplete: Boolean,
        override var isRequired: Boolean = false,
    ) : OptionBuilder, AutoCompletingOptionBuilder() {

        override fun build(): CommandOption.AutoCompletingCommandOption {
            return CommandOption.AutoCompletingCommandOption(
                type = type,
                name = name,
                description = description,
                isRequired = isRequired,
                choices = choices,
                isAutoComplete = isAutoComplete,
                autoComplete = autoComplete,
            )
        }

    }

    public sealed class NumberOptionBuilder(
        type: OptionType,
        override val name: String,
        override val description: String?,
        override var isRequired: Boolean = false,
        public var minValue: Double? = null,
        public var maxValue: Double? = null,
    ) : BasicAutoCompletingOptionBuilder(type, name, description, false) {

        override fun build(): CommandOption.NumberCommandOption {
            return CommandOption.NumberCommandOption(
                type = type,
                name = name,
                description = description,
                isRequired = isRequired,
                isAutoComplete = isAutoComplete,
                minValue = minValue,
                maxValue = maxValue,
            )
        }

    }

    public class StringOption(
        name: String,
        description: String?,
        isAutoComplete: Boolean = false
    ) : BasicAutoCompletingOptionBuilder(OptionType.STRING, name, description, isAutoComplete)

    public class IntOption(
        name: String,
        description: String?,
        isAutoComplete: Boolean = false
    ) : NumberOptionBuilder(OptionType.INTEGER, name, description, isAutoComplete)

    public class BooleanOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionType.BOOLEAN, name, description)

    public class UserOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionType.USER, name, description)

    public class ChannelOption(
        override val name: String,
        override val description: String?,
        override var isRequired: Boolean = false,
    ) : OptionBuilder {

        private val channelTypes = EnumSet.noneOf(ChannelType::class.java)

        public fun channelTypes(vararg types: ChannelType): ChannelOption = apply {
            channelTypes.addAll(types)
        }

        public fun channelTypes(types: Collection<ChannelType>): ChannelOption = apply {
            channelTypes.addAll(types)
        }

        public fun channelTypes(types: EnumSet<ChannelType>): ChannelOption = apply {
            channelTypes.addAll(types)
        }

        override fun build(): CommandOption.ChannelCommandOption {
            return CommandOption.ChannelCommandOption(
                name = name,
                description = description,
                isRequired = isRequired,
                channelTypes = channelTypes,
            )
        }

    }

    public class RoleOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionType.ROLE, name, description)

    public class MentionableOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionType.MENTIONABLE, name, description)

    public class NumberOption(
        name: String,
        description: String?,
        isAutoComplete: Boolean = false
    ) : NumberOptionBuilder(OptionType.NUMBER, name, description, isAutoComplete)

    public class AttachmentOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionType.ATTACHMENT, name, description)

}
