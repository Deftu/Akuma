package dev.deftu.akuma

public sealed class AkumaComponent {
    public class Button(
        public val customId: String,
        public val label: String?,
        public val style: AkumaButtonStyle,
        public val isDisabled: Boolean,
        public val emoji: String?,
    ) : AkumaComponent(), AkumaSectionAccessory

    public class StringSelect(
        public val customId: String,
        public val placeholder: String?,
        public val minValues: Int,
        public val maxValues: Int,
        public val options: List<Option>,
    ) : AkumaComponent() {
        public class Option(
            public val label: String,
            public val value: String,
            public val description: String?,
            public val isDefault: Boolean,
            public val emoji: String?,
        )
    }

    public class EntitySelect(
        public val customId: String,
        public val entityTypes: Set<AkumaEntitySelectType>,
        public val channelTypes: Set<AkumaChannelType>,
        public val placeholder: String?,
        public val minValues: Int,
        public val maxValues: Int,
    ) : AkumaComponent()
}
