package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaComponent
import dev.deftu.akuma.AkumaContainerChild
import dev.deftu.akuma.AkumaLayoutComponent
import dev.deftu.akuma.AkumaSectionAccessory
import dev.deftu.akuma.AkumaSectionContent
import dev.deftu.akuma.AkumaSeparatorSpacing
import dev.deftu.akuma.AkumaTopLevelComponent
import dev.deftu.akuma.LayoutComponentBuilder
import dev.kord.common.Color
import dev.kord.common.entity.SeparatorSpacingSize
import dev.kord.rest.builder.component.AccessoryComponentBuilder
import dev.kord.rest.builder.component.ContainerBuilder
import dev.kord.rest.builder.component.ContainerComponentBuilder
import dev.kord.rest.builder.component.MediaGalleryBuilder
import dev.kord.rest.builder.component.MediaGalleryItemBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.component.SectionBuilder
import dev.kord.rest.builder.component.SeparatorBuilder
import dev.kord.rest.builder.component.TextDisplayBuilder
import dev.kord.rest.builder.component.ThumbnailBuilder

internal fun Array<out LayoutComponentBuilder>.toKordTopLevelComponents(): List<MessageComponentBuilder> {
    return map { it.build().toKordTopLevel() }
}

internal fun AkumaTopLevelComponent.toKordTopLevel(): MessageComponentBuilder {
    return when (this) {
        is AkumaLayoutComponent.Container -> toKord()
        is AkumaLayoutComponent.Section -> toKord()
        is AkumaLayoutComponent.TextDisplay -> toKord()
        is AkumaLayoutComponent.MediaGallery -> toKord()
        is AkumaLayoutComponent.Separator -> toKord()
    }
}

internal fun AkumaContainerChild.toKordChild(): ContainerComponentBuilder {
    return when (this) {
        is AkumaLayoutComponent.Section -> toKord()
        is AkumaLayoutComponent.TextDisplay -> toKord()
        is AkumaLayoutComponent.MediaGallery -> toKord()
        is AkumaLayoutComponent.Separator -> toKord()
    }
}

internal fun AkumaSectionContent.toKordContent(): TextDisplayBuilder {
    return when (this) {
        is AkumaLayoutComponent.TextDisplay -> toKord()
    }
}

internal fun AkumaSectionAccessory.toKordAccessory(): AccessoryComponentBuilder {
    return when (this) {
        is AkumaLayoutComponent.Thumbnail -> toKord()
        is AkumaComponent.Button -> toKord()
    }
}

internal fun AkumaLayoutComponent.Container.toKord(): ContainerBuilder {
    return ContainerBuilder().apply {
        components = this@toKord.components.map { it.toKordChild() }.toMutableList()
        this@toKord.accentColor?.let { accentColor = Color(it) }
        if (isSpoiler) spoiler = true
    }
}

internal fun AkumaLayoutComponent.Section.toKord(): SectionBuilder {
    return SectionBuilder().apply {
        accessory = this@toKord.accessory.toKordAccessory()
        components.addAll(this@toKord.content.map { it.toKordContent() })
    }
}

internal fun AkumaLayoutComponent.TextDisplay.toKord(): TextDisplayBuilder {
    return TextDisplayBuilder().apply { content = this@toKord.content }
}

internal fun AkumaLayoutComponent.MediaGallery.toKord(): MediaGalleryBuilder {
    return MediaGalleryBuilder().apply {
        this@toKord.items.forEach { item ->
            items.add(MediaGalleryItemBuilder(item.url).apply {
                description = item.description
                if (item.isSpoiler) spoiler = true
            })
        }
    }
}

internal fun AkumaLayoutComponent.Separator.toKord(): SeparatorBuilder {
    return SeparatorBuilder().apply {
        divider = isDivider
        spacing = this@toKord.spacing.toKord()
    }
}

internal fun AkumaSeparatorSpacing.toKord(): SeparatorSpacingSize {
    return when (this) {
        AkumaSeparatorSpacing.SMALL -> SeparatorSpacingSize.Small
        AkumaSeparatorSpacing.LARGE -> SeparatorSpacingSize.Large
    }
}

internal fun AkumaLayoutComponent.Thumbnail.toKord(): ThumbnailBuilder {
    return ThumbnailBuilder().apply {
        url = this@toKord.url
        description = this@toKord.description
        if (isSpoiler) spoiler = true
    }
}
