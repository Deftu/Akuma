package dev.deftu.akuma

public sealed interface ComponentBuilder {
    public val customId: String

    public fun build(): AkumaComponent

    public class Button(
        override val customId: String,
        public var label: String? = null,
    ) : ComponentBuilder {
        public var style: AkumaButtonStyle = AkumaButtonStyle.SECONDARY
        public var isDisabled: Boolean = false
        public var emoji: String? = null

        override fun build(): AkumaComponent.Button {
            return AkumaComponent.Button(customId, label, style, isDisabled, emoji)
        }
    }

    public class StringSelect(
        override val customId: String,
        public var placeholder: String? = null,
    ) : ComponentBuilder {
        public var minValues: Int = 1
        public var maxValues: Int = 1

        private val options = mutableListOf<AkumaComponent.StringSelect.Option>()

        public fun option(
            label: String,
            value: String,
            description: String? = null,
            isDefault: Boolean = false,
            emoji: String? = null,
        ): StringSelect = apply {
            options.add(AkumaComponent.StringSelect.Option(label, value, description, isDefault, emoji))
        }

        override fun build(): AkumaComponent.StringSelect {
            return AkumaComponent.StringSelect(customId, placeholder, minValues, maxValues, options.toList())
        }
    }

    public class EntitySelect(
        override val customId: String,
        public val entityTypes: Set<AkumaEntitySelectType>,
        public var placeholder: String? = null,
    ) : ComponentBuilder {
        public var minValues: Int = 1
        public var maxValues: Int = 1
        public val channelTypes: MutableSet<AkumaChannelType> = mutableSetOf()

        override fun build(): AkumaComponent.EntitySelect {
            return AkumaComponent.EntitySelect(customId, entityTypes, channelTypes, placeholder, minValues, maxValues)
        }
    }
}
