package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaComponent
import dev.deftu.akuma.AkumaContainerChild
import dev.deftu.akuma.AkumaLayoutComponent
import dev.deftu.akuma.AkumaSectionAccessory
import dev.deftu.akuma.AkumaSectionContent
import dev.deftu.akuma.AkumaSeparatorSpacing
import dev.deftu.akuma.AkumaTopLevelComponent
import dev.deftu.akuma.LayoutComponentBuilder
import net.dv8tion.jda.api.components.MessageTopLevelComponent
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.container.ContainerChildComponent
import net.dv8tion.jda.api.components.mediagallery.MediaGallery
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem
import net.dv8tion.jda.api.components.section.Section
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent
import net.dv8tion.jda.api.components.section.SectionContentComponent
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.components.thumbnail.Thumbnail

internal fun Array<out LayoutComponentBuilder>.toTopLevelComponents(): List<MessageTopLevelComponent> {
    return map { it.build().toJdaTopLevel() }
}

internal fun AkumaTopLevelComponent.toJdaTopLevel(): MessageTopLevelComponent {
    return when (this) {
        is AkumaLayoutComponent.Container -> toJda()
        is AkumaLayoutComponent.Section -> toJda()
        is AkumaLayoutComponent.TextDisplay -> toJda()
        is AkumaLayoutComponent.MediaGallery -> toJda()
        is AkumaLayoutComponent.Separator -> toJda()
    }
}

internal fun AkumaContainerChild.toJdaChild(): ContainerChildComponent {
    return when (this) {
        is AkumaLayoutComponent.Section -> toJda()
        is AkumaLayoutComponent.TextDisplay -> toJda()
        is AkumaLayoutComponent.MediaGallery -> toJda()
        is AkumaLayoutComponent.Separator -> toJda()
    }
}

internal fun AkumaSectionContent.toJdaContent(): SectionContentComponent {
    return when (this) {
        is AkumaLayoutComponent.TextDisplay -> toJda()
    }
}

internal fun AkumaSectionAccessory.toJdaAccessory(): SectionAccessoryComponent {
    return when (this) {
        is AkumaLayoutComponent.Thumbnail -> toJda()
        is AkumaComponent.Button -> toJda()
    }
}

internal fun AkumaLayoutComponent.Container.toJda(): Container {
    var container = Container.of(components.map { it.toJdaChild() })
    accentColor?.let { container = container.withAccentColor(it) }
    if (isSpoiler) container = container.withSpoiler(true)
    return container
}

internal fun AkumaLayoutComponent.Section.toJda(): Section {
    return Section.of(accessory.toJdaAccessory(), content.map { it.toJdaContent() })
}

internal fun AkumaLayoutComponent.TextDisplay.toJda(): TextDisplay {
    return TextDisplay.of(content)
}

internal fun AkumaLayoutComponent.MediaGallery.toJda(): MediaGallery {
    return MediaGallery.of(items.map { it.toJda() })
}

internal fun AkumaLayoutComponent.MediaGallery.Item.toJda(): MediaGalleryItem {
    var item = MediaGalleryItem.fromUrl(url)
    description?.let { item = item.withDescription(it) }
    if (isSpoiler) item = item.withSpoiler(true)
    return item
}

internal fun AkumaLayoutComponent.Separator.toJda(): Separator {
    return Separator.create(isDivider, spacing.toJda())
}

internal fun AkumaSeparatorSpacing.toJda(): Separator.Spacing {
    return when (this) {
        AkumaSeparatorSpacing.SMALL -> Separator.Spacing.SMALL
        AkumaSeparatorSpacing.LARGE -> Separator.Spacing.LARGE
    }
}

internal fun AkumaLayoutComponent.Thumbnail.toJda(): Thumbnail {
    var thumbnail = Thumbnail.fromUrl(url)
    description?.let { thumbnail = thumbnail.withDescription(it) }
    if (isSpoiler) thumbnail = thumbnail.withSpoiler(true)
    return thumbnail
}
