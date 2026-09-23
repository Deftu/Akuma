package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaTextInput
import dev.deftu.akuma.AkumaTextInputStyle
import dev.deftu.akuma.ModalDefinition
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.modals.Modal

public fun AkumaTextInputStyle.toJda(): TextInputStyle {
    return when (this) {
        AkumaTextInputStyle.SHORT -> TextInputStyle.SHORT
        AkumaTextInputStyle.PARAGRAPH -> TextInputStyle.PARAGRAPH
    }
}

// JDA 6.x's `TextInput` is a `LabelChildComponent`, not a top-level modal component on its own
// -- it has to be wrapped in a `Label` to go into `Modal.Builder.addComponents(...)`.
public fun AkumaTextInput.toJda(): Label {
    val builder = TextInput.create(name, style.toJda()).setRequired(isRequired)
    placeholder?.let(builder::setPlaceholder)
    value?.let(builder::setValue)
    minLength?.let(builder::setMinLength)
    maxLength?.let(builder::setMaxLength)
    return Label.of(label, builder.build())
}

public fun ModalDefinition.toJda(): Modal {
    val builder = Modal.create(customId, title)
    inputs.forEach { builder.addComponents(it.toJda()) }
    return builder.build()
}
