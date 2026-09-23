package dev.deftu.akuma

public class ThumbnailBuilder(public var url: String) {
    public var description: String? = null
    public var isSpoiler: Boolean = false

    public fun build(): AkumaLayoutComponent.Thumbnail {
        return AkumaLayoutComponent.Thumbnail(url, description, isSpoiler)
    }
}
