package dev.deftu.akuma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ModalBuilderTest {

    @Test
    fun `modal collects text inputs with defaults`() {
        val modal = ModalBuilder("modal:feedback", "Send feedback").apply {
            textInput("summary", "Summary")
        }.build()

        assertEquals("modal:feedback", modal.customId)
        assertEquals("Send feedback", modal.title)

        val input = modal.inputs.single()
        assertEquals("summary", input.name)
        assertEquals("Summary", input.label)
        assertEquals(AkumaTextInputStyle.SHORT, input.style)
        assertTrue(input.isRequired)
    }

    @Test
    fun `text input accepts overrides`() {
        val modal = ModalBuilder("modal:feedback", "Send feedback").apply {
            textInput("details", "Details") {
                style = AkumaTextInputStyle.PARAGRAPH
                placeholder = "What went wrong?"
                value = "prefilled"
                isRequired = false
                minLength = 10
                maxLength = 500
            }
        }.build()

        val input = modal.inputs.single()
        assertEquals(AkumaTextInputStyle.PARAGRAPH, input.style)
        assertEquals("What went wrong?", input.placeholder)
        assertEquals("prefilled", input.value)
        assertEquals(false, input.isRequired)
        assertEquals(10, input.minLength)
        assertEquals(500, input.maxLength)
    }

}
