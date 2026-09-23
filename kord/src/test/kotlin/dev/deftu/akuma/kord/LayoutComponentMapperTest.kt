package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaSeparatorSpacing
import dev.deftu.akuma.ContainerBuilder
import dev.deftu.akuma.MediaGalleryBuilder
import dev.deftu.akuma.SectionBuilder
import dev.deftu.akuma.SeparatorBuilder
import dev.deftu.akuma.TextDisplayBuilder
import dev.kord.common.entity.SeparatorSpacingSize
import dev.kord.rest.builder.component.ButtonBuilder
import dev.kord.rest.builder.component.SeparatorBuilder as KordSeparatorBuilder
import dev.kord.rest.builder.component.TextDisplayBuilder as KordTextDisplayBuilder
import dev.kord.rest.builder.component.ThumbnailBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LayoutComponentMapperTest {

    @Test
    fun `container carries its accent color, spoiler flag and children in order`() {
        val container = ContainerBuilder().apply {
            accentColor = 0xFF00FF
            isSpoiler = true
            textDisplay("hello")
            separator()
        }.build().toKord()

        assertEquals(0xFF00FF, container.accentColor?.rgb)
        assertEquals(true, container.spoiler)
        assertEquals(2, container.components?.size)
        assertTrue(container.components?.get(0) is KordTextDisplayBuilder)
        assertTrue(container.components?.get(1) is KordSeparatorBuilder)
    }

    @Test
    fun `section carries its text displays and a button accessory`() {
        val section = SectionBuilder().apply {
            textDisplay("line one")
            button("btn:go") { label = "Go" }
        }.build().toKord()

        assertEquals(1, section.components.size)
        assertEquals("line one", section.components.single().content)
        assertTrue(section.accessory is ButtonBuilder)
    }

    @Test
    fun `section carries a thumbnail accessory`() {
        val section = SectionBuilder().apply {
            textDisplay("line one")
            thumbnail("https://example.com/image.png") { description = "An image" }
        }.build().toKord()

        val accessory = section.accessory as ThumbnailBuilder
        assertEquals("https://example.com/image.png", accessory.url)
        assertEquals("An image", accessory.description)
    }

    @Test
    fun `media gallery carries each item's url, description and spoiler flag`() {
        val gallery = MediaGalleryBuilder().apply {
            item("https://example.com/a.png", description = "A", isSpoiler = true)
            item("https://example.com/b.png")
        }.build().toKord()

        assertEquals(2, gallery.items.size)
        assertEquals("https://example.com/a.png", gallery.items[0].url)
        assertEquals("A", gallery.items[0].description)
        assertEquals(true, gallery.items[0].spoiler)
    }

    @Test
    fun `separator maps divider and spacing`() {
        val separator = SeparatorBuilder().apply {
            isDivider = false
            spacing = AkumaSeparatorSpacing.LARGE
        }.build().toKord()

        assertEquals(false, separator.divider)
        assertEquals(SeparatorSpacingSize.Large, separator.spacing)
    }

    @Test
    fun `text display carries its content`() {
        val textDisplay = TextDisplayBuilder("hello world").build().toKord()

        assertEquals("hello world", textDisplay.content)
    }

}
