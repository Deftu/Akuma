package dev.deftu.akuma

public class TextDisplayBuilder(public var content: String) : LayoutComponentBuilder {
    override fun build(): AkumaLayoutComponent.TextDisplay {
        return AkumaLayoutComponent.TextDisplay(content)
    }
}
