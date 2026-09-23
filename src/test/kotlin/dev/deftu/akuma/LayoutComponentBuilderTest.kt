package dev.deftu.akuma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LayoutComponentBuilderTest {

    @Test
    fun `text display carries its content`() {
        val textDisplay = TextDisplayBuilder("Hello, world!").build()
        assertEquals("Hello, world!", textDisplay.content)
    }

    @Test
    fun `media gallery collects items in order`() {
        val gallery = MediaGalleryBuilder().apply {
            item("https://example.com/a.png")
            item("https://example.com/b.png", description = "b", isSpoiler = true)
        }.build()

        assertEquals(
            listOf("https://example.com/a.png", "https://example.com/b.png"),
            gallery.items.map { it.url }
        )
        assertEquals("b", gallery.items[1].description)
        assertTrue(gallery.items[1].isSpoiler)
    }

    @Test
    fun `media gallery requires at least one item`() {
        assertThrows(IllegalStateException::class.java) {
            MediaGalleryBuilder().build()
        }
    }

    @Test
    fun `separator defaults to a small divider`() {
        val separator = SeparatorBuilder().build()
        assertTrue(separator.isDivider)
        assertEquals(AkumaSeparatorSpacing.SMALL, separator.spacing)
    }

    @Test
    fun `separator can be an invisible large spacer`() {
        val separator = SeparatorBuilder().apply {
            isDivider = false
            spacing = AkumaSeparatorSpacing.LARGE
        }.build()

        assertFalse(separator.isDivider)
        assertEquals(AkumaSeparatorSpacing.LARGE, separator.spacing)
    }

    @Test
    fun `section with a thumbnail accessory`() {
        val section = SectionBuilder().apply {
            textDisplay("Some text")
            thumbnail("https://example.com/thumb.png") {
                description = "A thumbnail"
            }
        }.build()

        assertEquals(1, section.content.size)
        val accessory = section.accessory as AkumaLayoutComponent.Thumbnail
        assertEquals("https://example.com/thumb.png", accessory.url)
        assertEquals("A thumbnail", accessory.description)
    }

    @Test
    fun `section with a button accessory`() {
        val section = SectionBuilder().apply {
            textDisplay("Some text")
            button("btn:confirm") {
                label = "Confirm"
            }
        }.build()

        val accessory = section.accessory as AkumaComponent.Button
        assertEquals("btn:confirm", accessory.customId)
        assertEquals("Confirm", accessory.label)
    }

    @Test
    fun `section requires at least one text display`() {
        assertThrows(IllegalStateException::class.java) {
            SectionBuilder().apply {
                thumbnail("https://example.com/thumb.png")
            }.build()
        }
    }

    @Test
    fun `section requires an accessory`() {
        assertThrows(IllegalStateException::class.java) {
            SectionBuilder().apply {
                textDisplay("Some text")
            }.build()
        }
    }

    @Test
    fun `container collects mixed children in order`() {
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
        }.build()

        assertEquals(0xFF0000, container.accentColor)
        assertTrue(container.isSpoiler)
        assertEquals(4, container.components.size)
        assertTrue(container.components[0] is AkumaLayoutComponent.TextDisplay)
        assertTrue(container.components[1] is AkumaLayoutComponent.Section)
        assertTrue(container.components[2] is AkumaLayoutComponent.MediaGallery)
        assertTrue(container.components[3] is AkumaLayoutComponent.Separator)
    }

    @Test
    fun `container requires at least one component`() {
        assertThrows(IllegalStateException::class.java) {
            ContainerBuilder().build()
        }
    }

}
