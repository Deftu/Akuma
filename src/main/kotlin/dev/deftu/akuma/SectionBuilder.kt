package dev.deftu.akuma

public class SectionBuilder : LayoutComponentBuilder {
    private val content = mutableListOf<TextDisplayBuilder>()
    private var accessory: (() -> AkumaSectionAccessory)? = null

    public fun textDisplay(content: String): SectionBuilder = apply {
        this.content.add(TextDisplayBuilder(content))
    }

    public fun thumbnail(url: String, block: ThumbnailBuilder.() -> Unit = {}): SectionBuilder = apply {
        val builder = ThumbnailBuilder(url).apply(block)
        accessory = builder::build
    }

    public fun button(customId: String, block: ComponentBuilder.Button.() -> Unit = {}): SectionBuilder = apply {
        val builder = ComponentBuilder.Button(customId).apply(block)
        accessory = builder::build
    }

    override fun build(): AkumaLayoutComponent.Section {
        check(content.isNotEmpty()) { "Section requires at least one text display" }
        val builtAccessory = checkNotNull(accessory) { "Section requires an accessory (thumbnail or button)" }
        return AkumaLayoutComponent.Section(content.map(TextDisplayBuilder::build), builtAccessory())
    }
}
