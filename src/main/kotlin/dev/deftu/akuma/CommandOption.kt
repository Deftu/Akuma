package dev.deftu.akuma

public open class CommandOption(
    public val type: OptionKind,
    public val name: String,
    public val description: String?,
    public val isRequired: Boolean,
) {
    public val nameLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()
    public val descriptionLocalizations: MutableMap<AkumaLocale, String> = mutableMapOf()

    public open class AutoCompletingCommandOption(
        type: OptionKind,
        name: String,
        description: String?,
        isRequired: Boolean,
        public val choices: List<AkumaChoice> = emptyList(),
        public val isAutoComplete: Boolean = false,
        public val autoComplete: (suspend (AutoCompleteContext) -> Unit)? = null,
    ) : CommandOption(type, name, description, isRequired)

    public class StringCommandOption(
        name: String,
        description: String?,
        isRequired: Boolean,
        choices: List<AkumaChoice>,
        isAutoComplete: Boolean,
        autoComplete: (suspend (AutoCompleteContext) -> Unit)?,
        public val minLength: Int? = null,
        public val maxLength: Int? = null,
    ) : AutoCompletingCommandOption(OptionKind.STRING, name, description, isRequired, choices, isAutoComplete, autoComplete)

    public class NumberCommandOption(
        type: OptionKind,
        name: String,
        description: String?,
        isRequired: Boolean,
        choices: List<AkumaChoice> = emptyList(),
        isAutoComplete: Boolean,
        autoComplete: (suspend (AutoCompleteContext) -> Unit)? = null,
        public val minValue: Double? = null,
        public val maxValue: Double? = null
    ) : AutoCompletingCommandOption(type, name, description, isRequired, choices, isAutoComplete, autoComplete)

    public class ChannelCommandOption(
        name: String,
        description: String?,
        isRequired: Boolean,
        public val channelTypes: Set<AkumaChannelType> = emptySet(),
    ) : CommandOption(OptionKind.CHANNEL, name, description, isRequired)
}
