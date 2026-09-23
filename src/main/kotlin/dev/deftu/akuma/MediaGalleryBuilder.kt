package dev.deftu.akuma

public class MediaGalleryBuilder : LayoutComponentBuilder {
    private val items = mutableListOf<AkumaLayoutComponent.MediaGallery.Item>()

    public fun item(url: String, description: String? = null, isSpoiler: Boolean = false): MediaGalleryBuilder = apply {
        items.add(AkumaLayoutComponent.MediaGallery.Item(url, description, isSpoiler))
    }

    override fun build(): AkumaLayoutComponent.MediaGallery {
        check(items.isNotEmpty()) { "MediaGallery requires at least one item" }
        return AkumaLayoutComponent.MediaGallery(items.toList())
    }
}
