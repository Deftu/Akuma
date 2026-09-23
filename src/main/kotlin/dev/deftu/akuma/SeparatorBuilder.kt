package dev.deftu.akuma

public class SeparatorBuilder : LayoutComponentBuilder {
    public var isDivider: Boolean = true
    public var spacing: AkumaSeparatorSpacing = AkumaSeparatorSpacing.SMALL

    override fun build(): AkumaLayoutComponent.Separator {
        return AkumaLayoutComponent.Separator(isDivider, spacing)
    }
}
