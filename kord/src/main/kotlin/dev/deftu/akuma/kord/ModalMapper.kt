package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaTextInput
import dev.deftu.akuma.AkumaTextInputStyle
import dev.deftu.akuma.ModalDefinition
import dev.kord.common.entity.TextInputStyle
import dev.kord.rest.builder.interaction.ModalBuilder as KordModalBuilder

public fun AkumaTextInputStyle.toKord(): TextInputStyle {
    return when (this) {
        AkumaTextInputStyle.SHORT -> TextInputStyle.Short
        AkumaTextInputStyle.PARAGRAPH -> TextInputStyle.Paragraph
    }
}

// Kord wraps each text input in a `label(labelText) { textInput(...) }` block, same as the JDA
// adapter's `Label.of(labelText, textInput)` -- both frameworks model a modal's visible label as
// a component wrapping the input, not a property on the input itself.
public fun AkumaTextInput.applyTo(builder: KordModalBuilder) {
    builder.label(label) {
        textInput(style.toKord(), name) {
            placeholder = this@applyTo.placeholder
            value = this@applyTo.value
            required = isRequired

            // Discord's own bound for a modal text input's length, used as the missing side of
            // the range when only one of minLength/maxLength is set.
            if (minLength != null || maxLength != null) {
                allowedLength = (minLength ?: 0)..(maxLength ?: 4000)
            }
        }
    }
}

public fun ModalDefinition.applyTo(builder: KordModalBuilder) {
    inputs.forEach { it.applyTo(builder) }
}
