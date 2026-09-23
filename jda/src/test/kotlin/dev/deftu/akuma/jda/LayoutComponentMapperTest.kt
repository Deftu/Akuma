package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaSeparatorSpacing
import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ContainerBuilder
import dev.deftu.akuma.MediaGalleryBuilder
import dev.deftu.akuma.SectionBuilder
import dev.deftu.akuma.SeparatorBuilder
import dev.deftu.akuma.TextDisplayBuilder
import net.dv8tion.jda.api.components.separator.Separator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LayoutComponentMapperTest {

    @Test
    fun `text display maps content`() {
        val textDisplay = TextDisplayBuilder("Hello, world!").build().toJda()
        assertEquals("Hello, world!", textDisplay.content)
    }

    @Test
    fun `media gallery maps items in order`() {
        val gallery = MediaGalleryBuilder().apply {
            item("https://example.com/a.png")
            item("https://example.com/b.png", description = "b", isSpoiler = true)
        }.build().toJda()

        assertEquals(
            listOf("https://example.com/a.png", "https://example.com/b.png"),
            gallery.items.map { it.url }
        )
        assertEquals("b", gallery.items[1].description)
        assertTrue(gallery.items[1].isSpoiler)
    }

    @Test
    fun `separator maps divider and spacing`() {
        val separator = SeparatorBuilder().apply {
            isDivider = false
            spacing = AkumaSeparatorSpacing.LARGE
        }.build().toJda()

        assertFalse(separator.isDivider)
        assertEquals(Separator.Spacing.LARGE, separator.spacing)
    }

    @Test
    fun `section maps text display content and thumbnail accessory`() {
        val section = SectionBuilder().apply {
            textDisplay("Body")
            thumbnail("https://example.com/thumb.png") {
                description = "A thumbnail"
            }
        }.build().toJda()

        assertEquals(1, section.contentComponents.size)
        val accessory = section.accessory.asThumbnail()
        assertEquals("https://example.com/thumb.png", accessory.url)
        assertEquals("A thumbnail", accessory.description)
    }

    @Test
    fun `section maps button accessory`() {
        val section = SectionBuilder().apply {
            textDisplay("Body")
            button("btn:confirm") {
                label = "Confirm"
            }
        }.build().toJda()

        val accessory = section.accessory.asButton()
        assertEquals("btn:confirm", accessory.customId)
        assertEquals("Confirm", accessory.label)
    }

    @Test
    fun `container maps mixed children in order with accent color and spoiler`() {
        val container = ContainerBuilder().apply {
            accentColor = 0xFF0000
            isSpoiler = true
            textDisplay("Heading")
            section {
                textDisplay("Body")
                thumbnail("https://example.com/thumb.png")
            }
            mediaGallery {
                item("https://example.com/a.png")
            }
            separator()
        }.build().toJda()

        assertEquals(0xFF0000, container.accentColorRaw)
        assertTrue(container.isSpoiler)
        assertEquals(4, container.components.size)
        assertEquals("Heading", container.components[0].asTextDisplay().content)
        assertTrue(container.components[2].asMediaGallery().items.isNotEmpty())
    }

}
