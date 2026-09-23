package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaChoice
import dev.deftu.akuma.AutoCompleteContext
import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.interaction.AutoCompleteInteraction

public class KordAutoCompleteContext(public val interaction: AutoCompleteInteraction) : AutoCompleteContext {
    override val focusedName: String
        get() = interaction.command.options.entries.first { it.value.focused }.key

    override val focusedValue: String get() = interaction.focusedOption.value

    override suspend fun replyChoices(choices: List<AkumaChoice>) {
        interaction.suggestString {
            choices.forEach { choice(it.name, it.value) {} }
        }
    }
}
