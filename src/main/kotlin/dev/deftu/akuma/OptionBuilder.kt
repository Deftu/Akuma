package dev.deftu.akuma

public sealed class AutoCompletingOptionBuilder {
    internal val choices = mutableListOf<AkumaChoice>()

    public abstract var isAutoComplete: Boolean
    public var autoComplete: (suspend (AutoCompleteContext) -> Unit)? = null

    public fun choices(vararg choices: AkumaChoice): AutoCompletingOptionBuilder = apply {
        this.choices.addAll(choices)
    }

    public fun choices(choices: Collection<AkumaChoice>): AutoCompletingOptionBuilder = apply {
        this.choices.addAll(choices)
    }

    public fun choice(choice: AkumaChoice): AutoCompletingOptionBuilder = apply {
        this.choices.add(choice)
    }

    public fun choice(name: String, value: String): AutoCompletingOptionBuilder = apply {
        this.choices.add(AkumaChoice(name, value))
    }

    public fun choice(name: String, value: Number): AutoCompletingOptionBuilder = apply {
        this.choices.add(AkumaChoice(name, value.toString()))
    }

    public fun choice(name: String, value: Boolean): AutoCompletingOptionBuilder = apply {
        this.choices.add(AkumaChoice(name, value.toString()))
    }

    public fun choice(value: String): AutoCompletingOptionBuilder = apply {
        this.choices.add(AkumaChoice(value, value))
    }

    public fun choice(value: Number): AutoCompletingOptionBuilder = apply {
        this.choices.add(AkumaChoice(value.toString(), value.toString()))
    }

    public fun choice(value: Boolean): AutoCompletingOptionBuilder = apply {
        this.choices.add(AkumaChoice(value.toString(), value.toString()))
    }

    public fun autocomplete(
        block: suspend (AutoCompleteContext) -> Unit,
    ): AutoCompletingOptionBuilder = apply {
        isAutoComplete = true
        autoComplete = block
    }
}

public sealed interface OptionBuilder {
    public val name: String
    public val description: String?
    public var isRequired: Boolean

    public val nameLocalizations: MutableMap<AkumaLocale, String>
    public val descriptionLocalizations: MutableMap<AkumaLocale, String>

    public fun required(value: Boolean): OptionBuilder = apply {
        isRequired = value
    }

    public fun required(): OptionBuilder = apply {
        isRequired = true
    }

    public fun build(): CommandOption

    public sealed class BasicOptionBuilder(
        public val type: OptionKind,
        override val name: String,
        override val description: String?,
        override var isRequired: Boolean = false,
    ) : OptionBuilder {
        override val nameLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()
        override val descriptionLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()

        override fun build(): CommandOption {
            val option = CommandOption(
                type = type,
                name = name,
                description = description,
                isRequired = isRequired,
            )

            option.nameLocalizations.putAll(nameLocalizations)
            option.descriptionLocalizations.putAll(descriptionLocalizations)

            return option
        }
    }

    public sealed class BasicAutoCompletingOptionBuilder(
        public val type: OptionKind,
        override val name: String,
        override val description: String?,
        override var isAutoComplete: Boolean,
        override var isRequired: Boolean = false,
    ) : OptionBuilder, AutoCompletingOptionBuilder() {
        override val nameLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()
        override val descriptionLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()

        override fun build(): CommandOption.AutoCompletingCommandOption {
            val option = CommandOption.AutoCompletingCommandOption(
                type = type,
                name = name,
                description = description,
                isRequired = isRequired,
                choices = choices,
                isAutoComplete = isAutoComplete,
                autoComplete = autoComplete,
            )

            option.nameLocalizations.putAll(nameLocalizations)
            option.descriptionLocalizations.putAll(descriptionLocalizations)

            return option
        }
    }

    public sealed class NumberOptionBuilder(
        type: OptionKind,
        override val name: String,
        override val description: String?,
        isAutoComplete: Boolean,
        override var isRequired: Boolean = false,
        public var minValue: Double? = null,
        public var maxValue: Double? = null,
    ) : BasicAutoCompletingOptionBuilder(type, name, description, isAutoComplete) {
        override fun build(): CommandOption.NumberCommandOption {
            val option = CommandOption.NumberCommandOption(
                type = type,
                name = name,
                description = description,
                isRequired = isRequired,
                isAutoComplete = isAutoComplete,
                autoComplete = autoComplete,
                minValue = minValue,
                maxValue = maxValue,
            )

            option.nameLocalizations.putAll(nameLocalizations)
            option.descriptionLocalizations.putAll(descriptionLocalizations)

            return option
        }
    }

    public class StringOption(
        name: String,
        description: String?,
        isAutoComplete: Boolean = false,
        public var minLength: Int? = null,
        public var maxLength: Int? = null,
    ) : BasicAutoCompletingOptionBuilder(OptionKind.STRING, name, description, isAutoComplete) {
        override fun build(): CommandOption.StringCommandOption {
            val option = CommandOption.StringCommandOption(
                name = name,
                description = description,
                isRequired = isRequired,
                isAutoComplete = isAutoComplete,
                choices = choices,
                autoComplete = autoComplete,
                minLength = minLength,
                maxLength = maxLength,
            )

            option.nameLocalizations.putAll(nameLocalizations)
            option.descriptionLocalizations.putAll(descriptionLocalizations)

            return option
        }
    }

    public class IntOption(
        name: String,
        description: String?,
        isAutoComplete: Boolean = false
    ) : NumberOptionBuilder(OptionKind.INTEGER, name, description, isAutoComplete)

    public class BooleanOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionKind.BOOLEAN, name, description)

    public class UserOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionKind.USER, name, description)

    public class ChannelOption(
        override val name: String,
        override val description: String?,
        override var isRequired: Boolean = false,
    ) : OptionBuilder {
        private val channelTypes = mutableSetOf<AkumaChannelType>()

        override val nameLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()
        override val descriptionLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()

        public fun channelTypes(vararg types: AkumaChannelType): ChannelOption = apply {
            channelTypes.addAll(types)
        }

        public fun channelTypes(types: Collection<AkumaChannelType>): ChannelOption = apply {
            channelTypes.addAll(types)
        }

        override fun build(): CommandOption.ChannelCommandOption {
            val option = CommandOption.ChannelCommandOption(
                name = name,
                description = description,
                isRequired = isRequired,
                channelTypes = channelTypes,
            )

            option.nameLocalizations.putAll(nameLocalizations)
            option.descriptionLocalizations.putAll(descriptionLocalizations)

            return option
        }
    }

    public class RoleOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionKind.ROLE, name, description)

    public class MentionableOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionKind.MENTIONABLE, name, description)

    public class NumberOption(
        name: String,
        description: String?,
        isAutoComplete: Boolean = false
    ) : NumberOptionBuilder(OptionKind.NUMBER, name, description, isAutoComplete)

    public class AttachmentOption(
        name: String,
        description: String?
    ) : BasicOptionBuilder(OptionKind.ATTACHMENT, name, description)
}
