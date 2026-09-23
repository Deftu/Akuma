package dev.deftu.akuma

public class ContainerBuilder : LayoutComponentBuilder {
    public var accentColor: Int? = null
    public var isSpoiler: Boolean = false

    private val children = mutableListOf<() -> AkumaContainerChild>()

    public fun section(block: SectionBuilder.() -> Unit): ContainerBuilder = apply {
        val builder = SectionBuilder().apply(block)
        children.add(builder::build)
    }

    public fun textDisplay(content: String): ContainerBuilder = apply {
        children.add { AkumaLayoutComponent.TextDisplay(content) }
    }

    public fun mediaGallery(block: MediaGalleryBuilder.() -> Unit): ContainerBuilder = apply {
        val builder = MediaGalleryBuilder().apply(block)
        children.add(builder::build)
    }

    public fun separator(block: SeparatorBuilder.() -> Unit = {}): ContainerBuilder = apply {
        val builder = SeparatorBuilder().apply(block)
        children.add(builder::build)
    }

    override fun build(): AkumaLayoutComponent.Container {
        check(children.isNotEmpty()) { "Container requires at least one component" }
        return AkumaLayoutComponent.Container(children.map { it() }, accentColor, isSpoiler)
    }
}
